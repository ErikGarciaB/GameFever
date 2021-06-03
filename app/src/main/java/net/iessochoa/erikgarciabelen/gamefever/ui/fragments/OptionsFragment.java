package net.iessochoa.erikgarciabelen.gamefever.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.ui.authentication.LoggingActivity;
import net.iessochoa.erikgarciabelen.gamefever.R;
import net.iessochoa.erikgarciabelen.gamefever.ui.options.HistoryActivity;

public class OptionsFragment extends Fragment {

    private Button btSignOut, btHistory;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private TextView tvUsername, tvEmail;

    /**
     * Create the view of the fragment activity
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_options, container, false);
        initializeComponents(root);

        tvEmail.setText(auth.getCurrentUser().getEmail());
        tvUsername.setText(auth.getCurrentUser().getDisplayName());



        /**
         * Log out the user and start the login activity
         */
        btSignOut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.sign_out_confirm)
                    .setTitle(R.string.sign_out);

            builder.setPositiveButton(R.string.yes,(dialog, which) -> {
                auth.signOut();
                startActivity(new Intent(getActivity(), LoggingActivity.class));
            });

            builder.setNegativeButton(R.string.no, (dialog, which) -> {

            });
            
            builder.show();

        });

        /**
         * Launch the history activity
         */
        btHistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), HistoryActivity.class)));


        return root;
    }

    /**
     * Initialize the components of the Activity.
     *
     * @param root Is used to initialize the components.
     */
    private void initializeComponents(View root) {
        btSignOut = root.findViewById(R.id.btLogOut);
        btHistory = root.findViewById(R.id.btHistory);
        tvEmail = root.findViewById(R.id.tvEmail);
        tvUsername = root.findViewById(R.id.tvUsername);
    }


}