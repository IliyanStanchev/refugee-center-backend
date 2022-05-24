package bg.tuvarna.diploma_work.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class CustomResponse extends ResponseEntity {

    public CustomResponse(Object body, MultiValueMap headers, int rawStatus) {
        super(body, headers, rawStatus);
    }
}
