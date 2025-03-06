package mywild.wildguide.framework.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends AbstractAppException {

    public ForbiddenException(String messageKey) {
        super(messageKey);
    }

    public ForbiddenException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

}
