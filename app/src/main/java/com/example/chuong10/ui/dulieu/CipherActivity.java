package com.example.chuong10.ui.dulieu;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chuong10.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;

public class CipherActivity extends AppCompatActivity {

    private KeyStore keyStore;
    private Cipher cipher;
    private SecretKey secretKey;
    private TextView resultTextView;
    private static final String KEY_NAME = "cipher_key";
    private byte[] iv; // Initialization Vector for CBC mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cipher);

        // Initialize views
        resultTextView = findViewById(R.id.result_text_view);
        Button encryptButton = findViewById(R.id.encrypt_button);
        Button decryptButton = findViewById(R.id.decrypt_button);

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Generate or load the cryptographic key
        try {
            generateKey();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo khóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Set up button click listeners
        encryptButton.setOnClickListener(v -> encryptData());
        decryptButton.setOnClickListener(v -> decryptData());
    }

    private void generateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException, NoSuchPaddingException, UnrecoverableKeyException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false); // Disable biometric for this example

        keyGenerator.init(builder.build());
        keyGenerator.generateKey();
        secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null); // Ensure key is loaded
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    private void encryptData() {
        if (secretKey == null) {
            Toast.makeText(this, "Khóa chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String dataToEncrypt = "Nhom04";
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); // Initialize cipher with SecretKey
            iv = cipher.getIV(); // Store IV for decryption
            byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());
            String encryptedHex = bytesToHex(encryptedData);
            resultTextView.setText("Dữ liệu mã hóa: " + encryptedHex + "\nIV: " + bytesToHex(iv));
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mã hóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resultTextView.setText("Lỗi mã hóa");
        }
    }

    private void decryptData() {
        if (iv == null || secretKey == null) {
            Toast.makeText(this, "Vui lòng mã hóa trước", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            String encryptedText = resultTextView.getText().toString().split("\n")[0].replace("Dữ liệu mã hóa: ", "");
            byte[] encryptedBytes = hexToBytes(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedData = new String(decryptedBytes);
            resultTextView.setText("Dữ liệu giải mã: " + decryptedData);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi giải mã: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resultTextView.setText("Lỗi giải mã");
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}