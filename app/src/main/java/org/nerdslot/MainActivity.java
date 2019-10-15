package org.nerdslot;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.nerdslot.Fragments.Main.MainInterface;
import org.nerdslot.Fragments.RootInterface;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements MainInterface, RootInterface {

    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private ProgressBar overlayProgressBar;
    private View overlayView;
    private TextView overlayTextView;
    private Badge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.main_fragments);

        overlayView = findViewById(R.id.overlay_view);
        overlayTextView = findViewById(R.id.overlay_textView);
        overlayProgressBar = findViewById(R.id.overlay_progress_bar);

        setupBottomNavMenu(navController);
        configureAppBar();
    }

    private void setupBottomNavMenu(NavController navController) {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

//        bottomNavigationView.enableAnimation(false);
//        bottomNavigationView.enableShiftingMode(0, false);
    }

    private void configureAppBar() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_account, R.id.navigation_menu).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                navController.navigate(R.id.navigation_menu);
                break;
            }

            case R.id.action_help: {
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public Badge makeBadge(int position, int number) {
        badge = new QBadgeView(this)
//                .bindTarget(bottomNavigationView.getBottomNavigationItemView(position))
                .setGravityOffset(12, 2, true)
                .setBadgeNumber(number);

        return badge;
    }

    @Override
    public void updateBadge(int position, int number) {
        Badge thisBadge = findBadge(position);

        thisBadge.setBadgeNumber(number)
                .setGravityOffset(12, 2, true);
    }

    @Override
    public void removeBadge(int position) {
        Badge badge = findBadge(position);
        badge.setBadgeNumber(0);
        badge.hide(true);
    }

    private Badge findBadge(int position) {
//        badge.bindTarget(bottomNavigationView.getBottomNavigationItemView(position));
//        return badge;
        return null;
    }

    @Override
    public void showOverlay(String msg) {
        setVisibility(overlayView, true);
        overlayView.bringToFront();
        overlayProgressBar.bringToFront();
        overlayTextView.setText(msg);
        setVisibility(bottomNavigationView, false);
    }

    @Override
    public void showOverlay(String msg, double progress) {
        showOverlay(msg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            overlayProgressBar.setProgress((int) progress, true);
        overlayProgressBar.setProgress((int) progress);
    }

    @Override
    public void hideOverlay() {
        setVisibility(bottomNavigationView, true);
        setVisibility(overlayView, false);
    }
}
