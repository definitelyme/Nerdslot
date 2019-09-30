package org.nerdslot.Foundation.Helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

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
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        child.setTranslationY(Math.max(0f,
                Math.min(Float.parseFloat(String.valueOf(child.getHeight())), child.getTranslationY() + dyConsumed)));

        if (dyConsumed < 0) {
            hideBottomNavigationView(child);
        } else if (dyConsumed > 0) {
            showBottomNavigationView(child);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull View child, @NonNull
                                          View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        child.setTranslationY(Math.min(0f, Math.min(Float.parseFloat(String.valueOf(child.getHeight())), child.getTranslationY() + dy)));

        if (dy < 0) {
            hideBottomNavigationView(child);
        } else if (dy > 0) {
            showBottomNavigationView(child);
        }
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout)
            updateSnackbar(child, dependency);
        return super.layoutDependsOn(parent, child, dependency);
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

    private void updateFAB(View child, @NonNull View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            float oldTranslation = child.getTranslationY();
            float height = Float.parseFloat(String.valueOf(dependency.getHeight()));
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);

//            return oldTranslation != newTranslation;
        }
//        return false;
    }

    private void hideBottomNavigationView(View view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(View view) {
        view.animate().translationY(0);
    }

}
