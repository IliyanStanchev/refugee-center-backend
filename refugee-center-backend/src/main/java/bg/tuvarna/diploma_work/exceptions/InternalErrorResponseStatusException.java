package bg.tuvarna.diploma_work.exceptions;

import org.springframework.web.server.ResponseStatusException;

public class InternalErrorResponseStatusException extends ResponseStatusException {

    final static int HTTP_STATUS_INTERNAL_SERVER_ERROR_CODE = 500;

    public InternalErrorResponseStatusException() {
        super(  HTTP_STATUS_INTERNAL_SERVER_ERROR_CODE
                ,"Something went wrong. Please try again later."
                ,null );
    }
}
