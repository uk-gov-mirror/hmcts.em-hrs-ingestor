package uk.gov.hmcts.reform.em.hrs.ingestor.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.em.hrs.ingestor.componenttests.AbstractBaseTest;
import uk.gov.hmcts.reform.em.hrs.ingestor.service.DefaultIngestorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {IngestController.class})
class IngestControllerTest extends AbstractBaseTest {

    @MockBean
    DefaultIngestorService mockIngestService;

    @DisplayName("Should welcome upon root request with 200 response code")
    @Test
    public void ingestEndpoint() throws Exception {
        MvcResult response = mockMvc.perform(get("/ingest")).andExpect(status().isOk()).andReturn();
        assertThat(response.getResponse().getContentAsString()).startsWith("Ingestion");
    }

    @DisplayName("Should welcome upon root request with 200 response code")
    @Test
    public void ingestEndpointWithLimit() throws Exception {
        Integer maxFilesToProcess = 1;
        MvcResult response =
            mockMvc.perform(get("/ingest/" + maxFilesToProcess)).andExpect(status().isOk()).andReturn();
        String welcomeText = "Ingestion Initiated with " + maxFilesToProcess + " limit";
        assertThat(response.getResponse().getContentAsString()).startsWith(welcomeText);
    }
}
