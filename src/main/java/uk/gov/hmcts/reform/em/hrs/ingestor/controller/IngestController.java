package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import javax.inject.Inject;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class IngestController {

    @Inject
    DefaultIngestorService defaultIngestorService;

    @GetMapping(value = "/ingest", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> ingest() {
        defaultIngestorService.ingest();
        return ok("Ingestion Initiated");
    }
}
