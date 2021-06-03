package net.iessochoa.erikgarciabelen.gamefever.ui.options;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.HistoryAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.History;

public class HistoryActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button btHistoryReturn;
    HistoryAdapter adapter;
    RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        /**
         * Detect if the mobile has internet. If the mobile doesn't have internet the application shut down.
         */
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
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

        initializeComponents();

        /**
         * Finish the activity
         */
        btHistoryReturn.setOnClickListener(v -> finish());

    }

    /**
     * Create the components of the activity
     */
    private void initializeComponents(){
        btHistoryReturn = findViewById(R.id.btHistoryReturn);
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        initializeHistoryAdapter();
    }

    /**
     * Initialize the chat adapter and create the query to obtain all the games played by the user.
     */
    private void initializeHistoryAdapter(){
        String userName = auth.getCurrentUser().getDisplayName();

        Query q = db.collection(FirebaseContract.UserEntry.COLLECTION_NAME)
                .document(userName)
                .collection(FirebaseContract.HistoryEntry.COLLECTION_NAME)
                .orderBy(FirebaseContract.HistoryEntry.DATE, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<History> options = new
                FirestoreRecyclerOptions.Builder<History>()
                .setQuery(q, History.class)
                .setLifecycleOwner(this)
                .build();

        if(adapter != null)
            adapter.stopListening();

        adapter = new HistoryAdapter(options, this);
        rvHistory.setAdapter(adapter);

        adapter.startListening();
    }
}