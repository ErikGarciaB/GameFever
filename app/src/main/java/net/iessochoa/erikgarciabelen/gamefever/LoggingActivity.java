package net.iessochoa.erikgarciabelen.gamefever;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoggingActivity extends AppCompatActivity{

    private EditText etLoginName, etLoginPassword;
    private TextView tvLoginAlert, tvNoAccount;
    private Button btLogin;
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    /**
     * Initialize the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        initializeComponents();

        /**
         * If firebase has a user, skip the login screen.
         */
        if (auth.getCurrentUser() != null) {
            initializeMainApp();
            finish();
        }

        /**
         * When the login button is pressed, the app check if the name and password fields
         * are filled. If they are filled, firebase try to log the user.
         */
        btLogin.setOnClickListener(v -> {
            String email, password;


            email = etLoginName.getText().toString();
            password = etLoginPassword.getText().toString();
            if (checkFields()) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    initializeMainApp();
                                } else {
                                    tvLoginAlert.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
        /**
         * When the No Account button is pressed, the RegistingActivity is created and started.
         */
        tvNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegistingActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Create and start the MainAppActivity
     */
    private void initializeMainApp(){
        Intent intent = new Intent(LoggingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Initialize the component of the view
     */

    private void initializeComponents() {
        etLoginName = findViewById(R.id.etLoginMail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btLogin = findViewById(R.id.btLogin);
        tvLoginAlert = findViewById(R.id.tvLoginAlert);
        tvNoAccount = findViewById(R.id.tvNoAccount);
    }

    /**
     * Check if username and password fields are empty.
     * @return false if one of them is empty. Return true if they are filled.
     */

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