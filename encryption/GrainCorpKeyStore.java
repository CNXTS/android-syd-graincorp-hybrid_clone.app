package com.webling.graincorp.encryption;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.SecretKeyEntry;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * @author Artaza Aziz, Sagar Sethi
 */
public class GrainCorpKeyStore {

    private static final String TRANSFORMATION_SYMMETRIC = "AES/GCM/NoPadding";
    private static final String TRANSFORMATION_ASYMMETRIC = "RSA/ECB/PKCS1Padding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String GRAINCORP_KEY_ALIAS = "GcKey";
    private static final Charset charset = StandardCharsets.UTF_8;
    private static final int GCM_AUTH_TAG_LENGTH = 128;

    private static KeyStore keyStore;
    private Context context;
    private boolean isInitialised;


    @Inject
    public GrainCorpKeyStore(Context context) {
        this.context = context;
        //cannot initKeyStore() here since it throws exception and Dagger cannot use @Inject on constructors that throw exceptions
    }

    private void initKeyStore() throws Exception {
        if (!isInitialised) {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            isInitialised = true;
        }
    }

    /**
     * Performs encryption on the provided plain text.
     * If Android API 23 and above - performs encryption using Symmetric keys.
     * If Android API 18-22 - performs encryption using Asymmetric keys.
     * If Android API 17 and below - Doesn't encrypt as Android key store not properly supported
     * below 18.
     *
     * @param textToEncrypt plain text to be encrypted
     * @return encrypted data
     */
    @Nullable
    public String encrypt(@NonNull final String textToEncrypt) throws Exception {
        initKeyStore();
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                return encryptWithSymmetricKeys(textToEncrypt);
            } else {
                return encryptWithAsymmetricKeys(textToEncrypt);
            }
            // If below Android API 18, do nothing.
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Performs decryption on the provided encrypted data.
     * If Android API 23 and above - performs decryption using Symmetric keys.
     * If Android API 18-22 - performs decryption using Asymmetric keys.
     * If Android API 17 and below - Doesn't decrypt as Android key store not properly supported
     * below 18.
     *
     * @param encryptedData encrypted data to be decrypted
     * @return decrypted text
     */
    @Nullable
    public String decrypt(@NonNull final String encryptedData) throws Exception {
        initKeyStore();
        try {
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                return decryptWithSymmetricKeys(encryptedData.getBytes(charset));
            } else {
                return decryptWithAsymmetricKeys(encryptedData.getBytes(charset));
            }
            // If below Android API 18, do nothing.
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clears the key saved in the keystore
     */
    public void clearKey() {
        try {
            initKeyStore();
            keyStore.deleteEntry(GRAINCORP_KEY_ALIAS);
        } catch (Exception ignored) {
            //Do nothing.
        }
    }

    /**
     * Encrypts data using Asymmetric keys
     *
     * @param initialText plain text to be encrypted
     * @return decrypted data
     * @throws Exception all exception
     */
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    private String encryptWithAsymmetricKeys(@NonNull final String initialText) throws Exception {

        if (TextUtils.isEmpty(initialText)) {
            return null;
        }

        generateAsymmetricKeys();

        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(GRAINCORP_KEY_ALIAS, null);
        RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();


        Cipher cipher = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
        cipherOutputStream.write(initialText.getBytes(charset));
        cipherOutputStream.close();

        byte[] vals = outputStream.toByteArray();
        return new String(Base64.encode(vals, Base64.NO_WRAP));
    }

    /**
     * Encrypts data using Symmetric keys
     *
     * @param textToEncrypt plain text to be encrypted
     * @return encrypted test
     * @throws Exception all exceptions
     */
    @RequiresApi(api = VERSION_CODES.M)
    private String encryptWithSymmetricKeys(@NonNull final String textToEncrypt) throws Exception {
        SecretKey key;
        if (keyStore.getEntry(GRAINCORP_KEY_ALIAS, null) == null) {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

            keyGenerator.init(new KeyGenParameterSpec.Builder(GRAINCORP_KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            key = keyGenerator.generateKey();
        } else {
            key = ((SecretKeyEntry) keyStore.getEntry(GRAINCORP_KEY_ALIAS, null)).getSecretKey();
        }

        byte[] iv = new byte[12]; //This should always be 96-bit according to the GCM proposal
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        GCMParameterSpec ivParams = cipher.getParameters().getParameterSpec(GCMParameterSpec.class);
        iv = ivParams.getIV();

        byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes(charset));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + encrypted.length);
        byteBuffer.put((byte) iv.length);
        byteBuffer.put(iv);
        byteBuffer.put(encrypted);
        return Base64.encodeToString(byteBuffer.array(), Base64.NO_WRAP);
    }

    /**
     * Decrypts data using Asymmetric keys.
     *
     * @param encryptedData encrypted data to be decrypted
     * @return decrypted data
     * @throws Exception all exceptions
     */
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    private String decryptWithAsymmetricKeys(@NonNull final byte[] encryptedData) throws Exception {
        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(GRAINCORP_KEY_ALIAS, null);

        Cipher output = Cipher.getInstance(TRANSFORMATION_ASYMMETRIC);
        output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

        CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(Base64.decode(encryptedData, Base64.NO_WRAP)), output);
        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;
        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }

        return new String(bytes, 0, bytes.length, charset);
    }

    /**
     * Decrypts data using Symmetric keys
     *
     * @param encryptedData encrypted data to be decrypted
     * @return decrypted data
     * @throws Exception all exceptions
     */
    @RequiresApi(api = VERSION_CODES.M)
    private String decryptWithSymmetricKeys(@NonNull final byte[] encryptedData) throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.decode(encryptedData, Base64.NO_WRAP));

        int ivLength = byteBuffer.get();
        byte[] iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] encrypted = new byte[byteBuffer.remaining()];
        byteBuffer.get(encrypted);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION_SYMMETRIC);
        SecretKey key = ((SecretKeyEntry) keyStore.getEntry(GRAINCORP_KEY_ALIAS, null)).getSecretKey();

        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, iv));
        byte[] decrypted = cipher.doFinal(encrypted);

        Arrays.fill(iv, (byte) 0);
        key = null;
        Arrays.fill(encrypted, (byte) 0);

        return new String(decrypted, charset);
    }


    /**
     * Creates Asymmetric keys for Cryptography operations using Android API 18-22 only.
     * For API 23 onwards, Symmetric keys are used for Crypto operations (not implemented in this method)
     *
     * @throws Exception all possible exceptions.
     */
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    private void generateAsymmetricKeys() throws Exception {
        initKeyStore();
        // Only create new keys if doesn't already exist in keystore.
        if (!keyStore.containsAlias(GRAINCORP_KEY_ALIAS)) {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1000);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(GRAINCORP_KEY_ALIAS)
                    .setSubject(new X500Principal("CN=GrainCorp, O=GrainCorp Limited"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setKeySize(4096)
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);

            generator.initialize(spec);
            generator.generateKeyPair();
        }
    }
}