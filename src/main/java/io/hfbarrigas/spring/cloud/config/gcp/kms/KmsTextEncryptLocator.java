package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.services.cloudkms.v1.CloudKMS;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Map;

public class KmsTextEncryptLocator implements TextEncryptorLocator {

    private final static String KEY = "key";
    private final static String SECRET = "secret";
    private final CloudKMS kms;
    private final String defaultKey;

    KmsTextEncryptLocator(CloudKMS kms, String defaultKey) {
        this.kms = kms;
        this.defaultKey = defaultKey;
    }

    @Override
    public TextEncryptor locate(Map<String, String> keys) {
        String key = keys.getOrDefault(KEY, this.defaultKey);
        if (keys.containsKey(SECRET)) {
            throw new UnsupportedOperationException("KMS secret usage not implemented.");
        }
        return new KmsTextEncryptor(this.kms, key);
    }
}
