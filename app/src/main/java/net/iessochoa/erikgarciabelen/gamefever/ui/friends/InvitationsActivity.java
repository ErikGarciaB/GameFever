package net.iessochoa.erikgarciabelen.gamefever.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.adapter.InvitationAdapter;
import net.iessochoa.erikgarciabelen.gamefever.model.FirebaseContract;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;
import net.iessochoa.erikgarciabelen.gamefever.model.Invitation;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;
import net.iessochoa.erikgarciabelen.gamefever.ui.fragments.FriendsFragment;

import java.util.ArrayList;

public class InvitationsActivity extends AppCompatActivity {

    RecyclerView rvInvitation;
    Button btInvitationReturn;
    final InvitationAdapter adapter = new InvitationAdapter();
    ArrayList<Invitation> invitations;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Create the view of the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);

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
        initializeComponents();
        /**
         * Finish the activity
         */
        btInvitationReturn.setOnClickListener(v -> finish());
    }



    /**
     * Initialize the components from the activity.
     */
    private void initializeComponents(){
        rvInvitation = findViewById(R.id.rvInvitation);

        btInvitationReturn = findViewById(R.id.btInvitationReturn);

        rvInvitation.setLayoutManager(new LinearLayoutManager(this));
        rvInvitation.setAdapter(adapter);

        invitations = getIntent().getExtras().getParcelableArrayList(FriendsFragment.EXTRA_INVITATION);
        adapter.setInvitationList(invitations);

        adapter.setOnClickAcceptListener(invitation -> {
            acceptInvitation(invitation);
        });

        adapter.setOnClickDenyListener(invitation -> {
            denyInvitation(invitation);
        });
    }

    /**
     *  A friend relation is created in the database with the user selected and the invitation is deleted from the database.
     */
    private void acceptInvitation(Invitation invitation){
        CollectionReference collectionReference = db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME);
        collectionReference.whereEqualTo(FirebaseContract.InvitationEntry.HOST_USER, invitation.getHostUser().getName())
                .whereEqualTo(FirebaseContract.InvitationEntry.INVITED_USER, auth.getCurrentUser().getDisplayName());

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invitations.remove(invitation);
                for (QueryDocumentSnapshot document: task.getResult()) {
                    String primaryKey = invitation.getHostUser().getName() + "-" + invitation.getInvitedUser().getName();
                    FriendRelation fr = new FriendRelation(invitation.getHostUser(), invitation.getInvitedUser(), primaryKey);
                    db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).document(primaryKey).set(fr);
                    db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).document(primaryKey)
                            .collection(FirebaseContract.ChatEntry.COLLECTION_NAME).document().set(new Message());
                    document.getReference().delete();
                    adapter.setInvitationList(invitations);
                }
                Toast.makeText(InvitationsActivity.this, R.string.invitation_accepted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * The invitation is deleted in the database
     */
    private void denyInvitation(Invitation invitation){
        CollectionReference collectionReference = db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME);
        collectionReference.whereEqualTo(FirebaseContract.InvitationEntry.HOST_USER, invitation.getHostUser().getName())
                .whereEqualTo(FirebaseContract.InvitationEntry.INVITED_USER, auth.getCurrentUser().getDisplayName());

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invitations.remove(invitation);
                for (QueryDocumentSnapshot document: task.getResult()) {
                    document.getReference().delete();
                    adapter.setInvitationList(invitations);
                }
                Toast.makeText(InvitationsActivity.this, R.string.invitation_denied, Toast.LENGTH_SHORT).show();
            }
        });
    }
}