package org.nerdslot.Foundation;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nerdslot.Foundation.Helper.Pluralizer;
import org.nerdslot.Foundation.Helper.Slugify;
import org.nerdslot.Models.src.Model;

public class FireUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static ValueEventListener valueEventListener;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseUser user;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static FirebaseStorage firebaseStorage;
    private static FireUtil fireUtil;

    public FireUtil() {
    }

    static void init() {
        if (fireUtil == null) {
            fireUtil = new FireUtil();

            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            firebaseStorage = FirebaseStorage.getInstance();
        }
    }

    // Database Storage
    public static DatabaseReference databaseReference() {
        return databaseReference((String) null);
    }

    public static DatabaseReference databaseReference(@Nullable String node) {
        return node != null && !node.equals("") && !TextUtils.isEmpty(node)
                ? firebaseDatabase.getReference().child(node)
                : firebaseDatabase.getReference();
    }

    public static DatabaseReference databaseReference(Class<? extends Model> child) {
        String node = Pluralizer.build(
                new Slugify.Builder().input(child.getSimpleName().toLowerCase()).seperator("-").make()
        );

        return databaseReference(node);
    }

    // Firebase Storage
    public static StorageReference storageReference() {
        return storageReference((String) null);
    }

    public static StorageReference storageReference(@Nullable String node) {
        return node != null && !node.equals("") && !TextUtils.isEmpty(node)
                ? firebaseStorage.getReference().child(node)
                : firebaseStorage.getReference();
    }

    public static StorageReference storageReference(Class<? extends Model> child) {
        String node = Pluralizer.build(
                new Slugify.Builder().input(child.getSimpleName().toLowerCase()).seperator("-").make()
        );

        return storageReference(node);
    }

    public static void attachAuthStateListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachAuthStateListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
