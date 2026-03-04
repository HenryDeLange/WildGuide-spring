package mywild.wildguide.framework.error;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ErrorConfig {
    
    /**
     * Custom response error handler, to prevent using the default "/error" endpoint, 
     * which masks the real error codes thrown from the services.
     */
    @RestControllerAdvice
    public static class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

        @Autowired
        private MessageSource messageSource;

        @ExceptionHandler(Exception.class)
        public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
            String url = null;
            if (request instanceof ServletWebRequest servletWebRequest) {
                log.debug("Caused By URL: {}", servletWebRequest.getRequest().getRequestURL());
                url = servletWebRequest.getRequest().getRequestURL().toString();
            }
            try {
                return super.handleException(ex, request);
            }
            catch (Exception notHandledException) {
                if (notHandledException instanceof AbstractAppException appException) {
                    String translatedMessage = messageSource.getMessage(
                        appException.getMessage(), null, appException.getMessage(), request.getLocale());
                    if (appException.getMessage().equals("file.not-found")) {
                        log.debug(translatedMessage);
                    }
                    else {
                        log.error(translatedMessage, appException);
                    }
                    ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
                    HttpStatus status = (responseStatus != null) ? responseStatus.code() : HttpStatus.INTERNAL_SERVER_ERROR;
                    return handleExceptionInternal(
                        appException,
                        getJsonBody(translatedMessage, appException.getLocalizedMessage(), url),
                        new HttpHeaders(),
                        status,
                        request);
                }
                else if (notHandledException instanceof DbActionExecutionException dbException) {
                    log.error(dbException.getMessage(), dbException);
                    String translatedMessage = messageSource.getMessage(
                        "db.error", null, dbException.getLocalizedMessage(), request.getLocale());
                    return handleExceptionInternal(dbException,
                        getJsonBody(translatedMessage, dbException.getLocalizedMessage(), url),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST,
                        request);
                }
                else  {
                    log.error(notHandledException.getMessage(), notHandledException);
                    return handleExceptionInternal(notHandledException,
                        notHandledException.getMessage(),
                        new HttpHeaders(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request);
                }
            }
        }

        @Override
        @Nullable
        protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex, @Nullable Object body, @NonNull HttpHeaders headers, 
                @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {
            if (ex instanceof HttpMessageNotReadableException notReadableException) {
                return createResponseEntity(notReadableException.getMessage().replace("\"", "\\\""), headers, statusCode, request);
            }
            else if (ex instanceof DbActionExecutionException dbException) {
                // Don't show the database details to the frontend client
                return createResponseEntity(dbException.getClass().getSimpleName(), headers, statusCode, request);
            }
            return super.handleExceptionInternal(ex, body, headers, statusCode, request);
        }

        @Override
        @NonNull
        protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, @NonNull HttpHeaders headers,
                @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {
            return new ResponseEntity<>(body, headers, statusCode);
        }

        private String getJsonBody(String reason, String details, String url) {
            return String.format("{ \"reason\": %s, \"details\": %s, \"url\": %s }", 
                escapeJsonString(reason), 
                escapeJsonString(details),
                escapeJsonString(url));
        }

        private String escapeJsonString(String value) {
            if (value == null) {
                return "\"\"";
            }
            return "\""
                + value
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                + "\"";
        }

    }

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
