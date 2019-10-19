package org.nerdslot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ImagePagerAdapter extends PagerAdapter implements RootInterface {
    private Context context;
    private ArrayList<String> imageUris;

    public ImagePagerAdapter(Context context, @NotNull Issue issue) {
        this.context = context;
        this.imageUris = new ArrayList<>();

        String[] split = issue.getMagazine().getImages().split(STRING_URI_SEPERATOR);
        this.imageUris.addAll(Arrays.asList(split));
    }

    @Override
    public int getCount() {
        return this.imageUris.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.item_viewpager, container, false);

        String imageUri = this.imageUris.get(position);
        ImageView pagerImage = layout.findViewById(R.id.issue_image);

        // Reference
        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
        GlideApp.with(getActivityFromContext(context)).load(imgRef)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into(pagerImage);

        container.addView(layout, 0);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
