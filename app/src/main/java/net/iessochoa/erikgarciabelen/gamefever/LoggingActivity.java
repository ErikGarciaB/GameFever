package net.iessochoa.erikgarciabelen.gamefever;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoggingActivity extends AppCompatActivity {

    private EditText etLoginName, etLoginPassword;
    private TextView tvAviso, tvNoAccount;
    private Button btIniciarSesion;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        initializeComponents();

        btIniciarSesion.setOnClickListener(v -> {
            String email, password;


            email = etLoginName.getText().toString();
            password = etLoginPassword.getText().toString();
            if (checkFields()) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String message = String.format(getResources().getString(R.string.welcome_message), auth.getCurrentUser().getDisplayName());
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                                    Intent intent = new Intent(LoggingActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    tvAviso.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
        tvNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistingActivity.class);
            startActivity(intent);
        });
    }

    private void initializeComponents() {
        etLoginName = findViewById(R.id.etLoginMail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btIniciarSesion = findViewById(R.id.btIniciarSesion);
        tvAviso = findViewById(R.id.tvAviso);
        tvNoAccount = findViewById(R.id.tvNoAccount);
    }

    private boolean checkFields() {
        boolean bool = true;
        if (TextUtils.isEmpty(etLoginName.getText())) {
            etLoginName.setHint(R.string.no_mail);
            bool = false;
        }
        if (TextUtils.isEmpty(etLoginPassword.getText())) {
            etLoginPassword.setHint(R.string.no_password);
            bool = false;
        }
        return bool;
    }

}