package uk.gov.hmcts.reform.em.hrs.ingestor.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AntivirusClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.av.AvScanResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItem;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.CvpItemSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.HrsFileSet;
import uk.gov.hmcts.reform.em.hrs.ingestor.domain.Metadata;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.FileParsingException;
import uk.gov.hmcts.reform.em.hrs.ingestor.exception.HrsApiException;
import uk.gov.hmcts.reform.em.hrs.ingestor.http.HrsApiClient;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefaultIngestorServiceTest {
    @Mock
    private CvpBlobstoreClient cvpBlobstoreClient;
    @Mock
    private HrsApiClient hrsApiClient;
    @Mock
    private IngestionFilterer ingestionFilterer;
    @Mock
    private AntivirusClient antivirusClient;
    @Mock
    private MetadataResolver metadataResolver;

    @InjectMocks
    private DefaultIngestorService underTest;

    private static final String FOLDER_ONE = "folder-1";
    private static final String FOLDER_TWO = "folder-2";
    private static final String FOLDER_THREE = "folder-3";
    private static final CvpItem YET_TO_INGEST = new CvpItem("f3.mp4", "uri3", "hash3");
    private static final CvpItemSet CVP_FILE_SET = new CvpItemSet(Set.of(
        new CvpItem("f1.mp4", "uri1", "hash1"),
        new CvpItem("f2.mp4", "uri2", "hash2"),
        YET_TO_INGEST
    ));
    private static final HrsFileSet HRS_FILE_SET = new HrsFileSet(Set.of("f1.mp4", "f2.mp4"));
    private static final Metadata METADATA = new Metadata(
        "recording-uri",
        "I2foA30B==",
        null,
        "xyz",
        LocalDateTime.now(),
        "AB",
        "222",
        null,
        0
    );

    @Test
    void testShouldIngestOneFolder() throws Exception {
        doReturn(Set.of(FOLDER_ONE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(FOLDER_ONE);
        doReturn(HRS_FILE_SET).when(hrsApiClient).getIngestedFiles(FOLDER_ONE);
        doReturn(Set.of(YET_TO_INGEST)).when(ingestionFilterer).filter(CVP_FILE_SET, HRS_FILE_SET);
        doNothing().when(cvpBlobstoreClient).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        doReturn(AvScanResult.CLEAN).when(antivirusClient).scan(any(InputStream.class));
        try {
            doReturn(METADATA).when(metadataResolver).resolve(any(CvpItem.class));
        } catch (FileParsingException e) {
            e.printStackTrace();
        }
        // THEN
        // Filename parsing happen here
        // AND
        // call HRS to copy, create record in CCD and update databases

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(1)).findByFolder(FOLDER_ONE);
        verify(hrsApiClient, times(1)).getIngestedFiles(FOLDER_ONE);
        verify(ingestionFilterer, times(1)).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, times(1)).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, times(1)).scan(any(InputStream.class));
        try {
            verify(metadataResolver, times(1)).resolve(any(CvpItem.class));
        } catch (FileParsingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testShouldIngestMultipleFolders() throws Exception {
        doReturn(Set.of(FOLDER_ONE, FOLDER_TWO, FOLDER_THREE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(anyString());
        doReturn(HRS_FILE_SET).when(hrsApiClient).getIngestedFiles(anyString());
        doReturn(Set.of(YET_TO_INGEST)).when(ingestionFilterer).filter(CVP_FILE_SET, HRS_FILE_SET);
        doNothing().when(cvpBlobstoreClient).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        doReturn(AvScanResult.CLEAN).when(antivirusClient).scan(any(InputStream.class));
        try {
            doReturn(METADATA).when(metadataResolver).resolve(any(CvpItem.class));
        } catch (FileParsingException e) {
            e.printStackTrace();
        }
        // THEN
        // Filename parsing happen here
        // AND
        // call HRS to copy, create record in CCD and update databases

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(3)).findByFolder(anyString());
        verify(hrsApiClient, times(3)).getIngestedFiles(anyString());
        verify(ingestionFilterer, times(3)).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, times(3)).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, times(3)).scan(any(InputStream.class));
        try {
            verify(metadataResolver, times(3)).resolve(any(CvpItem.class));
        } catch (FileParsingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testShouldRefuseIngestionWhenInfectedFileIsDetected() throws Exception {
        doReturn(Set.of(FOLDER_ONE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(FOLDER_ONE);
        doReturn(HRS_FILE_SET).when(hrsApiClient).getIngestedFiles(FOLDER_ONE);
        doReturn(Set.of(YET_TO_INGEST)).when(ingestionFilterer).filter(CVP_FILE_SET, HRS_FILE_SET);
        doNothing().when(cvpBlobstoreClient).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        doReturn(AvScanResult.INFECTED).when(antivirusClient).scan(any(InputStream.class));
        // THEN
        // Filename parsing happen here
        // AND
        // call HRS to copy, create record in CCD and update databases

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(1)).findByFolder(FOLDER_ONE);
        verify(hrsApiClient, times(1)).getIngestedFiles(FOLDER_ONE);
        verify(ingestionFilterer, times(1)).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, times(1)).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, times(1)).scan(any(InputStream.class));
        verify(metadataResolver, never()).resolve(any(CvpItem.class));

    }

    @Test
    void testShouldRefuseIngestionWhenGettingFilesFromHrsApiRaisesInputOutException() throws Exception {
        doReturn(Set.of(FOLDER_ONE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(anyString());
        doThrow(IOException.class).when(hrsApiClient).getIngestedFiles(anyString());

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(1)).findByFolder(anyString());
        verify(hrsApiClient, times(1)).getIngestedFiles(anyString());
        verify(ingestionFilterer, never()).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, never()).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, never()).scan(any(InputStream.class));
        verify(metadataResolver, never()).resolve(any(CvpItem.class));

    }

    @Test
    void testShouldRefuseIngestionWhenGettingFilesFromHrsApiRaisesHrsApiException() throws Exception {
        doReturn(Set.of(FOLDER_ONE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(anyString());
        doThrow(HrsApiException.class).when(hrsApiClient).getIngestedFiles(anyString());

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(1)).findByFolder(anyString());
        verify(hrsApiClient, times(1)).getIngestedFiles(anyString());
        verify(ingestionFilterer, never()).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, never()).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, never()).scan(any(InputStream.class));
        verify(metadataResolver, never()).resolve(any(CvpItem.class));

    }

    @Test
    void testShouldRefuseIngestionWhenVirusCheckingRaisesException() throws Exception {
        doReturn(Set.of(FOLDER_ONE)).when(cvpBlobstoreClient).getFolders();
        doReturn(CVP_FILE_SET).when(cvpBlobstoreClient).findByFolder(anyString());
        doReturn(HRS_FILE_SET).when(hrsApiClient).getIngestedFiles(anyString());
        doReturn(Set.of(YET_TO_INGEST)).when(ingestionFilterer).filter(CVP_FILE_SET, HRS_FILE_SET);
        doNothing().when(cvpBlobstoreClient).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        doThrow(IOException.class).when(antivirusClient).scan(any(InputStream.class));

        underTest.ingest();

        verify(cvpBlobstoreClient, times(1)).getFolders();
        verify(cvpBlobstoreClient, times(1)).findByFolder(anyString());
        verify(hrsApiClient, times(1)).getIngestedFiles(anyString());
        verify(ingestionFilterer, times(1)).filter(CVP_FILE_SET, HRS_FILE_SET);
        verify(cvpBlobstoreClient, times(1)).downloadFile(eq(YET_TO_INGEST.getFilename()), any(OutputStream.class));
        verify(antivirusClient, times(1)).scan(any(InputStream.class));
        verify(metadataResolver, never()).resolve(any(CvpItem.class));

    }

}
