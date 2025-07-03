package com.example.chuong10.ui.dulieu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chuong10.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InternalActivity extends AppCompatActivity {
    private EditText editTextInternal, editTextSharedPrefs;
    private TextView textViewInternalResult, textViewSharedPrefsResult;
    private static final String INTERNAL_FILE_NAME = "sensitive_data.txt";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREFS_KEY = "sensitive_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_internal);

        // Initialize views
        editTextInternal = findViewById(R.id.editTextInternal);
        editTextSharedPrefs = findViewById(R.id.editTextSharedPrefs);
        textViewInternalResult = findViewById(R.id.textViewInternalResult);
        textViewSharedPrefsResult = findViewById(R.id.textViewSharedPrefsResult);
        Button buttonSaveInternal = findViewById(R.id.buttonSaveInternal);
        Button buttonReadInternal = findViewById(R.id.buttonReadInternal);
        Button buttonSavePrefs = findViewById(R.id.buttonSavePrefs);
        Button buttonReadPrefs = findViewById(R.id.buttonReadPrefs);

        // Handle Internal Storage save
        buttonSaveInternal.setOnClickListener(v -> {
            String data = editTextInternal.getText().toString();
            try (FileOutputStream fos = openFileOutput(INTERNAL_FILE_NAME, MODE_PRIVATE)) {
                fos.write(data.getBytes());
                Toast.makeText(this, "Data saved to Internal Storage", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Internal Storage read
        buttonReadInternal.setOnClickListener(v -> {
            try (FileInputStream fis = openFileInput(INTERNAL_FILE_NAME)) {
                StringBuilder data = new StringBuilder();
                int ch;
                while ((ch = fis.read()) != -1) {
                    data.append((char) ch);
                }
                textViewInternalResult.setText(data.toString());
            } catch (IOException e) {
                Toast.makeText(this, "Error reading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle SharedPreferences save
        buttonSavePrefs.setOnClickListener(v -> {
            String data = editTextSharedPrefs.getText().toString();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_KEY, data);
            editor.apply();
            Toast.makeText(this, "Data saved to SharedPreferences", Toast.LENGTH_SHORT).show();
        });

        // Handle SharedPreferences read
        buttonReadPrefs.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String data = prefs.getString(PREFS_KEY, "No data found");
            textViewSharedPrefsResult.setText(data);
        });

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}