package org.nerdslot.Fragments.Admin.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.nerdslot.Fragments.Admin.AdminInterface;
import org.nerdslot.R;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Home extends Fragment implements AdminInterface {

    private static final int REQUEST_PERMISSIONS = 0;
    private int REQUEST_GRANTED = 0;
    private AdminInterface mListener;
    private AppCompatActivity activity;

    public Home() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        if (!permissionGranted())
            showPermissionDialog();

        if (context instanceof AdminInterface) mListener = (AdminInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement AdminInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
