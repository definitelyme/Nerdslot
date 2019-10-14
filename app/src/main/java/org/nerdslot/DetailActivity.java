package org.nerdslot;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.nerdslot.Adapters.ImagePagerAdapter;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Issue;

import java.io.File;

public class DetailActivity extends AppCompatActivity implements RootInterface {

    private Issue issue;

    private LinearLayout viewPagerLayout;
    private ViewPager imageSlider;
    private TextView titleView, categoryView, descriptionView, sizeView;
    private MaterialButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, getTheme()));

        findViewsById();

        issue = getIntent().getParcelableExtra(ISSUE_INTENT);
        getSupportActionBar().setTitle(issue.getTitle());

        populateViews();
        setupListeners();
    }

    private void setupListeners() {
        fab.setOnClickListener(view ->
                Toast.makeText(this, issue.getTitle() + " added to cart.", Toast.LENGTH_SHORT).show());
    }

    private void findViewsById() {
        fab = findViewById(R.id.fab);
//        imageSlider = findViewById(R.id.imageSlider);
        titleView = findViewById(R.id.issue_title);
        categoryView = findViewById(R.id.issue_category);
        descriptionView = findViewById(R.id.issue_description);
        sizeView = findViewById(R.id.issue_size);
        viewPagerLayout = findViewById(R.id.view_pager_layout);
    }

    private void populateViews() {
        titleView.setText(issue.getTitle());
        categoryView.setText(issue.getCategory_id());
        descriptionView.setText(issue.getDescription());

        viewPagerLayout.addView(addViewPager());
    }

    private void populateImageSlider() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "nerdslot/img_3.jpg");
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(this);
        mAdapter.addCardItem(new ImagePagerAdapter.ImageItem(issue.getIssueImageUri()));
        mAdapter.addCardItem(new ImagePagerAdapter.ImageItem(file.getPath()));
        imageSlider.setAdapter(mAdapter);
    }

    private ViewPager addViewPager(){
        imageSlider = new ViewPager(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(280, LinearLayout.LayoutParams.MATCH_PARENT);
        imageSlider.setLayoutParams(layoutParams);
        imageSlider.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        imageSlider.setClipToPadding(false);

        populateImageSlider();
        return imageSlider;
    }
}
