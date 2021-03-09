package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class RootController {

    /*
     * Azure hits us on / every 5 seconds to prevent it sleeping the application
     * Application insights registers that as a 404 and adds it as an exception,
     * This is here to reduce the noise
     */
    @GetMapping(value = "/", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> welcome() {
        return ok("Welcome to Hearing Recordings Ingestor");
    }
}
