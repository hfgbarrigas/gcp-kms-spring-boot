package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.model.DecryptRequest;
import com.google.api.services.cloudkms.v1.model.DecryptResponse;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static io.hfbarrigas.spring.cloud.config.gcp.kms.Constants.BASE64_PLAINTEXT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ConditionalOnProperty(prefix = "gcp.kms", name = "useMock", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(KmsEncryptionConfiguration.class)
class MockAwsKmsConfig {

    @Bean
    CloudKMS kms() throws IOException {
        final CloudKMS mock = mock(CloudKMS.class);
        final CloudKMS.Projects projectsMock = mock(CloudKMS.Projects.class);
        final CloudKMS.Projects.Locations locationsMock = mock(CloudKMS.Projects.Locations.class);
        final CloudKMS.Projects.Locations.KeyRings keyRingsMock = mock(CloudKMS.Projects.Locations.KeyRings.class);
        final CloudKMS.Projects.Locations.KeyRings.CryptoKeys cryptoKeysMock = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.class);
        final CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt decryptMock = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt.class);

        when(mock.projects()).thenReturn(projectsMock);
        when(projectsMock.locations()).thenReturn(locationsMock);
        when(locationsMock.keyRings()).thenReturn(keyRingsMock);
        when(keyRingsMock.cryptoKeys()).thenReturn(cryptoKeysMock);
        when(cryptoKeysMock.decrypt(anyString(), any(DecryptRequest.class))).thenReturn(decryptMock);
        when(decryptMock.execute()).thenReturn(new DecryptResponse().setPlaintext(BASE64_PLAINTEXT));
        return mock;
    }


}
