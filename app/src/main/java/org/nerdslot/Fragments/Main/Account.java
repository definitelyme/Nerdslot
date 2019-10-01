package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;

import org.nerdslot.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainInterface} interface
 * to handle interaction events.
 */
public class Account extends Fragment {

    private MainInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;

    public Account() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialToolbar toolbar = view.findViewById(R.id.account_toolbar);
        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = ((AppCompatActivity) getActivity());

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.main_fragments);

        if (context instanceof MainInterface) mListener = (MainInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement MainInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupActionBar(NavController navController) {
        NavigationUI.setupActionBarWithNavController(activity, navController);
    }
}
