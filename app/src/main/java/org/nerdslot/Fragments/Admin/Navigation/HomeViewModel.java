package org.nerdslot.Fragments.Admin.Navigation;

import android.app.Application;
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
import org.nerdslot.Models.Issue.Issue;

import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel implements RootInterface {
    ArrayList<Issue> issues = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private MutableLiveData<ArrayList<Issue>> listMutableLiveData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    LiveData<ArrayList<Issue>> getLiveIssues() {
        if (listMutableLiveData.getValue() == null) {
            loadData();
        }

        return listMutableLiveData;
    }

    @Nullable
    private ArrayList<Issue> refreshIssues(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            try {
                Issue issue = snapshot.getValue(Issue.class);
                issues.add(issue);
            } catch (Exception ex) {
                Log.i(TAG, "refreshIssues: DataSnapshot Error - " + ex.getMessage(), ex);
            }
        }

        return null;
    }

    private void loadData() {
        Query query = databaseReference.child(new Issue().getNode());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    listMutableLiveData.postValue(refreshIssues(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
