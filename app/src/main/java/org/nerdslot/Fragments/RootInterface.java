package org.nerdslot.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageException;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.BuildConfig;
import org.nerdslot.Foundation.Nerdslot;

public interface RootInterface extends OnFailureListener, ValueEventListener {
    String NETWORK_AVAILABLE_ACTION = "Network-Available-Action";
    String IS_NETWORK_AVAILABLE = "isNetworkAvailable";
    String SHARED_PREF_FILE = "sharedPrefs";
    String IS_ADMIN_SHARED_PREF = "is-admin";
    String ADMIN_STATE_SHARED_PREF = "admin-state";
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
    String NOT_AVAILABLE_IN_VERSION = String.format("Not available in version %s!", BuildConfig.VERSION_NAME);

    // References
    String ADMIN_NODE_REFERENCE = "administrators";
    String USERS_NODE_REFERENCE = "users";

    String ISSUE_IMAGE_NODE = "issueImageUri";
    String MAGAZINE_IMAGES_NODE = "images";
    String USER_PROFILE_PHOTO_NODE = "photoUri";
    String MAGAZINE_COVER_NODE = "cover";
//    String FEATURED_ISSUE_NODE = "featured";

    String STRING_URI_SEPERATOR = ",";
    String STRING_URI_INCREMENTER = "-no-";
    String STRING_APP_STORAGE_ROOT = "gs://nerdslot-x.appspot.com";

    // Intents
    String ISSUE_INTENT_KEY = "issue";
    String MAGAZINE_INTENT_KEY = "magazine";

