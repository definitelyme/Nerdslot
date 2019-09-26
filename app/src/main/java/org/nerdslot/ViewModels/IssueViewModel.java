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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Issue;

import java.util.ArrayList;

public class IssueViewModel extends AndroidViewModel implements ValueEventListener, RootInterface {
    public ArrayList<Issue> issues;
    public Issue issue;
    private Query query;
    private MutableLiveData<ArrayList<Issue>> mutableIssues = new MutableLiveData<>();
    private MutableLiveData<Issue> mutableIssue = new MutableLiveData<>();

    public IssueViewModel(@NonNull Application application) {
        super(application);
        query = FireUtil.databaseReference(Issue.class);
    }

    public LiveData<ArrayList<Issue>> getLiveIssues() {
        if (mutableIssues.getValue() == null) {
            query.addListenerForSingleValueEvent(this);
        }

        return mutableIssues;
    }

    public LiveData<Issue> find(String issue_id) {
        query.orderByKey()
                .equalTo(issue_id)
                .addListenerForSingleValueEvent(this);
        return mutableIssue;
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

    private Issue refreshSingle(@NonNull DataSnapshot dataSnapshot) {
        issue = dataSnapshot.getValue(Issue.class);
        return issue;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (dataSnapshot.getChildrenCount() > 1)
                mutableIssues.postValue(refreshIssues(dataSnapshot));
            else
                mutableIssue.setValue(refreshSingle(dataSnapshot));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
