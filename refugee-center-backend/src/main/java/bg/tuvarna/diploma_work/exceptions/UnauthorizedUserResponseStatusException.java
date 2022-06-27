package bg.tuvarna.diploma_work.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedUserResponseStatusException extends ResponseStatusException {

    final static int HTTP_STATUS_ERROR_CODE = HttpStatus.UNAUTHORIZED.value();

    public UnauthorizedUserResponseStatusException() {
        super(HTTP_STATUS_ERROR_CODE
                , "Unauthorized access"
                , null);
    }
}
