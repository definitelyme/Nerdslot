package org.nerdslot.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.nerdslot.Foundation.Helper.BottomNavigationHandler;
import org.nerdslot.Fragments.Admin.AdminInterface;
import org.nerdslot.R;

public class AdminActivity extends AppCompatActivity implements AdminInterface {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private View container;
    private SpeedDialView fab;
    private SpeedDialView.OnActionSelectedListener actionSelectedListener = actionItem -> {
        switch (actionItem.getId()) {
            case R.id.id_create_issue: {
                navController.navigate(R.id.createIssue);
                fab.close();
                return true;
            }

            case R.id.id_create_category: {
                navController.navigate(R.id.createCategory);
                fab.close();
                return true;
            }

            case R.id.id_create_user: {
                navController.navigate(R.id.createUser);
                fab.close();
                return true;
            }

            case R.id.id_create_admin: {
                navController.navigate(R.id.createAdmin);
                fab.close();
                return true;
            }
        }

        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        container = findViewById(R.id.activity_admin);
        fab = findViewById(R.id.speed_dial);
        navController = Navigation.findNavController(this, R.id.admin_fragments);

        setupSpeedDial();
        setupBottomNavMenu(navController);
        configureAppBar();
    }

    private void setupBottomNavMenu(NavController navController) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        CoordinatorLayout.LayoutParams layoutParams = ((CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams());
        layoutParams.setBehavior(new BottomNavigationHandler());
    }

    private void configureAppBar() {
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_account, R.id.navigation_menu).build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setupSpeedDial() {

        fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.id_create_issue, R.drawable.ic_shelf)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()))
                        .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.darkGrey, getTheme()))
                        .setLabel(getString(R.string.create_issue_string))
                        .setLabelColor(Color.WHITE)
                        .setLabelClickable(true)
                        .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.midGrey, getTheme()))
                        .create()
        );

        fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.id_create_category, R.drawable.ic_category)
                        .setFabBackgroundColor(Color.WHITE)
                        .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.add_category_label_bg, getTheme()))
                        .setLabel(getString(R.string.create_category_string))
                        .setLabelClickable(true)
                        .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.add_category_label_bg, getTheme()))
                        .setLabelColor(Color.WHITE)
                        .create()
        );

        fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.id_create_admin, R.drawable.ic_add_admin)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.add_admin_color, getTheme()))
                        .setFabImageTintColor(Color.WHITE)
                        .setLabel(getString(R.string.create_admin_string))
                        .setLabelColor(Color.WHITE)
                        .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.midGrey, getTheme()))
                        .setLabelClickable(true)
                        .create()
        );

        fab.addActionItem(
                new SpeedDialActionItem.Builder(R.id.id_create_user, R.drawable.ic_add_user)
                        .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.add_user_bg, getTheme()))
                        .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.white, getTheme()))
                        .setLabel(getString(R.string.create_user_string))
                        .setLabelColor(getResources().getColor(R.color.add_user_bg))
                        .setLabelClickable(true)
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );

        fab.setOnActionSelectedListener(actionSelectedListener);
    }
}
