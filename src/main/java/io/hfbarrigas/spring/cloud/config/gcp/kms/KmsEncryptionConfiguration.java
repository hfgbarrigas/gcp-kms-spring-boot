package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.common.base.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

import java.util.Collections;

@Configuration
@ConditionalOnProperty(prefix = "gcp.kms", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(KmsProperties.class)
class KmsEncryptionConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpTransport.class)
    public HttpTransport httpTransport() {
        return new NetHttpTransport();
    }

    @Bean
    @ConditionalOnMissingBean(JsonFactory.class)
    public JsonFactory jsonFactory() {
        return new JacksonFactory();
    }

    @Bean
    @ConditionalOnBean({GoogleCredential.class, HttpTransport.class, JsonFactory.class})
    @ConditionalOnMissingBean
    public static CloudKMS defaultAuthorizedKmsClient(HttpTransport httpTransport,
                                                      JsonFactory jsonFactory,
                                                      KmsProperties kmsProperties,
                                                      GoogleCredential googleCredential) {
        Assert.isTrue(!Strings.isNullOrEmpty(kmsProperties.getApplicationName()), "applicationName cannot be empty or null");
        // Depending on the environment that provides the default credentials (e.g. Compute Engine, App
        // Engine), the credentials may require us to specify the scopes we need explicitly.
        // Check for this case, and inject the scope if required.
        if (googleCredential.createScopedRequired()) {
            googleCredential = googleCredential.createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloudkms"));
        }

        return new CloudKMS.Builder(httpTransport, jsonFactory, googleCredential)
                .setApplicationName(kmsProperties.getApplicationName())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public TextEncryptorLocator textEncryptorLocator(CloudKMS kms, KmsProperties kmsProperties) {
        return new KmsTextEncryptLocator(kms, kmsProperties.getKeyResource());
    }

    @Bean
    @ConditionalOnMissingBean
    public TextEncryptor textEncryptor(CloudKMS kms, KmsProperties kmsProperties) {
        Assert.isTrue(!Strings.isNullOrEmpty(kmsProperties.getKeyResource()), "keyResource cannot be empty or null");
        return new KmsTextEncryptor(kms, kmsProperties.getKeyResource());
    }
}
