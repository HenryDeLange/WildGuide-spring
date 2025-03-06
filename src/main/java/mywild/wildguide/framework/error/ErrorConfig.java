package mywild.wildguide.framework.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
    public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

        @Autowired
        private MessageSource messageSource;

        @ExceptionHandler(Exception.class)
        public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
            try {
                return super.handleException(ex, request);
            }
            catch (Exception notHandledException) {
                if (notHandledException instanceof AbstractAppException appException) {
                    String translatedMessage = messageSource.getMessage(
                        appException.getMessage(), null, appException.getMessage(), request.getLocale());
                    log.error(translatedMessage, appException);
                    return handleExceptionInternal(appException, translatedMessage,
                        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
                }
                else {
                    log.error(notHandledException.getMessage(), notHandledException);
                    return handleExceptionInternal(notHandledException, notHandledException.getMessage(),
                        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
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
                // TODO: Maybe look at the child type or up the causedBy chain to see if a more meaningful message can be returned (JdbcSQLIntegrityConstraintViolationException, etc.)
                return createResponseEntity(dbException.getClass().getSimpleName(), headers, statusCode, request);
            }
            return super.handleExceptionInternal(ex, body, headers, statusCode, request);
        }

        @Override
        @NonNull
        protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, @NonNull HttpHeaders headers,
                @NonNull HttpStatusCode statusCode, @NonNull WebRequest request) {
            String jsonBody = "{ \"reason\": \"" + body + "\" }";
            return new ResponseEntity<>(jsonBody, headers, statusCode);
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
