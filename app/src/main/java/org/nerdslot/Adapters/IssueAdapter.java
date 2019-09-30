package org.nerdslot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.nerdslot.Adapters.ViewHolders.IssueViewHolder;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.R;

import java.util.ArrayList;

public class IssueAdapter extends RecyclerView.Adapter<IssueViewHolder> {

    private ArrayList<Issue> issues;

    public IssueAdapter() {
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

    public void setIssues(ArrayList<Issue> issues) {
        this.issues = issues;
        notifyDataSetChanged();
    }
}
