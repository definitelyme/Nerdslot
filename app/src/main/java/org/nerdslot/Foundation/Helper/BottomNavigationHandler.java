package org.nerdslot.Foundation.Helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;

import org.jetbrains.annotations.NotNull;

public class BottomNavigationHandler extends CoordinatorLayout.Behavior<View> {

    public BottomNavigationHandler() {
    }

    public BottomNavigationHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
//        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        return true;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull View child, @NonNull
                                          View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        child.setTranslationY(Math.min(0f, Math.min(Float.parseFloat(String.valueOf(child.getHeight())), child.getTranslationY() + dy)));

//        if (dy < 0) {
//            showBottomNavigationView(child);
//        } else if (dy > 0) {
//            hideBottomNavigationView(child);
//        }
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency instanceof SpeedDialOverlayLayout)
//            updateSnackbar(child, (Snackbar.SnackbarLayout) dependency);
            updateFAB(child, dependency);
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return updateFAB(child, dependency);
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        child.setTranslationY(0f);
    }

    private void updateSnackbar(View child, @NotNull View dependency) {
        if (dependency.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();

            params.setAnchorId(child.getId());
            params.anchorGravity = Gravity.TOP;
            params.gravity = Gravity.TOP;
            dependency.setLayoutParams(params);
        }
    }

    private void updateSnackbar(View child, Snackbar.SnackbarLayout snackbarLayout) {
        if (snackbarLayout.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams layoutParams = ((CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams());

            layoutParams.setAnchorId(child.getId());
            layoutParams.anchorGravity = Gravity.TOP;
            layoutParams.gravity = Gravity.TOP;

            snackbarLayout.setLayoutParams(layoutParams);
        }
    }

    private boolean updateFAB(View child, @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float oldTranslation = child.getTranslationY();
            float height = Float.parseFloat(String.valueOf(dependency.getHeight()));
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);

            return oldTranslation != newTranslation;
        }
        return false;
    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }
}
