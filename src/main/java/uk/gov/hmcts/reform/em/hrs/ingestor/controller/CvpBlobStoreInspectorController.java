package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClientImpl;

import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@Slf4j
public class CvpBlobStoreInspectorController {

    @Autowired
    CvpBlobstoreClientImpl blobClient;

    @GetMapping(value = "/inspect", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> inspect() {
        Set<String> folders = blobClient.getFolders();
        if (folders != null) {
            log.info("Folders: \n" + String.join("\n ", folders));
        } else {
            log.info("Folders: None");
        }
        return ok("CVP Blobstore Inspected");
    }
}
