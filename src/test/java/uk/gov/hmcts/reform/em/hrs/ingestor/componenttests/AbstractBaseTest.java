package uk.gov.hmcts.reform.em.hrs.ingestor.componenttests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

@SpringBootTest(classes = {
    //    TestSecurityConfiguration.class,
    //    TestAzureStorageConfig.class
}
)
@ExtendWith(MockitoExtension.class)
public abstract class AbstractBaseTest {

    @Inject
    private WebApplicationContext context;

    //    @MockBean
    //    private JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    protected MockMvc mockMvc;

    //    @Mock
    //    protected Authentication authentication;
    //
    //    @Mock
    //    protected SecurityContext securityContext;

    @BeforeEach
    public void setupMocks() {
        // doReturn(authentication).when(securityContext).getAuthentication();
        // SecurityContextHolder.setContext(securityContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
}
