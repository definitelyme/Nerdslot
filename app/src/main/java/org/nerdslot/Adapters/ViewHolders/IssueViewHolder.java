package org.nerdslot.Adapters.ViewHolders;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.R;

public class IssueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private AppCompatActivity activity;
    private ImageView issueImage;
    private TextView issueTitle, issueDescription;
    private MaterialButton subscribeBtn, purchaseBtn;
    private Issue currentIssue;
    private View rootView;

    public IssueViewHolder(@NonNull View itemView) {
        super(itemView);

        activity = ((AppCompatActivity) itemView.getContext());
        rootView = activity.findViewById(R.id.main_activity);

        CardView cardView = itemView.findViewById(R.id.cardView);
        cardView.setBackgroundResource(R.drawable.ripple_rectangle);

        issueImage = itemView.findViewById(R.id.product_image);
        issueTitle = itemView.findViewById(R.id.product_title);
        issueDescription = itemView.findViewById(R.id.product_description);
        subscribeBtn = itemView.findViewById(R.id.subscribe_btn);
        purchaseBtn = itemView.findViewById(R.id.purchase_btn);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        subscribeBtn.setOnClickListener(v -> {
            Snackbar subscribeSn = Snackbar.make(rootView, "Subscription successful!", BaseTransientBottomBar.LENGTH_LONG);
            subscribeSn.setAction("Okay", v1 -> {
                subscribeSn.dismiss();
            });
            subscribeSn.setActionTextColor(Color.WHITE);
            subscribeSn.show();
        });

        purchaseBtn.setOnClickListener(v -> {
            Snackbar purchaseSn = Snackbar.make(rootView, "Item added to Cart.", BaseTransientBottomBar.LENGTH_LONG);
            purchaseSn.setAction("Checkout", v1 -> {
                purchaseSn.dismiss();
//                Navigation.findNavController(v).navigate(HomeFragmentDirections.actionBottomNavHomeToCheckoutFragment());
            });
            purchaseSn.setActionTextColor(Color.WHITE);
            purchaseSn.show();
        });
    }

    public void bind(@NonNull Issue issue) {
        this.currentIssue = issue;
        this.getIssueTitleView().setText(issue.getTitle());
        this.getIssueDescView().setText(issue.getDescription());
//        this.getIssueImageView().setImageBitmap(issue.getImage());
    }

    @Override
    public void onClick(View v) {
//        HomeFragmentDirections.ActionBottomNavHomeToDetailFragment toDetailFragment = HomeFragmentDirections.actionBottomNavHomeToDetailFragment(currentIssue);
//        Navigation.findNavController(rootView).navigate(toDetailFragment);
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
}
