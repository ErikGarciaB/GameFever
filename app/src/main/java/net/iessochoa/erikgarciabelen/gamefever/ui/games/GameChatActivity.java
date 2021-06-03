package net.iessochoa.erikgarciabelen.gamefever.ui.games;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.ChatAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;
import net.iessochoa.erikgarciabelen.gamefever.model.TicTacToe;
import net.iessochoa.erikgarciabelen.gamefever.ui.fragments.FriendsFragment;

public class GameChatActivity extends AppCompatActivity {

    TextView tvGameChatWith;
    ChatAdapter adapter;
    RecyclerView rvGameChat;
    EditText etGameMessage;
    Button btGameChatReturn;
    ImageButton btGameSend;
    TicTacToe ttt;
    String pk, username1, username2;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_chat);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

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

        initializeComponents();

        btGameSend.setOnClickListener(v -> sendMessage());
        btGameChatReturn.setOnClickListener(v -> finish());
    }

    /**
     * Initialize the components from the activity.
     */
    private void initializeComponents() {
        ttt = getIntent().getParcelableExtra(FriendsFragment.EXTRA_FRIENDS);
        pk = ttt.getId();
        tvGameChatWith = findViewById(R.id.tvGameChatWith);
        rvGameChat = findViewById(R.id.rvGameChat);
        rvGameChat.setLayoutManager(new LinearLayoutManager(this));

        String[] strings = pk.split("-");

        username1 = strings[1];
        username2 = strings[2];

        String rivalName = (ttt.getPlayer1().getName().equals(auth.getCurrentUser().getDisplayName()))
                ? ttt.getPlayer2().getName() : ttt.getPlayer1().getName();

        tvGameChatWith.setText(String.format(getString(R.string.chat_of), rivalName));

        etGameMessage = findViewById(R.id.etGameMessage);
        btGameChatReturn = findViewById(R.id.btGameChatReturn);
        btGameSend = findViewById(R.id.btGameSend);

        initializeGameChatAdapter();
    }

    /**
     * Initialize the game chat adapter and create the query to obtain all the message from the relation.
     */
    private void initializeGameChatAdapter() {
        Query q = db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                .document(pk)
                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                .orderBy(FirebaseContract.ChatEntry.DATE, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Message> options = new
                FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(q, Message.class)
                .setLifecycleOwner(this)
                .build();

        if (adapter != null)
            adapter.stopListening();

        adapter = new ChatAdapter(options, getApplicationContext());
        rvGameChat.setAdapter(adapter);

        adapter.startListening();

        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                rvGameChat.smoothScrollToPosition(0);
            }

            @Override
            public void onDataChanged() {

            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {

            }
        });
    }

    /**
     * Create a message in the sub-collection of the Game and remove the text from the editText
     */
    private void sendMessage() {
        String body = etGameMessage.getText().toString();
        if (!body.isEmpty()) {
            Message m = new Message(body, auth.getCurrentUser().getDisplayName());
            db.collection(FirebaseContract.TicTacToeGameEntry.COLLECTION_NAME)
                    .document(pk).collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                    .add(m);
            etGameMessage.setText("");
            hideKeyword();
        }
    }

    /**
     * Hide the keyword of the smartphone
     */
    private void hideKeyword() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etGameMessage.getWindowToken(), 0);
        }
    }
}