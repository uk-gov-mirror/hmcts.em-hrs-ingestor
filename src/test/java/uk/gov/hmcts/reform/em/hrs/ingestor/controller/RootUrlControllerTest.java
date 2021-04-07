package uk.gov.hmcts.reform.em.hrs.ingestor.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootUrlControllerTest {

    final RootController rootController = new RootController();

    @DisplayName("Should welcome upon root request with 200 response code")
    @Test
    void shouldReturnWelcomeResponse() {

        ResponseEntity<String> responseEntity = rootController.welcome();
        String expectedMessage = "Welcome to Hearing Recordings Ingestor";

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertThat(responseEntity.getBody()).contains(expectedMessage);
    }


}
