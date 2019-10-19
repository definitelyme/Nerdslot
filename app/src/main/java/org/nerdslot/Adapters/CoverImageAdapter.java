package org.nerdslot.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.R;

import java.util.ArrayList;

public class CoverImageAdapter extends RecyclerView.Adapter<CoverImageAdapter.CoverImageViewHolder> implements RootInterface {
    private ArrayList<Uri> issueImageUris;

    public CoverImageAdapter() {
        this.issueImageUris = new ArrayList<>();
    }

    @NonNull
    @Override
    public CoverImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.recycler_cover_item, parent, false);
        return new CoverImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoverImageViewHolder holder, int position) {
        Uri uri = issueImageUris.get(position);
        holder.bind(uri, position);
    }

    @Override
    public int getItemCount() {
        return issueImageUris.size();
    }

    public void addImage(Uri uri) {
        this.issueImageUris.add(uri);
        notifyDataSetChanged();
    }

    private void removeImage(int position) {
        this.issueImageUris.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, getItemCount());
    }

    public void resetAdapter(){
        issueImageUris.clear();
        notifyDataSetChanged();
    }

    public ArrayList<Uri> getImageUris() {
        return issueImageUris;
    }

    class CoverImageViewHolder extends RecyclerView.ViewHolder {
        int currentPosition;
        ImageView coverImage;
        ImageButton removeImage;

        CoverImageViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.cover_image);
            removeImage = itemView.findViewById(R.id.delete_issue_image);
            removeImage.setOnClickListener(v -> removeImage(currentPosition));
        }

        void bind(Uri uri, int position) {
            currentPosition = position;
            boolean fileExists = uri.toString().contains("gs://nerdslot-x.appspot.com");

            coverImage.getLayoutParams().height = 250;
            coverImage.getLayoutParams().width = 180;

            StorageReference ref = null;
            if (fileExists)
                ref = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
            GlideApp.with(getActivityFromContext(itemView.getContext()))
                    .load(fileExists ? ref : uri)
                    .centerCrop()
                    .into(coverImage);
        }
    }
}
