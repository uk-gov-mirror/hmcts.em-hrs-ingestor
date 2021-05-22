package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class IngestController {

    @Autowired
    DefaultIngestorService defaultIngestorService;

    @GetMapping(value = "/ingest", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> ingest() {
        defaultIngestorService.ingest();
        return ok("Ingestion Initiated");
    }

    @GetMapping(value = "/ingest/{maxFilesToProcess}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> ingest(@PathVariable("maxFilesToProcess") Integer maxFilesToProcess) {
        defaultIngestorService.ingest(maxFilesToProcess);
        return ok("Ingestion Initiated with " + maxFilesToProcess + " limit");
    }
}
