package org.nerdslot.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.Exclude;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Nerdslot;

public interface RootInterface {
    String NETWORK_AVAILABLE_ACTION = "Network-Available-Action";
    String IS_NETWORK_AVAILABLE = "isNetworkAvailable";
    String SHARED_PREF_FILE = "sharedPrefs";
    String IS_ADMIN_SHARED_PREF = "is-admin";
    String USER_EMAIL_PERMISSION = "email";
    String USER_PROFILE_PERMISSION = "public_profile";
    String USER_BIRTHDAY_PERMISSION = "user_birthday";
    String USER_FRIENDS_PERMISSION = "user_friends";
    String AUTH_BUG_RESPONSE = "An authentication error occurred! The Admin has been notified!";
    String TAG = "log-tag";
    String TOS = "https://nerdslot.org"; // Terms of Service URL
    int JOB_ID = 100;
    int SELECT_FILE_REQUEST_CODE = 110;

    // Log Strings
    String OPERATION_CANCELLED = "Operation cancelled by User.";

    // References
    String ADMIN_NODE_REFERENCE = "administrators";
    String USERS_NODE_REFERENCE = "users";

    default void sendToast(Activity context, String msg) {
        Toast.makeText(context, msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message", Toast.LENGTH_LONG).show();
    }

    default void sendSnackbar(View rootView, String msg) {
        Snackbar sn = Snackbar.make(rootView, msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message", BaseTransientBottomBar.LENGTH_LONG);
        sn.setAction("Okay", v -> sn.dismiss());
        sn.show();
    }

    @Exclude
    default boolean getAuthorizationStatus() {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(IS_ADMIN_SHARED_PREF, false);
    }

    @Exclude
    default void setAuthorizationStatus(boolean status) {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ADMIN_SHARED_PREF, status);
        editor.apply();
    }

    @Exclude
    default void resetAuthorizationStatus() {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(IS_ADMIN_SHARED_PREF);
        editor.apply();
    }

    default void setEnabled(@NotNull View v, boolean status) {
        v.setEnabled(status);
    }

    default void setEnabled(@NotNull View[] views, boolean status) {
        for (View v : views) {
            v.setEnabled(status);
        }
    }

    default void setVisibility(@NotNull View v, int visibility) {
        v.setVisibility(visibility);
    }

    default void setVisibility(@NotNull View[] views, int visibility) {
        for (View v : views) {
            v.setVisibility(visibility);
        }
    }

    default void resetView(View[] views) {
        resetView(views, null);
    }

    default void resetView(@NotNull View[] views, @Nullable String value) {
        for (View v : views) {
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            } else if (v instanceof Button) {
                ((Button) v).setText("");
            }
        }
    }

    enum MIME_TYPE {
        JPG("image/jpg", 0),
        PNG("image/png", 1),
        IMAGE("image/*", 2),
        EPUB("application/epub+zip", 3),
        PDF("application/pdf", 4),
        TXT("text/plain", 5),
        ZIP("application/zip", 6),
        DOC("application/msword", 7);

        private String mime;
        private int intValue;

        MIME_TYPE(String mime, int intValue) {
            this.mime = mime;
            this.intValue = intValue;
        }

        @NonNull
        @Override
        public String toString() {
            return mime;
        }

        public int getOrdinal() {
            return intValue;
        }
    }
}
