package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }


    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Message model) {
        String userName = auth.getCurrentUser().getDisplayName();

        if (model.getBody() != null) {
            if (model.getUsername().equals(userName)) {
                holder.tvMessage.setBackgroundColor(R.color.app_dark_background);
                holder.tvMessage.setGravity(Gravity.RIGHT);

            } else {
                holder.tvMessage.setBackgroundColor(Color.RED);
                holder.tvMessage.setGravity(Gravity.LEFT);
            }
            holder.tvMessage.setText(model.getBody());

            holder.tvMessage.setOnClickListener(v -> {
                Log.e("TEST", holder.tvMessage.getText().toString());
            });
        }
    }

    /**
     * Create the viewHolder and is assigned to a layout
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatHolder(itemView);
    }

    /**
     * Create the viewholder of the recyclerView and create the behaviour of the components.
     */
    public class ChatHolder extends RecyclerView.ViewHolder {

        CardView cvContainer;
        TextView tvMessage;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            cvContainer = itemView.findViewById(R.id.cvContainer);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
