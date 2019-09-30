package org.nerdslot;

import android.os.Bundle;

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
    private NavController navController;
    private Badge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.main_fragments);

        setupBottomNavMenu(navController);
        configureAppBar();
    }

    private void setupBottomNavMenu(NavController navController) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

//        CoordinatorLayout.LayoutParams layoutParams = ((CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams());
//        layoutParams.setBehavior(new BottomNavigationHandler());
//        bottomNavigationView.enableAnimation(false);
//        bottomNavigationView.enableShiftingMode(0, false);
    }

    private void configureAppBar() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_account, R.id.navigation_menu).build();
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
}
