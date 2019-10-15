package org.nerdslot.Adapters.ViewHolders;

import android.content.Intent;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
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
import org.nerdslot.MagazineActivity;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;
import org.nerdslot.R;

import java.io.File;
import java.util.Locale;

public class IssueViewHolder extends RecyclerView.ViewHolder implements MainInterface, View.OnClickListener {
    private AppCompatActivity activity;
    private Issue currentIssue;
    private StorageReference magazineReference, imageReference;
    private boolean isFreeIssue;
    private File folder, file;
    private int fileSize;

    // Views
    private ImageView issueImage;
    private TextView issueTitle, issueDescription, downloadProgressTextView;
    private MaterialButton subscribeBtn, purchaseBtn;
    private ProgressBar downloadProgressBar;
    private View rootView, divider, container;
    private Group downloadLayoutGroup;

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
        isFreeIssue = issue.getPrice().equalsIgnoreCase("free");
        this.getIssueTitleView().setText(issue.getTitle());
        this.getIssueDescView().setText(issue.getDescription());
        if (isLast) setVisibility(divider, false);

        magazineReference = FirebaseStorage.getInstance().getReferenceFromUrl(currentIssue.getMagazine().getMagazineUri());
        imageReference = FirebaseStorage.getInstance().getReferenceFromUrl(issue.getIssueImageUri());

        setupPath();
        updateIssue();

        GlideApp.with(activity).load(imageReference)
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

        downloadProgressBar = itemView.findViewById(R.id.download_progressBar);
        downloadProgressTextView = itemView.findViewById(R.id.download_progress_textView);
        downloadLayoutGroup = itemView.findViewById(R.id.download_progress_group);
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
                detailIntent.putExtra(ISSUE_INTENT_KEY, currentIssue);
                activity.startActivity(detailIntent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            }
            case R.id.purchase_btn: {
                purchase();
                break;
            }
            case R.id.subscribe_btn: {
                sendSnackbar(rootView, NOT_AVAILABLE_IN_VERSION);
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
        if (isFreeIssue) {
            if (folder.exists() || fileSize > 1) {
                startMagazineIntent();
                return;
            }
            downloadMagazine();
            return;
        }

        sendResponse(activity, "Oops! Not available in this version");
    }

    private void downloadMagazine() {
        folder.mkdirs();
        magazineReference.getFile(file)
                .addOnProgressListener(taskSnapshot -> {
                    int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    setVisibility(downloadLayoutGroup, true);
                    downloadProgressBar.setProgress(progress);
                    downloadProgressTextView.setText(String.format(Locale.getDefault(), "%d/100", progress));
                })
                .addOnSuccessListener(taskSnapshot -> {
                    setVisibility(downloadLayoutGroup, false);
                    fileSize = Integer.parseInt(String.valueOf(file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
                    updateIssue();
                })
                .addOnFailureListener(e -> {
                    sendResponse(getActivityFromContext(itemView.getContext()), e.getMessage());
                });
    }

    private void setupPath() {
        String[] split = magazineReference.getPath().split(File.separator);
        String fileName = split[split.length - 1];

        folder = new File(activity.getCacheDir(),
                new Reference.Builder()
                        .setNode(Magazine.class)
                        .setNode(currentIssue.getId())
                        .getNode());
        file = new File(folder, fileName);

        fileSize = Integer.parseInt(String.valueOf(file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
    }

    private void updateIssue() {
        currentIssue.getMagazine().setLocalLocation(file);

        if (fileSize > 1)
            getPurchaseBtn().setText(activity.getString(R.string.category_read_string));

        if (isFreeIssue && fileSize < 1)
            getPurchaseBtn().setText(activity.getString(R.string.get_string));
    }

    private void startMagazineIntent() {
        Intent magazineIntent = new Intent(itemView.getContext(), MagazineActivity.class);
        magazineIntent.putExtra(MAGAZINE_INTENT_KEY, file);
        activity.startActivity(magazineIntent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
