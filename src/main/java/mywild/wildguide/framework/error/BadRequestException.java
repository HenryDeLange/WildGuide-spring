package mywild.wildguide.framework.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends AbstractAppException {

    public BadRequestException(String messageKey) {
        super(messageKey);
    }

    public BadRequestException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

}
