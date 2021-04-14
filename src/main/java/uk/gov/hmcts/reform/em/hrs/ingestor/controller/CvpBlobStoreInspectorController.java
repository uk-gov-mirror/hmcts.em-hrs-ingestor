package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClientImpl;

import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class CvpBlobStoreInspectorController {

    private static final Logger log = LoggerFactory.getLogger(CvpBlobStoreInspectorController.class);

    @Autowired
    CvpBlobstoreClientImpl blobClient;

    @GetMapping(value = "/inspect", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> inspect() {
        Set<String> folders = blobClient.getFolders();
        String foldersMessage = "Folders: " + (folders.isEmpty() ? "None" : String.join("\n ", folders));

        log.info(foldersMessage);

        return ok("CVP Blobstore Inspected<p>" + foldersMessage.replace("\n", "<p>"));
    }
}
