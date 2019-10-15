package org.nerdslot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.nerdslot.Adapters.ImagePagerAdapter;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;

import java.io.File;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements RootInterface, View.OnClickListener {

    private Issue issue;
    private boolean isFreeIssue;
    private StorageReference magazineReference, imageReference;
    private File folder, file;
    private int fileSize;

    // Views
    private ViewPager imageSlider;
    private TextView titleView, categoryView, descriptionView, sizeView, createdAtView, downloadProgressTextView;
    private ProgressBar downloadProgressBar;
    private Group downloadLayoutGroup;
    private MaterialButton fab, subscribeBtn, purchaseBtn;
    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MaterialToolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, getTheme()));

        findViewsById();

        issue = getIntent().getParcelableExtra(ISSUE_INTENT_KEY);
        isFreeIssue = issue.getPrice().equalsIgnoreCase("free");
        magazineReference = FirebaseStorage.getInstance().getReferenceFromUrl(issue.getMagazine().getMagazineUri());
        imageReference = FirebaseStorage.getInstance().getReferenceFromUrl(issue.getIssueImageUri());
        getSupportActionBar().setTitle(issue.getTitle());

        setupListeners();

        setupPath();

        populateViews();
    }

    private void findViewsById() {
        fab = findViewById(R.id.add_to_cart_fab);
        imageSlider = findViewById(R.id.detail_viewPager);
        titleView = findViewById(R.id.issue_title);
        categoryView = findViewById(R.id.issue_category);
        descriptionView = findViewById(R.id.issue_description);
        sizeView = findViewById(R.id.issue_size);
        createdAtView = findViewById(R.id.created_at_txtView);
        purchaseBtn = findViewById(R.id.purchase_btn);
        subscribeBtn = findViewById(R.id.subscribe_btn);
        container = findViewById(R.id.activity_detail);
        downloadProgressBar = findViewById(R.id.download_progressBar);
        downloadProgressTextView = findViewById(R.id.download_progress_textView);
        downloadLayoutGroup = findViewById(R.id.download_progress_group);
    }

    private void populateViews() {
        titleView.setText(issue.getTitle());
        categoryView.setText(issue.getCategory().getName());
        descriptionView.setText(issue.getDescription());
        createdAtView.setText(issue.created_at());

        addViewPager();

        if (isFreeIssue)
            updateIssue();
    }

    private void setupListeners() {
        subscribeBtn.setOnClickListener(this);
        purchaseBtn.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void addViewPager() {
        imageSlider.setPadding(70, -20, 150, -20);
        imageSlider.setOffscreenPageLimit(4);

        imageSlider.setPageTransformer(false, (page, position) -> {
            final float normalizedposition = Math.abs(Math.abs(position) - 1);
            page.setScaleX(normalizedposition / 2 + 0.5f);
            page.setScaleY(normalizedposition / 2 + 0.5f);
        });

        if (issue.getMagazine().getImages() != null)
            imageSlider.setAdapter(new ImagePagerAdapter(this, issue));
    }

    private void setupPath() {
        String[] split = magazineReference.getPath().split(File.separator);
        String fileName = split[split.length - 1];

        folder = new File(getCacheDir(),
                new Reference.Builder()
                        .setNode(Magazine.class)
                        .setNode(issue.getId())
                        .getNode());
        file = new File(folder, fileName);

        fileSize = Integer.parseInt(String.valueOf(file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
    }

    private void updateIssue() {
        issue.getMagazine().setLocalLocation(file);

        if (fileSize > 1) {
            sizeView.setText(String.valueOf(Float.valueOf(fileSize / 1024)));
            purchaseBtn.setText(getString(R.string.category_read_string));
        } else {
            purchaseBtn.setText(getString(R.string.get_string));
            if (magazineReference != null)
                magazineReference.getMetadata().addOnSuccessListener(storageMetadata -> {
                    sizeView.setText(String.valueOf(Float.valueOf(storageMetadata.getSizeBytes() / (1024 * 1024))));
                });
        }
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

        sendResponse(this, "Oops! Not available in this version");
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
                    sendResponse(DetailActivity.this, e.getMessage());
                });
    }

    private void startMagazineIntent() {
        Intent magazineIntent = new Intent(this, MagazineActivity.class);
        magazineIntent.putExtra(MAGAZINE_INTENT_KEY, file);
        startActivity(magazineIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_to_cart_fab: {
                sendToast(this, issue.getTitle() + " added to cart.");
                break;
            }
            case R.id.purchase_btn: {
                purchase();
                break;
            }

            case R.id.subscribe_btn: {
                sendToast(this, NOT_AVAILABLE_IN_VERSION);
                break;
            }
        }
    }
}
