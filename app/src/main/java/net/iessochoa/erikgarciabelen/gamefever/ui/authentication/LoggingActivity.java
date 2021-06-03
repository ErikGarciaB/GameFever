package net.iessochoa.erikgarciabelen.gamefever.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.MainActivity;
import net.iessochoa.erikgarciabelen.gamefever.R;

public class LoggingActivity extends AppCompatActivity{

    private EditText etLoginName, etLoginPassword;
    private TextView tvLoginAlert, tvNoAccount;
    private Button btLogin, btLoginGoogle;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;


    /**
     * Initialize the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        initializeComponents();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        /**
         * Detect if the mobile has internet. If the mobile doesn't have internet the application shut down.
         */
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.no_intenet_alert).setMessage(R.string.no_internet_message);
            builder.setOnDismissListener(dialog -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            });
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            }).show();
        }

        /**
         * If firebase has a user, skip the login screen.
         */
        if (auth.getCurrentUser() != null) {
            initializeMainApp();
        }

        /**
         * When the login button is pressed, the app check if the name and password fields
         * are filled. If they are filled, firebase try to log the user.
         */
        btLogin.setOnClickListener(v -> {
            String email, password;

            hideKeyword();
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
                        }).addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.sign_in_error, Toast.LENGTH_SHORT).show();
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
        finish();
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


    /**
     * Hide the keyword of the smartphone
     */
    private void hideKeyword() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etLoginPassword.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(etLoginName.getWindowToken(), 0);
        }
    }

}