package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.model.DecryptRequest;
import com.google.api.services.cloudkms.v1.model.DecryptResponse;
import com.google.api.services.cloudkms.v1.model.EncryptRequest;
import com.google.api.services.cloudkms.v1.model.EncryptResponse;
import com.google.common.base.Strings;
import io.hfbarrigas.spring.cloud.config.gcp.kms.exceptions.KmsFailedRequestException;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Base64;

/**
 * This {@link TextEncryptor} uses GCP KMS (Key Management Service) to encrypt / decrypt strings. Encoded cipher strings
 * are represented in Base64 format, to have a nicer string representation (only alpha-numeric chars), that can be
 * easily used as values in property files.
 */
public class KmsTextEncryptor implements TextEncryptor {

    private static final String EMPTY_STRING = "";

    private final CloudKMS kms;
    private final String kmsKeyResource;

    /**
     * @param kms            The GCP KMS client
     * @param kmsKeyResource The resource name of the CryptoKey or CryptoKeyVersionto use for encryption.
     *                       Must not be blank, if you you want to encrypt text.
     */
    KmsTextEncryptor(final CloudKMS kms, final String kmsKeyResource) {
        Assert.notNull(kms, "KMS client must not be null.");
        Assert.isTrue(!Strings.isNullOrEmpty(kmsKeyResource), "kmsKeyResource cannot be empty or null.");
        this.kms = kms;
        this.kmsKeyResource = kmsKeyResource;
    }

    @Override
    public String encrypt(final String text) {
        Assert.hasText(this.kmsKeyResource, "kmsKeyResource must not be blank");
        if (Strings.isNullOrEmpty(text)) {
            return EMPTY_STRING;
        } else {
            try {
                EncryptResponse encryptResponse = kms
                        .projects()
                        .locations()
                        .keyRings()
                        .cryptoKeys()
                        .encrypt(this.kmsKeyResource, new EncryptRequest().setPlaintext(Base64.getEncoder().encodeToString(text.getBytes())))
                        .execute();
                return encryptResponse.getCiphertext();
            } catch (IOException e) {
                throw new KmsFailedRequestException("Failed to encrypt data using KMS.", e);
            }
        }
    }

    @Override
    public String decrypt(final String encryptedText) {
        Assert.hasText(this.kmsKeyResource, "kmsKeyResource must not be blank");
        if (Strings.isNullOrEmpty(encryptedText)) {
            return EMPTY_STRING;
        } else {
            DecryptRequest request = new DecryptRequest().setCiphertext(getCipheredValue(encryptedText));
            try {
                DecryptResponse response = kms.projects().locations().keyRings().cryptoKeys()
                        .decrypt(this.kmsKeyResource, request)
                        .execute();
                return new String(Base64.getDecoder().decode(response.getPlaintext()));
            } catch (IOException e) {
                throw new KmsFailedRequestException("Failed to decrypt data using KMS.", e);
            }
        }
    }

    private static String getCipheredValue(String value) {
        if (!value.contains("}")) {
            return value;
        }
        return value.substring(value.lastIndexOf("}") + 1, value.length());
    }
}
