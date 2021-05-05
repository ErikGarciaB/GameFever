package net.iessochoa.erikgarciabelen.gamefever.ui.invitations;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

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
import net.iessochoa.erikgarciabelen.gamefever.ui.friends.FriendsFragment;

import java.util.ArrayList;

public class InvitationsActivity extends AppCompatActivity {

    RecyclerView rvInvitation;
    Button btInvitationReturn;
    final InvitationAdapter adapter = new InvitationAdapter();
    ArrayList<Invitation> invitations;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations);
        initializeComponents();
        btInvitationReturn.setOnClickListener(v -> finish());
    }

    private void initializeComponents(){
        rvInvitation = findViewById(R.id.rvInvitations);

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
     *
     */
    private void acceptInvitation(Invitation invitation){
        CollectionReference collectionReference = db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME);
        collectionReference.whereEqualTo(FirebaseContract.InvitationEntry.HOST_USER, invitation.getHostUser().getName())
                .whereEqualTo(FirebaseContract.InvitationEntry.INVITED_USER, auth.getCurrentUser().getDisplayName());

        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                invitations.remove(invitation);
                for (QueryDocumentSnapshot document: task.getResult()) {
                    FriendRelation fr = new FriendRelation(invitation.getHostUser(), invitation.getInvitedUser());
                    db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).document().set(fr);
                    db.collection(FirebaseContract.FriendRelation.COLLECTION_NAME).whereEqualTo(FirebaseContract.FriendRelation.USER_1, invitation.getHostUser())
                            .whereEqualTo(FirebaseContract.FriendRelation.USER_2, invitation.getInvitedUser()).get().addOnCompleteListener(task1 -> {
                                task.getResult().getDocuments().get(0).getReference().collection("Chat").document("DONT").set(new Message());
                    });

                    document.getReference().delete();
                    adapter.setInvitationList(invitations);
                }
                Toast.makeText(InvitationsActivity.this, R.string.invitation_accepted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    private void denyInvitation(Invitation invitation){
        CollectionReference collectionReference = db.collection(FirebaseContract.InvitationEntry.COLLECTION_NAME);
        collectionReference.whereEqualTo(FirebaseContract.InvitationEntry.HOST_USER, invitation.getHostUser().getName())
                .whereEqualTo(FirebaseContract.InvitationEntry.INVITED_USER, auth.getCurrentUser().getDisplayName());


        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    document.getReference().delete();
                }
                Toast.makeText(InvitationsActivity.this, R.string.invitation_denied, Toast.LENGTH_SHORT).show();
            }
        });
    }
}