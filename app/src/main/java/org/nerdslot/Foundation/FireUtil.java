package org.nerdslot.Foundation;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public static DatabaseReference databaseReference(@NonNull String node){
        return firebaseDatabase.getReference().child(node);
    }

    public static DatabaseReference databaseReference(Model model){
        return databaseReference(model.getNode());
    }

    public static StorageReference storageReference(@NonNull String node){
        return firebaseStorage.getReference().child(node);
    }

    public static StorageReference storageReference(Model model){
        return storageReference(model.getNode());
    }

    public static void attachAuthStateListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachAuthStateListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
