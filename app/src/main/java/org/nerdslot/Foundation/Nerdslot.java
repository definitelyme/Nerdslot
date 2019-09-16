package org.nerdslot.Foundation;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Contract;

public class Nerdslot extends Application {
    private static Nerdslot instance;

    public Nerdslot() {
        instance = this;
    }

    @Contract(pure = true)
    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*Enable disk Persistence*/
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FireUtil.init(); // Initialize user data
    }
}
