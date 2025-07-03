package com.example.chuong10.ui.dulieu;

import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import android.content.Intent;

import com.example.chuong10.R;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class BiometricActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextView resultTextView;
    private KeyStore keyStore;
    private Cipher cipher;
    private static final String KEY_NAME = "biometric_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_biometric);

        // Initialize views
        resultTextView = findViewById(R.id.result_text_view);
        Button biometricLoginButton = findViewById(R.id.biometric_login_button);

        // Initialize Executor
        executor = ContextCompat.getMainExecutor(this);

        // Generate or load the cryptographic key
        generateKey();

        // Check if biometric authentication is available
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Biometric authentication is available
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Thiết bị không hỗ trợ cảm biến sinh trắc học", Toast.LENGTH_SHORT).show();
                biometricLoginButton.setEnabled(false);
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Cảm biến sinh trắc học không khả dụng", Toast.LENGTH_SHORT).show();
                biometricLoginButton.setEnabled(false);
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Chưa có dữ liệu sinh trắc học. Vui lòng đăng ký trong Cài đặt.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(android.provider.Settings.ACTION_BIOMETRIC_ENROLL));
                biometricLoginButton.setEnabled(false);
                return;
            default:
                Toast.makeText(this, "Không thể sử dụng xác thực sinh trắc học", Toast.LENGTH_SHORT).show();
                biometricLoginButton.setEnabled(false);
                return;
        }

        // Initialize BiometricPrompt
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
                resultTextView.setText("Kết quả: Lỗi xác thực - " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                try {
                    // Initialize cipher with the cryptographic object from authentication result
                    cipher.init(Cipher.ENCRYPT_MODE, (SecretKey) keyStore.getKey(KEY_NAME, null));
                    String dataToEncrypt = "Finger 1";
                    byte[] encryptedData = cipher.doFinal(dataToEncrypt.getBytes());
                    String encryptedHex = bytesToHex(encryptedData);
                    resultTextView.setText("Kết quả: Thành công\nDữ liệu mã hóa: " + encryptedHex);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Lỗi mã hóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resultTextView.setText("Kết quả: Thành công nhưng lỗi mã hóa");
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Xác thực thất bại, thử lại", Toast.LENGTH_SHORT).show();
                resultTextView.setText("Kết quả: Thất bại");
            }
        });

        // Configure BiometricPrompt dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng nhập bằng sinh trắc học")
                .setSubtitle("Chạm ngón tay vào cảm biến hoặc sử dụng khuôn mặt")
                .setNegativeButtonText("Hủy")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .build();

        // Set up button click listener
        biometricLoginButton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));
    }

    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true); // Key invalidated if new biometric enrolled

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(30, KeyProperties.AUTH_BIOMETRIC_STRONG);
            } else {
                builder.setUserAuthenticationValidityDurationSeconds(30); // 30 seconds timeout
            }

            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo khóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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