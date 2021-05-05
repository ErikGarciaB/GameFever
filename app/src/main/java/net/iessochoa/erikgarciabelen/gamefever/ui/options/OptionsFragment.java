package net.iessochoa.erikgarciabelen.gamefever.ui.options;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import net.iessochoa.erikgarciabelen.gamefever.LoggingActivity;
import net.iessochoa.erikgarciabelen.gamefever.R;

public class OptionsFragment extends Fragment {

    private OptionsViewModel optionsViewModel;
    private Button btCerrarSesion;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        optionsViewModel =
                new ViewModelProvider(this).get(OptionsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_options, container, false);
        initializeComponents(root);

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