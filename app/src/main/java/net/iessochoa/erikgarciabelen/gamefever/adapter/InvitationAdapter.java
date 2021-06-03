package net.iessochoa.erikgarciabelen.gamefever.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.model.Invitation;

import java.util.ArrayList;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {
    private ArrayList<Invitation> invitationList;
    private OnItemClickAcceptListener listenerAccept;
    private OnItemClickDenyListener listenerDeny;


    /**
     * Create the viewHolder and is assigned to a layout
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public InvitationAdapter.InvitationViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invitations, parent, false);
        return new InvitationViewHolder(itemView);
    }

    /**
     * Bind the information of the invitation to layout's component
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        if (invitationList != null){
            Invitation invitation = invitationList.get(position);
            holder.tvName.setText(invitation.getHostUser().getName());
        }
    }

    /**
     * Get the count of the list
     * @return the count of the list
     */
    @Override
    public int getItemCount() {
        if (invitationList != null){
            return  invitationList.size();
        }
        else return 0;
    }

    /**
     * Setter of the invitationList.
     * @param invitationList
     */
    public void setInvitationList(ArrayList<Invitation> invitationList){
        this.invitationList = invitationList;
        notifyDataSetChanged();
    }

    /**
     * Create the viewholder of the recyclerView and create the components.
     */
    public class InvitationViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private Button btAccept;
        private Button btDeny;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btAccept = itemView.findViewById(R.id.btAccept);
            btDeny = itemView.findViewById(R.id.btDeny);

            /**
             * Create the listeners of the buttons
             */
            btAccept.setOnClickListener(v -> {
                if (listenerAccept != null)
                    listenerAccept.onItemAcceptClick(invitationList.get(InvitationViewHolder.this.getAdapterPosition()));
            });

            btDeny.setOnClickListener(v -> {
                if (listenerDeny != null)
                    listenerDeny.onItemDenyClick(invitationList.get(InvitationViewHolder.this.getAdapterPosition()));
            });
        }
    }

    // Intefaces
    public interface OnItemClickAcceptListener {
        void onItemAcceptClick(Invitation invitation);
    }

    public interface OnItemClickDenyListener{
        void onItemDenyClick(Invitation invitation);
    }

    // Setters of interfaces
    public void setOnClickAcceptListener(OnItemClickAcceptListener listenerAccept){
        this.listenerAccept = listenerAccept;
    }

    public void setOnClickDenyListener(OnItemClickDenyListener listenerDeny){
        this.listenerDeny = listenerDeny;
    }

}
