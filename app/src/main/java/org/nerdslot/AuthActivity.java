package org.nerdslot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Admin.AdminActivity;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.User.Profile;
import org.nerdslot.Models.User.User;
import org.nerdslot.Network.ConnectionManager;

import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity implements RootInterface {
    private static final int RC_SIGN_IN = 1822; // Arbitrary Request code value
    private static boolean isAdmin = false;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private Button browseBtn, signInBtn;
    private ProgressBar progressBar;
    private View container;
    private View.OnClickListener onSignInClicked = v -> {
        configAuthButtons(false);

        if (ConnectionManager.isConnectionAvailable(this)) {
            showSignInMethods();
        } else {
            sendSnackbar(container, "No Internet Connection");
            configAuthButtons(true);
        }
    };
    private View.OnClickListener onBrowseClicked = v -> {
        configAuthButtons(false);
        anonymousSignIn();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        isAdmin = getAuthorizationStatus();

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        browseBtn = findViewById(R.id.anonymousSignInBtn);
        signInBtn = findViewById(R.id.authPickerBtn);
        progressBar = findViewById(R.id.auth_progress_bar);
        container = getWindow().getDecorView().getRootView();

        browseBtn.setOnClickListener(onBrowseClicked);
        signInBtn.setOnClickListener(onSignInClicked);
    }

    @Override
    protected void onStart() {
        super.onStart();

        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateUI(user);
            } else {
                configAuthButtons(true);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        auth.removeAuthStateListener(authStateListener);
    }

    public void updateUI(@NotNull FirebaseUser firebaseUser) {
        saveUserInformation(firebaseUser);
        intentService(firebaseUser);
    }

    private void anonymousSignIn() {
        auth.signInAnonymously();
    }

    private void intentService(@NotNull FirebaseUser firebaseUser) {
        if (isAdmin) {
            startActivity(new Intent(this, AdminActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        killActivity();
    }

    private void configAuthButtons(boolean active) {
        signInBtn.setEnabled(active);
        browseBtn.setEnabled(active);
        if (!active) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }

    private void killActivity() {
        finish();
    }

    private void showSignInMethods() {
        // You must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that you set the button ID for every provider
        // that you have enabled.
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout // Customize Sign In Layout
                .Builder(R.layout.fragment_login)
                .setEmailButtonId(R.id.email_signIn_btn)
                .setGoogleButtonId(R.id.google_signIn_btn)
                .setFacebookButtonId(R.id.facebook_signIn_btn)
                .build();

        AuthUI.IdpConfig emailIdp = new AuthUI.IdpConfig.EmailBuilder()
                .build();

        AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                .setScopes(Arrays.asList(Scopes.PROFILE, Scopes.EMAIL))
                .build();

        AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder() // Configure Facebook Login Permissions
                .setPermissions(Arrays.asList(USER_EMAIL_PERMISSION, USER_PROFILE_PERMISSION))
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                emailIdp,
                googleIdp,
                facebookIdp
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.logo)
                        .setTheme(R.style.AuthPickerTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true)
                        .setAuthMethodPickerLayout(customLayout)
                        .setAvailableProviders(providers)
                        .enableAnonymousUsersAutoUpgrade()
//                        .setTosAndPrivacyPolicyUrls(TOS, TOS)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void saveUserInformation(@NotNull FirebaseUser firebaseUser) {
        User user = new User();
        user.setUid(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setPhone(firebaseUser.getPhoneNumber());
        user.setPhotoUri(String.valueOf(firebaseUser.getPhotoUrl()));
        user.setCreated_at(firebaseUser.getMetadata().getCreationTimestamp());
        user.setUpdated_at(firebaseUser.getMetadata().getLastSignInTimestamp());

        for (UserInfo info : firebaseUser.getProviderData()) {
            Profile profile = new Profile();
            profile.setUid(info.getUid());
            profile.setDisplayName(info.getDisplayName());
            profile.setEmail(info.getEmail());
            profile.setPhone(info.getPhoneNumber());
            profile.setPhotoUri(String.valueOf(info.getPhotoUrl()));
            profile.setProvider(info.getProviderId());

            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (info.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID) && accessToken != null) {
                profile.setAccessToken(accessToken.getToken());
                profile.setTokenExpires(accessToken.isExpired());
            }

            profile.setEmailVerified(info.isEmailVerified());
            user.setProvider(profile);
        }

        databaseReference.child(new User().getNode()).child(user.getUid()).setValue(user);

        setAuthorizationStatus(isAdmin);

        checkAdmin(user);
    }

    private void checkAdmin(@NonNull User user) {
        if (user.getEmail() != null &&
                (user.getEmail().equalsIgnoreCase("ejike.br@gmail.com") ||
                        user.getEmail().equalsIgnoreCase("nerdslot.co@gmail.com"))) { // If true, add to "administrators" node
            databaseReference.child(ADMIN_NODE_REFERENCE).child(user.getUid()).setValue(user);
            isAdmin = true;
            setAuthorizationStatus(isAdmin);
        }
    }
}
