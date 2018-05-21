package io.hfbarrigas.spring.cloud.config.gcp.kms;

class Constants {
    static final String KEY = "projects/GCP_PROJECT_ID/locations/KEY_LOCATION_ID/keyRings/KEY_RING_ID/cryptoKeys/CRYPTO_KEY_ID";
    static final String PLAINTEXT = "Hello World!";
    static final String BASE64_PLAINTEXT = "SGVsbG8gV29ybGQh";
    static final String CYPHERED_TEXT = "c2VjcmV0";
}
