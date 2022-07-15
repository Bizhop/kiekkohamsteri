package fi.bizhop.kiekkohamsteri;

import fi.bizhop.kiekkohamsteri.security.GoogleAuthentication;
import fi.bizhop.kiekkohamsteri.security.JWTAuthentication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ContextConfiguration(initializers = {SpringContextTestBase.Initializer.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class SpringContextTestBase {
    @MockBean JWTAuthentication jwtAuthentication;
    @MockBean GoogleAuthentication googleAuthentication;

    @Container
    private static PostgreSQLContainer postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("hamsteri")
            .withUsername("hamsteri")
            .withPassword("hamsteri");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("spring.datasource.url=" + postgres.getJdbcUrl(),
                            "spring.datasource.username=" + postgres.getUsername(),
                            "spring.datasource.password=" + postgres.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
