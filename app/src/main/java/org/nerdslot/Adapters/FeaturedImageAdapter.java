package org.nerdslot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Featured;
import org.nerdslot.R;

import java.util.ArrayList;

public class FeaturedImageAdapter extends RecyclerView.Adapter<FeaturedImageAdapter.ViewHolder> implements RootInterface {
    private ArrayList<Featured> featuredArrayList;

    public FeaturedImageAdapter() {
        featuredArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.slider_view_pager, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Featured image = featuredArrayList.get(position);
        holder.bind(image, position);
    }

    @Override
    public int getItemCount() {
        return featuredArrayList.size();
    }

    public void addFeaturedImages(ArrayList<Featured> arrayList) {
        this.featuredArrayList = arrayList;
        notifyDataSetChanged();
    }

    private void deleteFeaturedImage(int currentPosition) {
        Featured featured = this.featuredArrayList.get(currentPosition);
        new Reference.Builder().setNode(Featured.class).setNode(featured.getId()).getDatabaseReference().removeValue();
        FirebaseStorage.getInstance().getReferenceFromUrl(featured.getImage()).delete(); // Delete Image
        notifyItemRemoved(currentPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        int currentPosition;
        private ImageView featuredImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            featuredImageView = itemView.findViewById(R.id.sliderImage);
            ImageButton deleteBtn = itemView.findViewById(R.id.delete_featured_image);
            setVisibility(deleteBtn, true);
            deleteBtn.bringToFront();
            deleteBtn.setOnClickListener(v -> deleteFeaturedImage(currentPosition));
        }

        void bind(@NotNull Featured featuredImage, int position) {
            currentPosition = position;

            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(featuredImage.getImage());

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(420, 300);
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            featuredImageView.setLayoutParams(layoutParams);
            featuredImageView.setPadding(0, 5, 0, 10);

            GlideApp.with(getActivityFromContext(itemView.getContext()))
                    .load(reference)
                    .into(featuredImageView);
        }
    }
}
