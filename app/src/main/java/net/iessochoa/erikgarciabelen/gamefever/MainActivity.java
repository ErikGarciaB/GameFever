package net.iessochoa.erikgarciabelen.gamefever;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        /**
         * Passing each menu ID as a set of Ids because each
         * menu should be considered as top level destinations.
         */
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (user != null){
            Timestamp actualTime = Timestamp.now();
            Map<String, Object> lastTimeConnected = new HashMap<>();
            lastTimeConnected.put(FirebaseContract.UserEntry.LAST_TIME_CONNECTED, actualTime);
            db.collection(FirebaseContract.UserEntry.COLLECTION_NAME).document(user.getDisplayName()).update(lastTimeConnected);
        }
    }
}