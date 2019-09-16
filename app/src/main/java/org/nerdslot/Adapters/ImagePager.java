package org.nerdslot.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePager extends PagerAdapter {
    private Context ctx;
    private List<CardView> mViews;
    private ArrayList<Bitmap> sliderImages;

    public ImagePager(Context ctx, ArrayList<Bitmap> sliderImages) {
        this.ctx = ctx;
        this.mViews = new ArrayList<>();
        this.sliderImages = sliderImages;
    }

    @Override
    public int getCount() {
        return sliderImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.recycler_detail_image, container, false);
        bind(sliderImages.get(position), view); // Set the Image with the File URI path

        ViewPager newPager = (ViewPager) container; // Cast the container ViewGroup to a new ViewPager
        newPager.addView(view, 0); // Finally add the CardView to the ViewPager
        return view;
    }

    private void bind(@NotNull Bitmap image, @NotNull View view) {
        ImageView imageView = view.findViewById(R.id.detail_image); // Get ImageView
//        singleImage.setImageURI(Uri.fromFile(new File(item.toURI())));
        imageView.setImageBitmap(image); // This represents a single singleImage in the ArrayList
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager pager = (ViewPager) container;
        View v = (CardView) object;
        pager.removeView(v);
    }
}
