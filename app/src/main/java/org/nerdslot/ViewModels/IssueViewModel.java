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
import org.nerdslot.Models.Issue.Issue;

import java.util.ArrayList;

public class IssueViewModel extends AndroidViewModel implements ChildEventListener, RootInterface {
    public Issue issue;
    private ArrayList<Issue> issues;
    private ArrayList<String> keyList;
    private Query query;
    private MutableLiveData<ArrayList<Issue>> mutableIssues = new MutableLiveData<>();

    public IssueViewModel(@NonNull Application application) {
        super(application);

        query = FireUtil.databaseReference(Issue.class);
        issues = new ArrayList<>();
        keyList = new ArrayList<>();
    }

    public LiveData<ArrayList<Issue>> getAllIssues() {
        if (mutableIssues.getValue() == null) query.addChildEventListener(this);

        return mutableIssues;
    }

    @Nullable
    private ArrayList<Issue> addIssues(@NonNull DataSnapshot dataSnapshot) {
        Issue addedIssue = dataSnapshot.getValue(Issue.class);
        issues.add(addedIssue);
        keyList.add(dataSnapshot.getKey());
        return issues;
    }

    private ArrayList<Issue> modifyIssue(@NonNull DataSnapshot dataSnapshot) {
        Issue modifiedIssue = dataSnapshot.getValue(Issue.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        issues.set(index, modifiedIssue);
        keyList.set(index, dataSnapshot.getKey());

        return issues;
    }

    private ArrayList<Issue> removeIssue(@NonNull DataSnapshot dataSnapshot) {
        Issue removedIssue = dataSnapshot.getValue(Issue.class);
        int index = keyList.indexOf(dataSnapshot.getKey());

        issues.remove(index);
        keyList.remove(index);

        return issues;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableIssues.postValue(addIssues(dataSnapshot));
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists())
            mutableIssues.postValue(modifyIssue(dataSnapshot));
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists())
            mutableIssues.postValue(removeIssue(dataSnapshot));
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.i(TAG, "onCancelled: " + OPERATION_CANCELLED);
    }
}
