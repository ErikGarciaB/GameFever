package net.iessochoa.erikgarciabelen.gamefever;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistingActivity extends AppCompatActivity {

    private EditText etRegName, etRegPassword, etRegMail;
    private Button btRegistrarCuenta;
    private TextView tvRegAviso, tvHaveAccount;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registing);
        initializeComponents();

        btRegistrarCuenta.setOnClickListener(v -> registerUser());
        tvHaveAccount.setOnClickListener(v -> finish());
    }


    private void initializeComponents(){
        etRegName = findViewById(R.id.etRegName);
        etRegMail = findViewById(R.id.etRegMail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btRegistrarCuenta = findViewById(R.id.btRegistrarCuenta);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);
        tvRegAviso = findViewById(R.id.tvRegAviso);
    }

    private void registerUser(){
        String email, password;
        if (checkFields()){
            email = etRegMail.getText().toString();
            password = etRegPassword.getText().toString();
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String name = etRegName.getText().toString();
                        FirebaseUser user = auth.getCurrentUser();
                        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        user.updateProfile(update);
                        Intent intent = new Intent(RegistingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean checkFields(){
        boolean bool = true;
        if (TextUtils.isEmpty(etRegName.getText())) {
            etRegName.setHint(R.string.no_name);
            bool = false;
        }
        if (TextUtils.isEmpty(etRegPassword.getText())){
            etRegPassword.setHint(R.string.no_password);
            bool = false;
        }
        if (TextUtils.isEmpty(etRegMail.getText())){
            etRegMail.setHint(R.string.no_mail);
            bool = false;
        }
        return bool;
    }
}