package ca.jrvs.apps.trading.controller;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseExceptionUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResponseExceptionUtil.class);

    public static ResponseStatusException getResponseStatusException(Exception e){
        if (e instanceof IllegalArgumentException) {
            logger.debug("Invalid input: ", e);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } else {
            logger.error("Internal Error: ", e);
            return new ResponseStatusException((HttpStatus.INTERNAL_SERVER_ERROR),
                    "Internal error: please contact administrator.");
        }
    }
}
