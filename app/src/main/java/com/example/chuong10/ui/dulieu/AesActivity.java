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
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidAlgorithmParameterException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class AesActivity extends AppCompatActivity {

    private KeyStore keyStore;
    private Cipher cipher;
    private SecretKey secretKey;
    private TextView resultTextView;
    private static final String KEY_NAME = "aes_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aes);

        // Initialize views
        resultTextView = findViewById(R.id.result_text_view);
        Button encryptButton = findViewById(R.id.encrypt_button);

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
            Toast.makeText(this, "Lỗi khởi tạo khóa AES: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Set up button click listener
        encryptButton.setOnClickListener(v -> encryptData());
    }

    private void generateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, CertificateException, IOException, NoSuchPaddingException, UnrecoverableKeyException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(256) // AES with 256-bit key
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(false); // No biometric required

        keyGenerator.init(builder.build());
        keyGenerator.generateKey();
        secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    private void encryptData() {
        if (secretKey == null) {
            Toast.makeText(this, "Khóa AES chưa được khởi tạo", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String dataToEncrypt = "Nhom04";
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV(); // IV for CBC mode
            byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());
            String encryptedHex = bytesToHex(encryptedData);
            String ivHex = bytesToHex(iv);
            resultTextView.setText("Dữ liệu mã hóa AES: " + encryptedHex + "\nIV: " + ivHex);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi mã hóa AES: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resultTextView.setText("Lỗi mã hóa AES");
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
}