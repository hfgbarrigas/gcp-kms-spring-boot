Spring Cloud Config - GCP KMS Add-on
====================================

This is a Spring Cloud Config add-on that provides encryption via GCP (Google Cloud Platform) KMS (Key management service).

Installation
------------

### Prerequisites
Given you have a [Spring Boot](http://projects.spring.io/spring-boot/) application.

### Step 1
Add the dependency to your pom.xml (or Gradle build file).

    ...
    <dependency>
        <groupId>io.hfbarrigas</groupId>
        <artifactId>spring-cloud-config-gcp-kms</artifactId>
        <version>${spring-cloud-gcp-kms.version}</version>
    </dependency>
    ...

### Step 2
Apply configuration to the application's bootstrap file

E.g. `bootstrap.yml`:

    gcp:
        kms:
            # Optional: Turn off the KMS feature completely (e.g. for local development) 
            enabled: false
            
            # Application name
            applicationName: Application's name used when interacting with KMS
            
            # Default key to encrypt/decrypt values when no key is specified
            key-resource: 'projects/myproject/locations/global/keyRings/configuration/cryptoKeys/configs'
               
            # Http timeouts when communicating with GCP KMS Api - Note: This configurations are not used atm, TODO!
            connect-timeout: 500
            read-timeout: 500

**GCP credentials** need to available in the application context in order to use this. Take a peek to:
[Google Cloud Spring Boot](hhttps://github.com/hfgbarrigas/spring-boot-gcp)
that exposes bean to fetch gcp metadata and credentials.

Usage
-----

Now you can add encrypted values to you property files. An encrypted value must always start with `{cipher}`.
Those properties are automatically decrypted on application startup.

E.g. `application.yml`

    secretPassword: '{cipher}random encrypted password'

### Use an encryption context

An encryption context is a set of key-value pairs used for encrypt and decrypt values, which might be useful as security
enhancement.
[Using Multiple Keys and Key Rotation](https://cloud.spring.io/spring-cloud-static/Brixton.SR6/#_using_multiple_keys_and_key_rotation) take a look to understand
spring config uses contexts.
When using this library, the user will need to care about the key resource name *{key:keyResourceName}*. An example is shown below:

E.g. `application.yml`

    secretPassword: '{cipher}{key:projects/GCP_PROJECT_ID/locations/KEY_LOCATION_ID/keyRings/KEY_RING_ID/cryptoKeys/CRYPTO_KEY_ID}CiA47hYvQqWFFGq3TLtzQO5FwZMam2AnaeQt4PGEZHhDLxFTAQEBAgB4OO4WL0KlhRRqt0y7c0DuRcGTGptgJ8nkLeDxhGR4Qy8AAABqMGgGCSqGSIb3DQEHBqBbMFkCAQAwVAYJKoZIhvcNAQcBMB4GCWCGSAFlAwQBLjARBAx61LJpXQwgTcnGeSQCARCAJ4xhpGC5HT2xT+Vhy2iAuT+P/PLliZK5u6CiGhgudteZsCr7VJ/1aw=='

The `{key:projects/GCP_PROJECT_ID/locations/KEY_LOCATION_ID/keyRings/KEY_RING_ID/cryptoKeys/CRYPTO_KEY_ID}` part is the encryption context,
 and the key resource name format respects gcp KMS usage. 

Notes
-----
- Secrets are not supported at the moment.
- Encode key resource names.

### How to get the cipher text?

The Spring Cloud Config Server library provides an endpoint to encrypt plain text strings. Make sure to secure this endpoint properly!
See [Encryption and Decryption](https://cloud.spring.io/spring-cloud-static/Brixton.SR6/#_encryption_and_decryption_2) for details.
