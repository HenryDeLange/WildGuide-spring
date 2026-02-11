package mywild.wildguide.framework.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApplicationException extends AbstractAppException {

    public ApplicationException(String messageKey) {
        super(messageKey);
    }

    public ApplicationException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

}
