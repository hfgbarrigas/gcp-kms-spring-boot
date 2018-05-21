package io.hfbarrigas.spring.cloud.config.gcp.kms;

import com.google.api.services.cloudkms.v1.CloudKMS;
import com.google.api.services.cloudkms.v1.model.DecryptRequest;
import com.google.api.services.cloudkms.v1.model.DecryptResponse;
import com.google.api.services.cloudkms.v1.model.EncryptRequest;
import com.google.api.services.cloudkms.v1.model.EncryptResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class KmsTextEncryptorTest {

    private static final String PLAINTEXT = "plaintext";
    private static final String CIPHER_TEXT = "C1PHERT3XT";

    private CloudKMS mockKms;
    private CloudKMS.Projects projectsMock;
    private CloudKMS.Projects.Locations locationsMock;
    private CloudKMS.Projects.Locations.KeyRings keyRingsMock;
    private CloudKMS.Projects.Locations.KeyRings.CryptoKeys cryptoKeysMock;
    private CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt decryptMock;
    private CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Encrypt encryptMock;
    private KmsTextEncryptor textEncryptor;
    private EncryptRequest expectedEncryptRequest;
    private DecryptRequest expectedDecryptRequest;
    private EncryptResponse expectedEncryptResponse;
    private DecryptResponse expectedDecryptResponse;

    @Captor
    private ArgumentCaptor<EncryptRequest> encryptRequestArgumentCaptor;

    @Captor
    ArgumentCaptor<DecryptRequest> decryptRequestArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        //prepare mocks
        mockKms = mock(CloudKMS.class);
        projectsMock = mock(CloudKMS.Projects.class);
        locationsMock = mock(CloudKMS.Projects.Locations.class);
        keyRingsMock = mock(CloudKMS.Projects.Locations.KeyRings.class);
        cryptoKeysMock = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.class);
        decryptMock = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Decrypt.class);
        encryptMock = mock(CloudKMS.Projects.Locations.KeyRings.CryptoKeys.Encrypt.class);
        textEncryptor = new KmsTextEncryptor(mockKms, Constants.KEY);

        expectedEncryptRequest = new EncryptRequest().setPlaintext(Base64.getEncoder().encodeToString(PLAINTEXT.getBytes()));
        expectedEncryptResponse = new EncryptResponse().setCiphertext(CIPHER_TEXT);

        expectedDecryptRequest = new DecryptRequest().setCiphertext(CIPHER_TEXT);
        expectedDecryptResponse = new DecryptResponse().setPlaintext(Base64.getEncoder().encodeToString(PLAINTEXT.getBytes()));

        //prepare actions
        when(mockKms.projects()).thenReturn(projectsMock);
        when(projectsMock.locations()).thenReturn(locationsMock);
        when(locationsMock.keyRings()).thenReturn(keyRingsMock);
        when(keyRingsMock.cryptoKeys()).thenReturn(cryptoKeysMock);
        when(cryptoKeysMock.decrypt(anyString(), any(DecryptRequest.class))).thenReturn(decryptMock);
        when(cryptoKeysMock.encrypt(anyString(), any(EncryptRequest.class))).thenReturn(encryptMock);

        when(decryptMock.execute()).thenReturn(expectedDecryptResponse);
        when(encryptMock.execute()).thenReturn(expectedEncryptResponse);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testEncrypt() throws Exception {
        //act
        String encryptedSecret = textEncryptor.encrypt(PLAINTEXT);

        //assert
        verify(projectsMock, times(1)).locations();
        verify(locationsMock, times(1)).keyRings();
        verify(keyRingsMock, times(1)).cryptoKeys();
        verify(cryptoKeysMock, times(1)).encrypt(Constants.KEY, expectedEncryptRequest);

        assertThat(encryptedSecret).isEqualTo(CIPHER_TEXT);
    }

    @Test
    public void testEncryptNull() throws Exception {
        assertThat(textEncryptor.encrypt(null)).isEqualTo("");
    }

    @Test
    public void testEncryptEmptyString() throws Exception {
        assertThat(textEncryptor.encrypt("")).isEqualTo("");
    }

    @Test
    public void testDecryptNull() throws Exception {
        assertThat(textEncryptor.decrypt(null)).isEqualTo("");
    }

    @Test
    public void testDecryptEmptyString() throws Exception {
        assertThat(textEncryptor.decrypt("")).isEqualTo("");
    }

    @Test
    public void testDecrypt() throws Exception {
        //act
        String decryptedSecret = textEncryptor.decrypt(CIPHER_TEXT);

        //assert
        verify(projectsMock, times(1)).locations();
        verify(locationsMock, times(1)).keyRings();
        verify(keyRingsMock, times(1)).cryptoKeys();
        verify(cryptoKeysMock, times(1)).decrypt(Constants.KEY, expectedDecryptRequest);

        assertThat(decryptedSecret).isEqualTo(PLAINTEXT);
    }
}