    default void sendToast(Activity context, String msg) {
        Toast.makeText(context, msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message", Toast.LENGTH_LONG).show();
    }

    default void sendSnackbar(View rootView, String msg) {
        sendSnackbar(rootView, msg, null);
    }

    default void sendSnackbar(View rootView, String msg, String actionMsg) {
        Snackbar sn = Snackbar.make(rootView,
                msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message",
                BaseTransientBottomBar.LENGTH_LONG);
        sn.setAction(actionMsg != null && !TextUtils.isEmpty(actionMsg) ? actionMsg : "Okay", v -> sn.dismiss());
        sn.setActionTextColor(Color.WHITE);
        sn.show();
    }

    default void sendResponse(String msg) {
        sendResponse(null, msg, null);
    }

    default void sendResponse(@Nullable Activity context, String msg) {
        sendResponse(context, msg, null);
    }

    default void sendResponse(String msg, Exception ex) {
        sendResponse(null, msg, ex);
    }

    default void sendResponse(@Nullable Activity context, String msg, @Nullable Exception ex) {
        Log.i(TAG, "sendResponse: " + msg, ex);
        if (context != null)
            Toast.makeText(context, msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message", Toast.LENGTH_LONG).show();
    }

    default void sendFullResponse(@NonNull Context ctx, @NonNull View rootView, String msg) {
        sendFullResponse(ctx, rootView, msg, null);
    }

    default void sendFullResponse(@NonNull Context ctx, @NonNull View rootView, String msg, @Nullable Exception ex) {
        if (ctx != null)
            Toast.makeText(ctx, msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message", Toast.LENGTH_LONG).show();
        if (rootView != null) {
            Snackbar sn = Snackbar.make(rootView,
                    msg != null && !TextUtils.isEmpty(msg) ? msg : "No Message",
                    BaseTransientBottomBar.LENGTH_LONG);
            sn.setAction(msg != null && !TextUtils.isEmpty(msg) ? msg : "Okay", v -> sn.dismiss());
            sn.setActionTextColor(Color.WHITE);
            sn.show();
        }
        if (ex != null)
            Log.i(TAG, "sendResponse: " + msg, ex);
    }

    default AppCompatActivity getActivityFromContext(Context context) {
        if (context == null)
            return null;
        else if (context instanceof Activity)
            return (AppCompatActivity) context;
        else if (context instanceof ContextWrapper)
            return getActivityFromContext(((ContextWrapper) context).getBaseContext());

        return null;
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
    default ADMIN_STATE getAdminState() {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        String state = sharedPreferences.getString(ADMIN_STATE_SHARED_PREF, ADMIN_STATE.MAIN_ACTIVITY.toString());
        if (state.equals(ADMIN_STATE.MAIN_ACTIVITY.toString())) return ADMIN_STATE.MAIN_ACTIVITY;
        else return ADMIN_STATE.ADMIN_ACTIVITY;
    }

    @Exclude
    default void setAdminState(@NotNull ADMIN_STATE state) {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ADMIN_STATE_SHARED_PREF, state.toString());
        editor.apply();
    }

    @Exclude
    default void resetAuthorizationStatus() {
        SharedPreferences sharedPreferences = Nerdslot.getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(IS_ADMIN_SHARED_PREF);
        editor.remove(ADMIN_STATE_SHARED_PREF);
        editor.apply();
    }

    default void setEnabled(@NotNull View v, boolean enabled) {
        v.setEnabled(enabled);
    }

    default void setEnabled(boolean enabled, @NotNull View... views) {
        for (View v : views) {
            v.setEnabled(enabled);
        }
    }

    default void setVisibility(@NotNull View v, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE; // If "visible = true", visibility = VISIBLE, else = GONE
        v.setVisibility(visibility);
    }

    default void setVisibility(boolean visible, @NotNull View... views) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        for (View v : views) {
            v.setVisibility(visibility);
        }
    }

    default void setError(@NonNull View v) {
        setError(v, null);
    }

    default void setError(@NonNull View v, @Nullable String errorText) {
        if (v instanceof TextInputLayout) {
            ((TextInputLayout) v).setError(
                    errorText != null && !TextUtils.isEmpty(errorText) && !errorText.equalsIgnoreCase("")
                            ? errorText
                            : "Field is required!");
        }
    }

    default void validateTextInput(TextInputLayout layout, String string) {
        if (!TextUtils.isEmpty(string)) {
            resetError(layout);
        } else {
            setError(layout, "Field is required!");
        }
    }

    default void resetError(@NonNull View v) {
        if (v instanceof TextInputLayout) ((TextInputLayout) v).setError(null);
    }

    default void resetError(@NonNull View... view) {
        for (View v : view) {
            if (v instanceof TextInputLayout) ((TextInputLayout) v).setError(null);
        }
    }

    default void resetView(@NonNull View v) {
        resetView(v, null);
    }

    default void resetView(@NonNull View v, @Nullable String value) {
        if (v instanceof Button) {
            ((Button) v).setText(value);
        }
        if (v instanceof EditText) {
            ((EditText) v).setText(value != null && !value.equals("") ? value : "");
            ((EditText) v).setError(null);
        }
    }

    default void resetViews(@NonNull View... views) {
        resetViews(null, views);
    }

    default void resetViews(@Nullable String value, @NotNull View... views) {
        for (View v : views) {
            if (v instanceof Button) {
                return;
            }

            if (v instanceof EditText) {
                ((EditText) v).setText(value != null && !value.equals("") ? value : "");
                ((EditText) v).setError(null);
            }
        }
    }

    @Override
    default void onFailure(@NonNull Exception e) {
        int errorCode = ((StorageException) e).getErrorCode();
        String errorMsg = e.getLocalizedMessage();

        switch (errorCode) {
            case StorageException.ERROR_NOT_AUTHENTICATED:
                sendResponse((Activity) Nerdslot.getContext(), errorMsg);
            case StorageException.ERROR_BUCKET_NOT_FOUND:
                sendResponse((Activity) Nerdslot.getContext(), errorMsg);
            case StorageException.ERROR_NOT_AUTHORIZED:
                sendResponse((Activity) Nerdslot.getContext(), "Sorry, you're not authorized to view this.");
            case StorageException.ERROR_OBJECT_NOT_FOUND:
                sendResponse((Activity) Nerdslot.getContext(), "File or Object not found!");
            case StorageException.ERROR_RETRY_LIMIT_EXCEEDED:
                sendResponse((Activity) Nerdslot.getContext(), "Retry Limit Exceeded!");
            case StorageException.ERROR_CANCELED:
                sendResponse((Activity) Nerdslot.getContext(), "Request Cancelled!");
            case StorageException.ERROR_QUOTA_EXCEEDED:
                sendResponse(errorMsg);
            case StorageException.ERROR_UNKNOWN:
                sendResponse((Activity) Nerdslot.getContext(), "Oops! Error unknown.");
        }
    }

    @Override
    default void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        //
    }

    @Override
    default void onCancelled(@NonNull DatabaseError databaseError) {
        sendResponse(databaseError.getMessage(), databaseError.toException());
    }

    default void showOverlay(String msg) {
        showOverlay(msg, 0);
    }

    default void showOverlay(String msg, int progress) {
        //
    }

    default void hideOverlay() {
        //
    }

    default void switchAccounts(RootInterface listener) {
        switchAccounts(listener, null);
    }

    default void switchAccounts(RootInterface listener, String msg) {
        if (getAdminState() == ADMIN_STATE.ADMIN_ACTIVITY)
            setAdminState(ADMIN_STATE.MAIN_ACTIVITY);
        if (getAdminState() == ADMIN_STATE.MAIN_ACTIVITY)
            setAdminState(ADMIN_STATE.ADMIN_ACTIVITY);
        listener.showOverlay(msg);
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
        private int ordinal;

        MIME_TYPE(String mime, int ordinal) {
            this.mime = mime;
            this.ordinal = ordinal;
        }

        @NonNull
        @Override
        public String toString() {
            return mime;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

    enum ADMIN_STATE {
        ADMIN_ACTIVITY("admin", 0),
        MAIN_ACTIVITY("user", 1);

        private String authState;
        private int ordinal;

        ADMIN_STATE(String as, int ordinal) {
            this.authState = as;
            this.ordinal = ordinal;
        }

        @NonNull
        @Override
        public String toString() {
            return authState;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }

    enum GENDER {
        MALE("male", 0),
        FEMALE("female", 1);

        private String type;
        private int ordinal;

        GENDER(String type, int ordinal) {
            this.type = type;
            this.ordinal = ordinal;
        }

        @NonNull
        @Override
        public String toString() {
            return type;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }
}
