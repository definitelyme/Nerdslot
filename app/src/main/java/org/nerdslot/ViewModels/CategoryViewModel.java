package org.nerdslot.ViewModels;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Category;

import java.util.ArrayList;

public class CategoryViewModel extends AndroidViewModel implements ValueEventListener, RootInterface {
    public ArrayList<Category> categories;
    public Category category;
    private Query query;
    private MutableLiveData<ArrayList<Category>> mutableCategories = new MutableLiveData<>();
    private MutableLiveData<Category> mutableCategory = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        query = databaseReference.child(new Category().getNode());
    }

    public LiveData<ArrayList<Category>> all() {
        if (mutableCategories.getValue() == null) {
            query.addListenerForSingleValueEvent(this);
        }

        return mutableCategories;
    }

    public LiveData<Category> find(String category_id) {
        query.orderByKey()
                .equalTo(category_id)
                .addListenerForSingleValueEvent(this);
        return mutableCategory;
    }

    @Nullable
    private ArrayList<Category> refreshCategories(@NonNull DataSnapshot dataSnapshot) {

        categories = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dataSnapshot.getChildren().iterator().forEachRemaining(snapshot -> {
                Category category = snapshot.getValue(Category.class);
                categories.add(category);
            });
        } else {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Category category = snapshot.getValue(Category.class);
                categories.add(category);
            }
        }

        return categories;
    }

    private Category refreshSingle(@NonNull DataSnapshot dataSnapshot) {
        category = dataSnapshot.getValue(Category.class);
        return category;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (dataSnapshot.getChildrenCount() > 1)
                mutableCategories.postValue(refreshCategories(dataSnapshot));
            else
                mutableCategory.setValue(refreshSingle(dataSnapshot));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
