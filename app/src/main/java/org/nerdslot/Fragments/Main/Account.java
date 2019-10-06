package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.nerdslot.Foundation.Helper.DownloadTaskManager;
import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Foundation.Helper.Upload;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.User.User;
import org.nerdslot.Network.ConnectionManager;
import org.nerdslot.R;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainInterface} interface
 * to handle interaction events.
 */
public class Account extends Fragment implements RootInterface, View.OnClickListener, ValueEventListener {

    private static boolean isAdmin;
    private ADMIN_STATE loggedInAs;
    private MainInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;
    private File image;

    // Helpers
    private Upload imageUpload;
    private User user;

    // Views
    private MaterialButton switchAccount, emailTextView, phoneTextView, genderTextView, dobTextView, signOutBtn;
    private Group profileLayout;
    private MaterialTextView resetPasswordBtn, deleteAccountBtn;
    private TextView nameTextView;
    private ImageView imageView;
    private View rootView;

    public Account() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUpload = new Upload(this);
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

        findViewsById(view);

        if (isAdmin) {
            setVisibility(switchAccount, View.VISIBLE);
            resetView(switchAccount, getString(R.string.login_as_user_string));
        }

        setupListeners();
    }

    private void findViewsById(View v) {
        rootView = v.findViewById(R.id.fragment_account);
        profileLayout = v.findViewById(R.id.acc_layout_group);

        switchAccount = v.findViewById(R.id.switch_account_btn);
        imageView = v.findViewById(R.id.acc_image);
        nameTextView = v.findViewById(R.id.acc_name);
        emailTextView = v.findViewById(R.id.acc_email);
        phoneTextView = v.findViewById(R.id.acc_phone);
        genderTextView = v.findViewById(R.id.acc_gender);
        dobTextView = v.findViewById(R.id.acc_dob);

        resetPasswordBtn = v.findViewById(R.id.reset_pwd_btn);
        deleteAccountBtn = v.findViewById(R.id.delete_acc_btn);
        signOutBtn = v.findViewById(R.id.signout_btn);
    }

    private void populateFields() {
        if (user != null) {
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            phoneTextView.setText(user.getPhone() != null ? user.getPhone() : "- - -");

            if (ConnectionManager.isConnectionAvailable(activity))
                GlideApp.with(activity).load(user.getPhotoUri()).placeholder(getResources().getDrawable(R.drawable.default_user_img))
                        .circleCrop()
                        .into(imageView);
            else if (image.exists())
                GlideApp.with(activity).load(image).placeholder(getResources().getDrawable(R.drawable.default_user_img))
                        .circleCrop()
                        .into(imageView);
        }
    }

    private void setupListeners() {
        switchAccount.setOnClickListener(this);
        resetPasswordBtn.setOnClickListener(this);
        deleteAccountBtn.setOnClickListener(this);
        signOutBtn.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = ((AppCompatActivity) getActivity());

        isAdmin = getAuthorizationStatus();
        loggedInAs = getAdminState();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = new Reference.Builder()
                .setNode(User.class)
                .setNode(firebaseUser.getUid())
                .getDatabaseReference();

        databaseReference.addValueEventListener(this);

        if (activity != null) {
            if (loggedInAs == ADMIN_STATE.USER)
                navController = Navigation.findNavController(activity, R.id.main_fragments);
            else if (loggedInAs == ADMIN_STATE.ADMIN)
                navController = Navigation.findNavController(activity, R.id.admin_fragments);
            else
                navController = Navigation.findNavController(activity, R.id.main_fragments);
        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_account_btn: {
                sendSnackbar(rootView, "Switch Accounts to Admin");
                break;
            }
            case R.id.acc_image: {
                imageUpload.__construct(MIME_TYPE.IMAGE);
                imageUpload.user_image__(user);
                break;
            }
            case R.id.reset_pwd_btn: {
                break;
            }
            case R.id.delete_acc_btn: {
                break;
            }
            case R.id.signout_btn: {
                break;
            }
        }
    }

    private void switchAccounts() {
        //
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        downloadUserPhoto();
        populateFields();
    }

    private void downloadUserPhoto() {
        File imageFolder = new File(activity.getCacheDir(),
                new Reference.Builder()
                        .setNode(User.class)
                        .setNode(user.getUid())
                        .getNode());

        image = new File(imageFolder, user.getUid() + ".jpg");
        int imageSize = Integer.parseInt(String.valueOf(image.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));

        if (!imageFolder.exists() || imageSize < 1) {
            imageFolder.mkdirs();
            new DownloadTaskManager(image).execute(user.getPhotoUri());
        }
    }
}
