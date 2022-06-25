package bg.tuvarna.diploma_work.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseStatusException extends ResponseStatusException {

    final static int HTTP_STATUS_CUSTOM_STATUS_CODE = 433;

    public CustomResponseStatusException(String reason) {
        super(  HTTP_STATUS_CUSTOM_STATUS_CODE
                ,reason
                ,null );
    }
}
