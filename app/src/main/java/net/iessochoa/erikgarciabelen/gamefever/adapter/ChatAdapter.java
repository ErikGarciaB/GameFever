package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.Message;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, ChatAdapter.ChatHolder> {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;
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
                String chatMessage = model.getBody();
                SpannableString message = new SpannableString(chatMessage);
                message.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.app_dark_background)), 0, chatMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvMessage.setGravity(Gravity.RIGHT);
                holder.tvMessage.setText(message);
            } else{
                String chatMessage = model.getBody();
                SpannableString message = new SpannableString(chatMessage);
                message.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context, R.color.app_altDark_background)), 0, chatMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvMessage.setGravity(Gravity.LEFT);
                holder.tvMessage.setText(message);
            }

        }
    }

    /**
     * Create the view holder and assign it to the layout item
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatHolder(itemView);
    }
    /**
     * Create the viewholder of the recyclerView and create the components.
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
