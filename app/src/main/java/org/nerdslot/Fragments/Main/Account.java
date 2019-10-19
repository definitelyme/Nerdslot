package org.nerdslot.Fragments.Main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Admin.AdminActivity;
import org.nerdslot.AuthActivity;
import org.nerdslot.Foundation.Helper.DownloadTaskManager;
import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Foundation.Helper.Upload;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.MainActivity;
import org.nerdslot.Models.User.User;
import org.nerdslot.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;


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
    private FirebaseUser firebaseUser;
    private File image;

    // Helpers
    private Upload imageUpload;
    private User user;

    // Views
    private MaterialButton switchAccount, emailTextView, phoneTextView, genderTextView, dobTextView, signOutBtn;
    private Group profileGroup, signInGroup;
    private MaterialTextView resetPasswordBtn, deleteAccountBtn, signInBtn, shareBtn, termsBtn, policyBtn;
    private TextView nameTextView;
    private ImageView imageView;
    private ImageButton editInfoBtn;
    private View rootView;

    public Account() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUpload = new Upload(this);
        setHasOptionsMenu(true);
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

        Toolbar toolbar = view.findViewById(R.id.account_toolbar);
        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);
        activity.getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, activity.getTheme()));

        findViewsById(view);

        if (firebaseUser.isAnonymous()) { // Check first if Account is Anonymous
            setVisibility(signInGroup, true);
            setVisibility(profileGroup, false);
        }

        if (isAdmin) { // Check if Auth User is Admin
            setVisibility(switchAccount, true);
        }

        setupListeners();
    }

    private void findViewsById(@NotNull View v) {
        rootView = v.findViewById(R.id.fragment_account);
        profileGroup = v.findViewById(R.id.acc_layout_group);
        signInGroup = v.findViewById(R.id.acc_sign_in_layout_group);

        switchAccount = v.findViewById(R.id.switch_account_btn);
        editInfoBtn = v.findViewById(R.id.edit_info);
        imageView = v.findViewById(R.id.acc_image);
        nameTextView = v.findViewById(R.id.acc_name);
        emailTextView = v.findViewById(R.id.acc_email);
        phoneTextView = v.findViewById(R.id.acc_phone);
        genderTextView = v.findViewById(R.id.acc_gender);
        dobTextView = v.findViewById(R.id.acc_dob);

        resetPasswordBtn = v.findViewById(R.id.reset_pwd_btn);
        deleteAccountBtn = v.findViewById(R.id.delete_acc_btn);
        signInBtn = v.findViewById(R.id.signin_btn);
        signOutBtn = v.findViewById(R.id.signout_btn);

        shareBtn = v.findViewById(R.id.share_btn);
        termsBtn = v.findViewById(R.id.terms_btn);
        policyBtn = v.findViewById(R.id.privacy_policy_btn);
    }

    private void populateFields() {
        if (user != null) {
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
            phoneTextView.setText(user.getPhone() != null ? user.getPhone() : "- - -");
            genderTextView.setText(user.getGender() != null ? user.getGender() : getString(R.string.na));
            dobTextView.setText(user.getDob() != null ? user.getGender() : getString(R.string.na));

            StorageReference imgRef = null;
            if (user.getPhotoUri().contains("gs://nerdslot-x.appspot.com"))
                imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(user.getPhotoUri());

            GlideApp.with(activity).load(imgRef != null ? imgRef : user.getPhotoUri()).placeholder(getResources().getDrawable(R.drawable.default_user_img))
                    .fitCenter()
                    .centerCrop()
                    .circleCrop()
                    .into(imageView);
        }
    }

    private void setupListeners() {
        switchAccount.setOnClickListener(this);
        resetPasswordBtn.setOnClickListener(this);
        deleteAccountBtn.setOnClickListener(this);
        editInfoBtn.setOnClickListener(this);
        imageView.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        policyBtn.setOnClickListener(this);
        termsBtn.setOnClickListener(this);
        signOutBtn.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        isAdmin = getAuthorizationStatus();
        loggedInAs = getAdminState();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = new Reference.Builder()
                .setNode(User.class)
                .setNode(firebaseUser.getUid())
                .getDatabaseReference();

        databaseReference.addValueEventListener(this);

        if (activity != null) {
            if (loggedInAs == ADMIN_STATE.MAIN_ACTIVITY)
                navController = Navigation.findNavController(activity, R.id.main_fragments);
            else if (loggedInAs == ADMIN_STATE.ADMIN_ACTIVITY)
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
                switchAccounts();
                break;
            }
            case R.id.acc_image: {
                imageUpload.__construct(MIME_TYPE.IMAGE);
                break;
            }
            case R.id.reset_pwd_btn: {
                resetPassword();
                break;
            }

            case R.id.edit_info:{
                updateAccountInformation();
                break;
            }

            case R.id.delete_acc_btn: {
                deleteAccount();
                break;
            }

            case R.id.signin_btn: {
                signIn();
                break;
            }

            case R.id.signout_btn: {
                signOut();
                break;
            }

            case R.id.share_btn: {
                break;
            }

            case R.id.privacy_policy_btn: {
                break;
            }

            case R.id.terms_btn: {
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            GlideApp.with(activity).load(uri).placeholder(getResources().getDrawable(R.drawable.default_user_img))
                    .fitCenter()
                    .centerCrop()
                    .circleCrop()
                    .into(imageView);
            imageUpload.user_image__(user, uri);
        }
    }

    private void switchAccounts() {
        mListener.showOverlay("Switching Accounts");
        if (loggedInAs == ADMIN_STATE.ADMIN_ACTIVITY) {
            setAdminState(ADMIN_STATE.MAIN_ACTIVITY);
            startActivity(new Intent(activity, MainActivity.class));
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            killActivity();
        }

        if (loggedInAs == ADMIN_STATE.MAIN_ACTIVITY) {
            setAdminState(ADMIN_STATE.ADMIN_ACTIVITY);
            startActivity(new Intent(activity, AdminActivity.class));
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            killActivity();
        }
    }

    private void signIn() {
        mListener.showOverlay("");
        if (firebaseUser.isAnonymous()) {
            // Delete Anonymous User
            new Reference.Builder().setNode(User.class).setNode(user.getUid()).getDatabaseReference().removeValue();
            signOut();
            firebaseUser.delete();
        }
    }

    private void updateAccountInformation(){
        sendToast(activity, NOT_AVAILABLE_IN_VERSION);
    }

    private void signOut() {
        mListener.showOverlay(firebaseUser.isAnonymous() ? "" : getString(R.string.signing_out_string));
        AuthUI.getInstance().signOut(activity)
                .addOnSuccessListener(aVoid -> {
                    resetAuthorizationStatus();
                    loginIntent();
                })
                .addOnFailureListener(e -> sendToast(activity, "Sign out Failed!"));
    }

    private void resetPassword() {
        sendSnackbar(rootView, NOT_AVAILABLE_IN_VERSION);
    }

    private void deleteAccount() {
        sendSnackbar(rootView, NOT_AVAILABLE_IN_VERSION);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        user = dataSnapshot.getValue(User.class);
        downloadUserPhoto();
        populateFields();
    }

    private void downloadUserPhoto() {
        if (user != null && user.getEmail() != null) {
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

    private void loginIntent() {
        startActivity(new Intent(activity, AuthActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        killActivity();
    }

    private void killActivity() {
        activity.finish();
    }
}
