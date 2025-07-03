package com.example.chuong10.ui.dulieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chuong10.R;

public class SecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_security);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_security = findViewById(R.id.btn_security);
        btn_security.setOnClickListener(v -> {
            startActivity(new Intent(SecurityActivity.this, SecurityUserActivity.class));
        });

        Button btn_finger = findViewById(R.id.btn_finger);
        btn_finger.setOnClickListener(v -> {
            startActivity(new Intent(SecurityActivity.this, BiometricActivity.class));
        });

        Button btn_cipher = findViewById(R.id.btn_cipher);
        btn_cipher.setOnClickListener(v -> {
            startActivity(new Intent(SecurityActivity.this, CipherActivity.class));
        });

        Button btn_aes = findViewById(R.id.btn_aes);
        btn_aes.setOnClickListener(v -> {
            startActivity(new Intent(SecurityActivity.this, AesActivity.class));
        });

        Button btn_internal = findViewById(R.id.btn_internal);
        btn_internal.setOnClickListener(v -> {
            startActivity(new Intent(SecurityActivity.this, InternalActivity.class));
        });
    }
}