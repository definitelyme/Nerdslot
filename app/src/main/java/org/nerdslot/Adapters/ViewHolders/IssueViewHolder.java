package org.nerdslot.Adapters.ViewHolders;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nerdslot.DetailActivity;
import org.nerdslot.Foundation.Helper.DownloadTaskManager;
import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.Main.MainInterface;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;
import org.nerdslot.R;

import java.io.File;

public class IssueViewHolder extends RecyclerView.ViewHolder implements MainInterface, View.OnClickListener {
    private AppCompatActivity activity;
    private ImageView issueImage;
    private TextView issueTitle, issueDescription;
    private MaterialButton subscribeBtn, purchaseBtn;
    private Issue currentIssue;
    private View rootView, divider, container;

    public IssueViewHolder(@NonNull View itemView) {
        super(itemView);

        activity = getActivityFromContext(itemView.getContext());
        rootView = activity.findViewById(R.id.main_activity);

        MaterialCardView cardView = itemView.findViewById(R.id.cardView);
        cardView.setBackgroundResource(R.drawable.ripple_rectangle);

        findViewsById();

        setupListeners();

        if (getAuthorizationStatus()) adminSetups();
    }

    public void bind(@NonNull Issue issue, boolean isLast) {
        this.currentIssue = issue;
        this.getIssueTitleView().setText(issue.getTitle());
        this.getIssueDescView().setText(issue.getDescription());
        if (isLast) setVisibility(divider, false);

        if (issue.getPrice().equalsIgnoreCase("free"))
            getPurchaseBtn().setText(activity.getString(R.string.category_read_string));

        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(issue.getIssueImageUri());
        GlideApp.with(activity).load(imgRef)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(getIssueImageView());
    }

    private void findViewsById() {
        issueImage = itemView.findViewById(R.id.product_image);
        issueTitle = itemView.findViewById(R.id.product_title);
        issueDescription = itemView.findViewById(R.id.product_description);
        subscribeBtn = itemView.findViewById(R.id.subscribe_btn);
        purchaseBtn = itemView.findViewById(R.id.purchase_btn);
        divider = itemView.findViewById(R.id.divider);
        container = itemView.findViewById(R.id.itemView);
    }

    private void setupListeners() {
        container.setOnClickListener(this);
        subscribeBtn.setOnClickListener(this);
        purchaseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.itemView: {
                Intent detailIntent = new Intent(itemView.getContext(), DetailActivity.class);
                detailIntent.putExtra(ISSUE_INTENT, currentIssue);
                activity.startActivity(detailIntent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            }
            case R.id.purchase_btn: {
                purchase();
                break;
            }
            case R.id.subscribe_btn: {
                sendSnackbar(rootView, "Subscription successful!");
                break;
            }
        }
    }

    private TextView getIssueTitleView() {
        return issueTitle;
    }

    private ImageView getIssueImageView() {
        return issueImage;
    }

    private TextView getIssueDescView() {
        return issueDescription;
    }

    private Button getSubscribeBtn() {
        return subscribeBtn;
    }

    private Button getPurchaseBtn() {
        return purchaseBtn;
    }

    private void downloadCover() {
        String extension = currentIssue.getIssueImageUri().substring(currentIssue.getIssueImageUri().indexOf("."));

        String folder = new Reference.Builder()
                .setNode(Magazine.class).setNode(currentIssue.getId())
                .setNode(MAGAZINE_COVER_NODE).getNode();
        String fileName = currentIssue.getId() + extension;

        File file = new File(getActivityFromContext(itemView.getContext()).getCacheDir(), folder + fileName);

        if (!file.exists()) {
            DownloadTaskManager downloadTaskManager = new DownloadTaskManager(itemView.getContext(), folder, fileName);

            downloadTaskManager.setCompleteListener((status, extras) -> GlideApp.with(activity)
                    .load(file)
                    .into(getIssueImageView()));

            downloadTaskManager.execute(currentIssue.getIssueImageUri());
        } else
            GlideApp.with(activity)
                    .load(file)
                    .into(getIssueImageView());
    }

    private void adminSetups() {
        //
    }

    private void purchase() {
        if (currentIssue.getPrice().contains("free")){
            //
            return;
        }

        sendResponse(activity, "Not allowed in Beta version!");
    }
}
