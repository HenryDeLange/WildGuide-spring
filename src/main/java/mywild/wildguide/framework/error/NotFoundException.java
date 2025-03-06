package mywild.wildguide.framework.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends AbstractAppException {

    public NotFoundException(String messageKey) {
        super(messageKey);
    }

    public NotFoundException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

}
