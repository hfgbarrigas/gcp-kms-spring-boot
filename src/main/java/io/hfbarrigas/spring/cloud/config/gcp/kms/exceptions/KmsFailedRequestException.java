package io.hfbarrigas.spring.cloud.config.gcp.kms.exceptions;

public class KmsFailedRequestException extends RuntimeException {
    public KmsFailedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
