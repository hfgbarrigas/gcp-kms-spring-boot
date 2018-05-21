package io.hfbarrigas.spring.cloud.config.gcp.kms;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "gcp.kms")
public class KmsProperties {

    private String applicationName;

    private String keyResource;

    private boolean enabled = false;

    private int connectTimeout = 500;

    private int readTimeout = 500;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getKeyResource() {
        return keyResource;
    }

    public void setKeyResource(String keyResource) {
        this.keyResource = keyResource;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
