package org.nerdslot.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Magazine;

public class MagazineViewModel extends AndroidViewModel implements ChildEventListener, RootInterface {
    private Query query;
    private MutableLiveData<Magazine> mutableMagazine = new MutableLiveData<>();

    public MagazineViewModel(@NonNull Application application) {
        super(application);
        query = new Reference.Builder()
                .setNode(Magazine.class).getDatabaseReference();
    }

    public LiveData<Magazine> find(String magazine_id) {
        if (mutableMagazine.getValue() == null)
            query.orderByKey()
                    .equalTo(magazine_id)
                    .addChildEventListener(this);

        return mutableMagazine;
    }

    private Magazine refreshMagazine(@NonNull DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Magazine.class);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableMagazine.postValue(refreshMagazine(dataSnapshot));
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableMagazine.postValue(refreshMagazine(dataSnapshot));
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists())
            mutableMagazine.postValue(refreshMagazine(dataSnapshot));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableMagazine.postValue(refreshMagazine(dataSnapshot));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
