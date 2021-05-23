package net.iessochoa.erikgarciabelen.gamefever.ui.friends;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;

public class ChatActivity extends AppCompatActivity {

    TextView tvChatWith;
    ChatAdapter adapter;
    RecyclerView rvChat;
    EditText etMessage;
    Button btChatReturn;
    ImageButton btSend;
    FriendRelation fr;
    String pk;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeComponents();

        btSend.setOnClickListener(v -> {
            sendMessage();
        });

        btChatReturn.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Initialize the components from the activity.
     */
    private void initializeComponents() {
        fr = getIntent().getParcelableExtra(FriendsFragment.EXTRA_FRIENDS);
        String friendName = (fr.getUser1().getName().equals(auth.getCurrentUser().getDisplayName()))
                ? fr.getUser2().getName() : fr.getUser1().getName();
        tvChatWith = findViewById(R.id.tvChatWtih);
        tvChatWith.setText(String.format(getString(R.string.chat_of), friendName));
        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        btChatReturn = findViewById(R.id.btChatReturn);

        initializeChatAdapter();

    }

    /**
     * Initialize the chat adapter and create the query to obtain all the message from the relation.
     */
    private void initializeChatAdapter() {
        pk = fr.getUser1().getName() + "-" + fr.getUser2().getName();

        Query q = db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME)
                .document(pk)
                .collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                .orderBy(FirebaseContract.ChatEntry.DATE, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Message> options = new
                FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(q, Message.class)
                .setLifecycleOwner(this)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }
        adapter = new ChatAdapter(options);
        rvChat.setAdapter(adapter);

        adapter.startListening();

        adapter.getSnapshots().addChangeEventListener(new ChangeEventListener() {
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                rvChat.smoothScrollToPosition(0);
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
     * Create a message in the sub-collection of the friend relation and remove the text from the editText
     */
    private void sendMessage() {
        String body = etMessage.getText().toString();
        if (!body.isEmpty()) {
            Message m = new Message(body, auth.getCurrentUser().getDisplayName());
            db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME)
                    .document(pk).collection(FirebaseContract.ChatEntry.COLLECTION_NAME)
                    .add(m);
            etMessage.setText("");
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
            imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}