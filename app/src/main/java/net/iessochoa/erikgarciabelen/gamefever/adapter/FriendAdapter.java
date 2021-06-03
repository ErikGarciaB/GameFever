package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.FriendRelation;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<FriendRelation> friendList;

    private OnItemClickChatListener listenerChat;
    private OnItemClickExpandListener listenerExpand;
    private OnItemClickDeleteListener listenerDelete;

    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friends, parent, false);
        return new FriendViewHolder(itemView);
    }

    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        if (friendList != null){
            FriendRelation fr = friendList.get(position);
            String friendName = (fr.getUser1().getName().equals(auth.getCurrentUser().getDisplayName()))
                    ? fr.getUser2().getName() : fr.getUser1().getName();
            holder.tvFriendName.setText(friendName);
        }
    }

    /**
     * Get the count of the list
     * @return the count of the list
     */
    @Override
    public int getItemCount() {
        if (friendList != null){
            return friendList.size();
        }
        else return 0;
    }

    /**
     * Setter of the friend list
     * @param friendList
     */
    public void setFriendList(ArrayList<FriendRelation> friendList){
        this.friendList = friendList;
        notifyDataSetChanged();
    }


    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class FriendViewHolder extends RecyclerView.ViewHolder{

        private TextView tvFriendName;
        private Button btChat, btDelete;
        private ImageView ivExpand;
        private LinearLayout vlOptions;
        private CardView cdFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendName = itemView.findViewById(R.id.tvFriendName);
            btChat = itemView.findViewById(R.id.btChat);
            btDelete = itemView.findViewById(R.id.btDelete);
            ivExpand = itemView.findViewById(R.id.ivExpand);
            vlOptions = itemView.findViewById(R.id.vlOptions);
            cdFriend = itemView.findViewById(R.id.cdFriend);

            /**
             * Create the listeners of the buttons
             */
            btChat.setOnClickListener(v -> {
                if (listenerChat != null){
                    listenerChat.onItemChatClick(friendList.get(FriendViewHolder.this.getAdapterPosition()));
                }
            });

            btDelete.setOnClickListener(v ->{
                if (listenerDelete != null){
                    listenerDelete.onItemInvitationClick(friendList.get(FriendViewHolder.this.getAdapterPosition()));
                }
            });

            ivExpand.setOnClickListener(v -> {
                if (listenerExpand != null){
                    listenerExpand.onItemExpandClick(vlOptions, ivExpand, cdFriend);
                }
            });
        }
    }
    // Intefaces
    public interface OnItemClickExpandListener {
        void onItemExpandClick(LinearLayout vlOptions, ImageView ivExpand, CardView cdFriend);
    }

    public interface OnItemClickDeleteListener {
        void onItemInvitationClick(FriendRelation fr);
    }

    public interface OnItemClickChatListener {
        void onItemChatClick(FriendRelation fr);
    }

    // Setters of interfaces
    public void setOnChatListener(OnItemClickChatListener listenerChat) {
        this.listenerChat = listenerChat;
    }

    public void setOnExpandListener(OnItemClickExpandListener listenerExpand) {
        this.listenerExpand = listenerExpand;
    }

    public void setOnDeleteListener(OnItemClickDeleteListener listenerInvitation) {
        this.listenerDelete = listenerInvitation;
    }
}
