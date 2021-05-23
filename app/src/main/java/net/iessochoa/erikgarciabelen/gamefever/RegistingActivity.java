package net.iessochoa.erikgarciabelen.gamefever;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;

import java.util.HashMap;
import java.util.Map;

public class RegistingActivity extends AppCompatActivity {

    private EditText etRegName, etRegPassword, etRegMail;
    private Button btRegisterAccount;
    private TextView tvRegAlert, tvHaveAccount;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registing);
        initializeComponents();

        btRegisterAccount.setOnClickListener(v -> {
            if (checkFields()) checkRegister(etRegName.getText().toString());
        });
        tvHaveAccount.setOnClickListener(v -> finish());
    }

    private void showMessageError(){
        tvRegAlert.setVisibility(View.VISIBLE);
    }

    private void hideMessageError(){
        tvRegAlert.setVisibility(View.INVISIBLE);
    }

    /**
     * Check if the username written is unique.
     * If the username is unique, the user can register in the app.
     * If not a message error appears.
     * @param name is the String to validate if it is unique.
     */
    private void checkRegister(String name) {
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference docRef = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()){
                    hideMessageError();
                    registerUser();
                } else
                    showMessageError();
                    progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * Register the user in the app, create a firebase document to him and start the MainActivity.
     */
    private void registerUser() {
        String email, password;
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

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put(FirebaseContract.UserEntry.NAME, name);

                    db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                            .document(name).set(userMap);

                    Intent intent = new Intent(RegistingActivity.this, MainActivity.class);
                    progressBar.setVisibility(View.VISIBLE);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    /**
     * Check if username, email adress and password fields are empty.
     * @return false if one of them is empty. Return true if they are filled.
     */
    private boolean checkFields() {
        boolean bool = true;

        if (TextUtils.isEmpty(etRegName.getText())) {
            etRegName.setHint(R.string.no_name);
            bool = false;
        }
        if (TextUtils.isEmpty(etRegPassword.getText())) {
            etRegPassword.setHint(R.string.no_password);
            bool = false;
        }
        if (TextUtils.isEmpty(etRegMail.getText())) {
            etRegMail.setHint(R.string.no_mail);
            bool = false;
        }
        if (etRegPassword.length() < 6){
            Toast.makeText(this, R.string.short_password, Toast.LENGTH_SHORT).show();
            etRegPassword.setText("");
            bool = false;
        }
        if (etRegName.length() > 12){
            Toast.makeText(this, R.string.long_username, Toast.LENGTH_SHORT).show();
            etRegName.setText("");
            bool = false;
        }
        return bool;
    }

    /**
     * Initialize the components.
     */
    private void initializeComponents() {
        etRegName = findViewById(R.id.etRegName);
        etRegMail = findViewById(R.id.etRegMail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btRegisterAccount = findViewById(R.id.btRegisterAccount);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);
        tvRegAlert = findViewById(R.id.tvRegAlert);
        progressBar = findViewById(R.id.progressBar);
    }
}