package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.core.content.res.ResourcesCompat;
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

    private static final int REQUEST_PERMISSIONS = 0;
    private int REQUEST_GRANTED = 0;
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
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupActionBar(navController);
        activity.getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, activity.getTheme()));

        cartButton.setOnClickListener(v -> {
            mListener.sendSnackbar(container, "Cart icon clicked!");
        });

        if (toolbar.hasExpandedActionView())
            Log.i("log-tag", "onViewCreated: Expanded Action View");


        adapter = new IssueAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (!permissionGranted())
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
        dialog.setCancelable(false);
        dialog.show();

        TextView title = customAlert.findViewById(R.id.dialog_title);
        title.setText(getString(R.string.default_permission_title_text));

        TextView description = customAlert.findViewById(R.id.dialog_description);
        description.setText(getString(R.string.storage_permission_desc));

        customAlert.findViewById(R.id.dialog_btn).setOnClickListener(v -> {
            dialog.dismiss();
            requestPermission();
        });
    }

    private boolean permissionGranted() {
        int storagePermission = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        return storagePermission != -1;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (activity.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // NOTE: 0 ---- means Permission granted. -1 ----- means Permission not granted.
        if (requestCode == REQUEST_PERMISSIONS) {
            for (int grantResult : grantResults) { // Go through the results
                REQUEST_GRANTED = grantResult; // Set the REQUEST_GRANTED variable for the current Permission

                if (grantResult != PackageManager.PERMISSION_GRANTED) { // If a Permission is not set, break
                    break;
                }
            }

            if (REQUEST_GRANTED == -1 && shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))
                showPermissionDialog();

            else if (REQUEST_GRANTED == -1) {
                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                settingsIntent.setData(uri);
                startActivityForResult(settingsIntent, 101);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IssueViewModel viewModel = ViewModelProviders.of(activity).get(IssueViewModel.class);
        viewModel.getAllIssues().observe(this, issues -> {
            adapter.setIssues(issues);
            recyclerView.setAdapter(adapter);
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
