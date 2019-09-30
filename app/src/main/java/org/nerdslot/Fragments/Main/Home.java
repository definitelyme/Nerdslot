package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Adapters.IssueAdapter;
import org.nerdslot.R;
import org.nerdslot.ViewModels.IssueViewModel;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


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

    private IssueAdapter adapter;

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

        if (toolbar.hasExpandedActionView())
            Log.i("log-tag", "onViewCreated: Expanded Action View");

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        showPermissionDialog();
    }

    private void showPermissionDialog() {
        //Create an Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Set Custom layout
        final View customAlert = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        builder.setView(customAlert);

        // Create the Alert Dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = customAlert.findViewById(R.id.dialog_title);
        title.setText(getString(R.string.permission_title_text));

        customAlert.findViewById(R.id.dialog_btn).setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public boolean permissionGranted() {
        int externalStoragePermissionResult = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int writeExternalStoragePermissionResult = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        return externalStoragePermissionResult != -1 && writeExternalStoragePermissionResult != -1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IssueViewModel viewModel = ViewModelProviders.of(activity).get(IssueViewModel.class);
        viewModel.getAllIssues().observe(this, issues -> {
            adapter = new IssueAdapter();
            recyclerView.setAdapter(adapter);
            adapter.setIssues(issues);
        });
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
