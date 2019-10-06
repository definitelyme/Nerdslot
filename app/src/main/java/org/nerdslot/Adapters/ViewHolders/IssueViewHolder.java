package org.nerdslot.Adapters.ViewHolders;

import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.Main.MainInterface;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;
import org.nerdslot.Network.ConnectionManager;
import org.nerdslot.R;

import java.io.File;

public class IssueViewHolder extends RecyclerView.ViewHolder implements MainInterface, View.OnClickListener, View.OnLongClickListener {
    private AppCompatActivity activity;
    private ImageView issueImage;
    private TextView issueTitle, issueDescription;
    private MaterialButton subscribeBtn, purchaseBtn;
    private Issue currentIssue;
    private View rootView;

    public IssueViewHolder(@NonNull View itemView) {
        super(itemView);

        activity = getActivityFromContext(itemView.getContext());
        rootView = activity.findViewById(R.id.main_activity);

        MaterialCardView cardView = itemView.findViewById(R.id.cardView);
        cardView.setBackgroundResource(R.drawable.ripple_rectangle);

        findViewsById();

        setupListeners();
    }

    public void bind(@NonNull Issue issue) {
        this.currentIssue = issue;
        this.getIssueTitleView().setText(issue.getTitle());
        this.getIssueDescView().setText(issue.getDescription());

        downloadCoverImage(issue);
    }

    private void findViewsById() {
        issueImage = itemView.findViewById(R.id.product_image);
        issueTitle = itemView.findViewById(R.id.product_title);
        issueDescription = itemView.findViewById(R.id.product_description);
        subscribeBtn = itemView.findViewById(R.id.subscribe_btn);
        purchaseBtn = itemView.findViewById(R.id.purchase_btn);
    }

    private void setupListeners() {
        itemView.setOnClickListener(this);
        subscribeBtn.setOnClickListener(this);
        purchaseBtn.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardView: {
                break;
            }
            case R.id.purchase_btn: {
                sendSnackbar(rootView, "Item added to Cart.", "Checkout");
                break;
            }
            case R.id.subscribe_btn: {
                sendSnackbar(rootView, "Subscription successful!");
                break;
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Snackbar.make(rootView, "Remove " + getIssueTitleView().getText() + "?", BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction("Nuke it", v -> Toast.makeText(activity, "Deleted!", Toast.LENGTH_SHORT).show())
                .setActionTextColor(activity.getResources().getColor(R.color.lightGrey2))
                .setBackgroundTint(activity.getResources().getColor(R.color.colorPrimaryDark2))
                .show();
        return false;
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

    public Button getSubscribeBtn() {
        return subscribeBtn;
    }

    public Button getPurchaseBtn() {
        return purchaseBtn;
    }

    private void downloadCoverImage(@NonNull Issue issue) {
        String id = issue.getId();
        StorageReference reference = FirebaseStorage.getInstance().getReference(issue.getIssueImageUri());
        File coverFolder = new File(activity.getCacheDir(),
                new Reference.Builder()
                        .setNode(Magazine.class)
                        .setNode(id)
                        .setNode(MAGAZINE_COVER_NODE)
                        .getNode());

        String extension = issue.getIssueImageUri().substring(issue.getIssueImageUri().indexOf("."));

        File cover = new File(coverFolder, issue.getTitle() + extension);
        int imageSize = Integer.parseInt(String.valueOf(cover.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));

        if (!coverFolder.exists() && imageSize < 1) {
            coverFolder.mkdirs();
            reference.getFile(cover)
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        sendResponse(activity, (int) progress + "%");
                    })
                    .addOnFailureListener(this); // Download Cover Image
        }

        if (ConnectionManager.isConnectionAvailable(activity))
            GlideApp.with(activity)
                    .load(reference)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .into(getIssueImageView());

        else if (imageSize > 0) {
            Picasso.with(activity)
                    .load(cover)
                    .into(getIssueImageView());
        }
    }
}
