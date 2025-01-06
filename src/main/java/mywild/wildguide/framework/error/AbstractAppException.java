package mywild.wildguide.framework.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class AbstractAppException extends RuntimeException {

    protected AbstractAppException(String message) {
        super(message);
    }

    protected AbstractAppException(String message, Throwable cause) {
        super(message, cause);
    }

}
