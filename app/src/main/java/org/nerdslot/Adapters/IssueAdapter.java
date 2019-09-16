package org.nerdslot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.nerdslot.Adapters.ViewHolders.IssueViewHolder;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.R;

import java.util.ArrayList;

public class IssueAdapter extends RecyclerView.Adapter<IssueViewHolder> implements ChildEventListener {

    private ArrayList<Issue> issues;
    private AppCompatActivity activity;
    private DatabaseReference reference;

    public IssueAdapter(AppCompatActivity activity) {
        this.activity = activity;

        issues = new ArrayList<>();
        reference = FireUtil.firebaseDatabase.getReference().child(new Issue().getNode());

        reference.addChildEventListener(IssueAdapter.this);
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.recycler_home_content, parent, false);
        return new IssueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue currentIssue = issues.get(position);
        holder.bind(currentIssue);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Issue issue = dataSnapshot.getValue(Issue.class);
        issues.add(issue);
        notifyItemInserted(issues.size() - 1);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

}
