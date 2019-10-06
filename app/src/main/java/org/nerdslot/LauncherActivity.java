package org.nerdslot;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.nerdslot.Admin.AdminActivity;
import org.nerdslot.Fragments.RootInterface;

public class LauncherActivity extends AppCompatActivity implements RootInterface {

    private static boolean isAdmin;
    private final Handler handler = new Handler();
    private final Runnable StartAuthActivity = this::intentService;
    private View container;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_launcher);

        isAdmin = getAuthorizationStatus();

        container = getWindow().getDecorView();
        setVisibilityOptions();

        auth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> updateUI(firebaseAuth.getCurrentUser());
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

    @SuppressLint("InlinedApi")
    private void setVisibilityOptions() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        container.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (isAdmin) {
                startActivity(new Intent(this, AdminActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            killActivity();
        } else {
            intentService();
        }
    }

    private void intentService() {
        startActivity(new Intent(this, AuthActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        killActivity();
    }

    private void killActivity() {
        finish();
    }
}
