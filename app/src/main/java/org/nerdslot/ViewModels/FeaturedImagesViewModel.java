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

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Featured;

import java.util.ArrayList;

public class FeaturedImagesViewModel extends AndroidViewModel implements ChildEventListener, RootInterface {
    private Query query;
    private ArrayList<Featured> featuredImages;
    private ArrayList<String> keyList;
    private MutableLiveData<ArrayList<Featured>> mutableLiveData = new MutableLiveData<>();

    public FeaturedImagesViewModel(@NonNull Application application) {
        super(application);
        query = new Reference.Builder()
                .setNode(Featured.class).getDatabaseReference();
        featuredImages = new ArrayList<>();
        keyList = new ArrayList<>();
    }

    public LiveData<ArrayList<Featured>> getFeaturedImages() {
        if (mutableLiveData.getValue() == null) query.addChildEventListener(this);
        return mutableLiveData;
    }

    private ArrayList<Featured> addFeaturedImages(@NotNull DataSnapshot dataSnapshot) {
        Featured featuredImage = dataSnapshot.getValue(Featured.class);
        featuredImages.add(featuredImage);
        keyList.add(dataSnapshot.getKey());
        return featuredImages;
    }

    private ArrayList<Featured> modifyFeaturedImages(@NotNull DataSnapshot dataSnapshot) {
        Featured featuredImage = dataSnapshot.getValue(Featured.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        featuredImages.set(index, featuredImage);
        keyList.set(index, dataSnapshot.getKey());

        return featuredImages;
    }

    private ArrayList<Featured> removeFeaturedImages(@NotNull DataSnapshot dataSnapshot) {
        Featured featuredImage = dataSnapshot.getValue(Featured.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        featuredImages.remove(index);
        keyList.remove(index);
        return featuredImages;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableLiveData.postValue(addFeaturedImages(dataSnapshot));
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableLiveData.postValue(modifyFeaturedImages(dataSnapshot));
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists())
            mutableLiveData.postValue(removeFeaturedImages(dataSnapshot));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
