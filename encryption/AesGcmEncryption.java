package com.webling.graincorp.encryption;

import androidx.annotation.Nullable;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

/**
 * @author Artaza Aziz
 */
public final class AesGcmEncryption {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ALGORITHM = "AES";
    private static final Charset charset = StandardCharsets.UTF_8;

    private final SecureRandom secureRandom;
    @Nullable
    private Cipher cipher;

    @Inject
    public AesGcmEncryption() {
        this(new SecureRandom());
    }

    public AesGcmEncryption(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }

    public String encrypt(String key, String rawData) throws Exception {

        if (key.length() < 16) {
            throw new IllegalArgumentException("key size should be 16 or 32 bytes");
        }
        byte[] iv = new byte[12]; //This should always be 96-bit according to the GCM proposal
        secureRandom.nextBytes(iv);

        final Cipher cipher = getCipher();

        byte[] rawEncryptionKey = key.getBytes(charset);

        cipher.init(Cipher.ENCRYPT_MODE,
                new SecretKeySpec(rawEncryptionKey, ALGORITHM),
                new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(rawData.getBytes(charset));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encrypted.length);
        byteBuffer.put((byte) iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(encrypted);
        return Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP);
    }

    public String decrypt(String key, String encryptedData) throws Exception {
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.decode(encryptedData, Base64.NO_WRAP));

            int ivLength = byteBuffer.get();
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);
            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);

            final Cipher cipher = getCipher();
            byte[] rawEncryptionKey = key.getBytes(charset);

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(rawEncryptionKey, ALGORITHM), new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encrypted);

            Arrays.fill(iv, (byte) 0);
            Arrays.fill(rawEncryptionKey, (byte) 0);
            Arrays.fill(encrypted, (byte) 0);

            return new String(decrypted, charset);
        } catch (BadPaddingException | InvalidKeyException e) { // bad key
            throw new InvalidKeyException();
        }
    }

    private Cipher getCipher() throws NoSuchAlgorithmException {
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance(TRANSFORMATION);
            } catch (Exception e) {
                throw new NoSuchAlgorithmException();
            }
        }
        return cipher;
    }
}