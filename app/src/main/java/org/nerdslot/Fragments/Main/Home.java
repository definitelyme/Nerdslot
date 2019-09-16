package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Adapters.IssueAdapter;
import org.nerdslot.R;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainInterface} interface
 * to handle interaction events.
 */
public class Home extends Fragment {

    private MainInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;

    // Views
    private WaveSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View container;
    private ImageButton cartButton;

    public Home() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.collapsing_toolbar);
        recyclerView = view.findViewById(R.id.issues_recycler_view);
        cartButton = view.findViewById(R.id.action_cart);
        container = activity.findViewById(R.id.main_activity);

        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);

        cartButton.setOnClickListener(v -> {
            mListener.sendSnackbar(container, "Cart icon clicked!");
        });

        if (toolbar.hasExpandedActionView()) Log.i("log-tag", "onViewCreated: Expanded Action View");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        if (activity != null) navController = Navigation.findNavController(activity, R.id.main_fragments);

        if (context instanceof MainInterface) mListener = (MainInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement MainInterface");
    }

    @Override
    public void onResume() {
        super.onResume();
        IssueAdapter adapter = new IssueAdapter(activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
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
