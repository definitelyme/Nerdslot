package org.nerdslot.Fragments.Admin.Navigation;

import android.app.Application;
import android.os.Build;

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
import org.nerdslot.Models.Issue.Issue;

import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel implements RootInterface {
    public ArrayList<Issue> issues;
    public ArrayList<Category> categories;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private MutableLiveData<ArrayList<Issue>> listMutableIssues = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Category>> listMutableCategories = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<Issue>> getLiveIssues() {
        if (listMutableIssues.getValue() == null) {
            loadIssues();
        }

        return listMutableIssues;
    }

    public LiveData<ArrayList<Category>> getLiveCategories() {
        if (listMutableCategories.getValue() == null) {
            loadCategories();
        }

        return listMutableCategories;
    }

    private void loadIssues() {
        Query query = databaseReference.child(new Issue().getNode());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    listMutableIssues.postValue(refreshIssues(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCategories() {
        Query query = databaseReference.child(new Category().getNode());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    listMutableCategories.postValue(refreshCategories(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    private ArrayList<Issue> refreshIssues(@NonNull DataSnapshot dataSnapshot) {

        issues = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dataSnapshot.getChildren().iterator().forEachRemaining(snapshot -> {
                Issue issue = snapshot.getValue(Issue.class);
                issues.add(issue);
            });
        } else {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Issue issue = snapshot.getValue(Issue.class);
                issues.add(issue);
            }
        }

        return issues;
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
}
