package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.model.DecryptRequest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import static io.hfbarrigas.spring.cloud.config.gcp.kms.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * This integration test shows the usage of spring-cloud-config-gcp-kms. You will find an encrypted property within
 * src/test/resources/ that will be decrypted during the bootstrap phase. In order to make this test runnable on every
 * machine, a mock is used instead of a real CloudKMS.
 */
@SpringBootTest
@ActiveProfiles("encryption")
public class KmsEncryptionIntegrationConfigurationTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private CloudKMS mockKms;

    @Value("${secret}")
    private String decryptedSecret;

    @Test
    public void testPropertyHasBeenDecrypted() throws Exception {

        assertThat(decryptedSecret).isEqualTo(PLAINTEXT);

        final DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setCiphertext(BASE64_PLAINTEXT);

        verify(mockKms, atLeastOnce()).projects();
        verify(mockKms.projects(), atLeastOnce()).locations();
        verify(mockKms.projects().locations(), atLeastOnce()).keyRings();
        verify(mockKms.projects().locations().keyRings(), atLeastOnce()).cryptoKeys();
        verify(mockKms.projects().locations().keyRings().cryptoKeys(), atLeastOnce()).decrypt(KEY, decryptRequest);
    }

    @Configuration
    @EnableAutoConfiguration
    static class TestConfig {
    }
}
