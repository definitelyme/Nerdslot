package org.nerdslot.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.Nullable;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Category;

public class CategoryViewModel extends AndroidViewModel implements ChildEventListener, RootInterface {
    public IndexList<Category> categories;
    public Category category;
    private Query query;
    private MutableLiveData<IndexList<Category>> mutableCategories = new MutableLiveData<>();
    private MutableLiveData<Category> mutableCategory = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        categories = new IndexList<>(Category::getName);
        query = databaseReference.child(new Category().getNode());
    }

    public LiveData<IndexList<Category>> all() {
        if (mutableCategories.getValue() == null) {
            query.addChildEventListener(this);
        }

        return mutableCategories;
    }

    public LiveData<Category> find(String category_id) {
        query.orderByKey()
                .equalTo(category_id)
                .addChildEventListener(this);
        return mutableCategory;
    }

    @Nullable
    private IndexList<Category> refreshCategories(@NonNull DataSnapshot dataSnapshot) {
        categories.add(dataSnapshot.getValue(Category.class));
        return categories;
    }

    private Category refreshSingle(@NonNull DataSnapshot dataSnapshot) {
        category = dataSnapshot.getValue(Category.class);
        return category;
    }

    private void updateData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (dataSnapshot.getChildrenCount() > 1)
                mutableCategories.postValue(refreshCategories(dataSnapshot));
            else
                mutableCategory.setValue(refreshSingle(dataSnapshot));
        }
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        updateData(dataSnapshot);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        updateData(dataSnapshot);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        int index = categories.getIndexByKey(dataSnapshot.getValue(Category.class).getName());
        categories.remove(index);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        updateData(dataSnapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
