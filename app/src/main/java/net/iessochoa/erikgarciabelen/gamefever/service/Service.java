package net.iessochoa.erikgarciabelen.gamefever.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;

import java.util.HashMap;
import java.util.Map;

public class Service extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Service(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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
