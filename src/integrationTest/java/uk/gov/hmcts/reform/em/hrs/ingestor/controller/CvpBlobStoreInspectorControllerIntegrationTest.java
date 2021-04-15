package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.componenttests.AbstractBaseTest;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;
import uk.gov.hmcts.reform.em.hrs.ingestor.storage.CvpBlobstoreClientImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {CvpBlobStoreInspectorController.class})
class CvpBlobStoreInspectorControllerIntegrationTest extends AbstractBaseTest {

    @MockBean
    CvpBlobstoreClientImpl mockBlobClient;

    @DisplayName("Should indicate CVP Blobstore Inspected with 200 response code")
    @Test
    public void inspectEndpoint() throws Exception {
        MvcResult response = mockMvc.perform(get("/inspect")).andExpect(status().isOk()).andReturn();
        assertThat(response.getResponse().getContentAsString()).startsWith("CVP Blobstore Inspected");
    }
}
