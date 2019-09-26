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
import com.google.firebase.database.Query;

import org.jetbrains.annotations.Nullable;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Category;

import java.util.ArrayList;

public class CategoryViewModel extends AndroidViewModel implements ChildEventListener, RootInterface {
    private ArrayList<Category> categories;
    public ArrayList<String> keyList;
    public ArrayList<String> categoryNames;
    public Category category;
    private Query query;
    private MutableLiveData<ArrayList<Category>> mutableCategories = new MutableLiveData<>();
    private MutableLiveData<Category> mutableCategory = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        query = FireUtil.databaseReference(Category.class);

        categories = new ArrayList<>();
        keyList = new ArrayList<>();
        categoryNames = new ArrayList<>();
    }

    public LiveData<ArrayList<Category>> all() {
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
    private ArrayList<Category> addCategories(@NonNull DataSnapshot dataSnapshot) {
        Category addedCategpry = dataSnapshot.getValue(Category.class);
        categories.add(addedCategpry);
        keyList.add(dataSnapshot.getKey());
        categoryNames.add(addedCategpry.getName());
        return categories;
    }

    private Category refreshSingle(@NonNull DataSnapshot dataSnapshot) {
        category = dataSnapshot.getValue(Category.class);
        return category;
    }

    private ArrayList<Category> modifyCategories(@NonNull DataSnapshot dataSnapshot){
        Category modifiedCategory = dataSnapshot.getValue(Category.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        categories.set(index, modifiedCategory);
        keyList.set(index, dataSnapshot.getKey());
//        categoryNames.set(categoryNames.indexOf(modifiedCategory.getName()), modifiedCategory.getName());

        return categories;
    }

    private ArrayList<Category> removeCategory(@NonNull DataSnapshot dataSnapshot) {
        Category removedCategory = dataSnapshot.getValue(Category.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        categories.remove(index);
        keyList.remove(index);
        categoryNames.remove(removedCategory.getName());

        return categories;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableCategories.postValue(addCategories(dataSnapshot));
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableCategories.postValue(modifyCategories(dataSnapshot));
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists())
            mutableCategories.postValue(removeCategory(dataSnapshot));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
