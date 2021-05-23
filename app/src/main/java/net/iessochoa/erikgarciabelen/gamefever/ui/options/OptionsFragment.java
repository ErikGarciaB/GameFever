package net.iessochoa.erikgarciabelen.gamefever.ui.options;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.LoggingActivity;
import net.iessochoa.erikgarciabelen.gamefever.R;

public class OptionsFragment extends Fragment {

    private Button btCerrarSesion;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Create the view of the fragment activity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_options, container, false);
        initializeComponents(root);


        /**
         * Log out the user and start the login activity
         */
        btCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity(), LoggingActivity.class));
            }
        });
        return root;
    }

    /**
     * Initialize the components of the Activity.
     * @param root Is used to initialize the components.
     */
    private void initializeComponents(View root){
        btCerrarSesion = root.findViewById(R.id.btLogOut);
    }


}