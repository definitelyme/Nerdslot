package org.nerdslot.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nerdslot.Foundation.Helper.GlideApp;
import org.nerdslot.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter implements AdapterInterface {
    private Context context;
    private List<CardView> mViews;
    private List<ImageItem> mData;

    public ImagePagerAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_viewpager, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = view.findViewById(R.id.cardView);

        mViews.set(position, cardView);
        return view;
    }

    public void addCardItem(ImageItem itemHolder) {
        mViews.add(null);
        mData.add(itemHolder);
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(ImageItem item, View view) {
        ImageView imageView = view.findViewById(R.id.sliderImage);
        final String imageUri = item.getSliderImage();

        StorageReference imgRef = null;
        if (!imageUri.contains("img_3"))
            imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
        GlideApp.with(getActivityFromContext(context)).load(imgRef != null ? imgRef : imageUri)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .into(imageView);
    }

    public static class ImageItem {
        private String sliderImage;

        public ImageItem(String uri) {
            this.sliderImage = uri;
        }

        public String getSliderImage() {
            return sliderImage;
        }

        public void setSliderImage(String sliderImage) {
            this.sliderImage = sliderImage;
        }
    }
}
