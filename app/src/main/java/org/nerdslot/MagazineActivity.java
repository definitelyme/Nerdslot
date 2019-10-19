package org.nerdslot;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.mertakdut.BookSection;
import com.github.mertakdut.CssStatus;
import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;
import com.google.firebase.appindexing.Indexable;

import org.nerdslot.Fragments.Magazine.MagazineFragment;
import org.nerdslot.Fragments.Magazine.MagazineInterface;

import java.io.File;

public class MagazineActivity extends AppCompatActivity implements MagazineInterface {

    private int pageCount = Integer.MAX_VALUE;
    private int pxScreenWidth;
    private Reader reader;
    private ProgressBar loader;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private View container;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine);

        findViewsById();

        if (getIntent() != null && getIntent().getExtras() != null) {
            File magazine = (File) getIntent().getSerializableExtra(MAGAZINE_INTENT_KEY);
            new ReaderTask().execute(magazine);
        }
    }

    private void findViewsById() {
        this.viewPager = findViewById(R.id.magazin_viewPager);
        this.loader = findViewById(R.id.magazine_loader);
        this.container = findViewById(R.id.magazine_activity);

        this.pxScreenWidth = getResources().getDisplayMetrics().widthPixels;
        this.sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0);
        this.viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public View onFragmentReady(int i) {
        BookSection bookSection = null;
        try {
            bookSection = this.reader.readSection(i);
        } catch (ReadingException e) {
            sendResponse(e.getLocalizedMessage(), e);
        } catch (OutOfPagesException ex) {
            sendResponse(ex.getLocalizedMessage(), ex);
            this.pageCount = ex.getPageCount();
            this.sectionsPagerAdapter.notifyDataSetChanged();
        }
        if (bookSection != null) {
            return loadMagazine(bookSection.getSectionContent(), "text/html; chartset=UTF-8", "base64");
        }
        return null;
    }

    private View loadMagazine(String data, String mimeType, String encoding) {
        String data2 = data.replace("\"images/data:image/", "\"data:image/")
                .replace("../Images/", "");

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        WebView webView = new WebView(this);

        webView.loadDataWithBaseURL(null, data2, mimeType, encoding, null);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setLayoutParams(layoutParams);

        return webView;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onStop() {
        super.onStop();

        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    reader.saveProgress(viewPager.getCurrentItem());
                    sendResponse("Current Progress Saved");
                } catch (ReadingException e) {
                    sendResponse(e.getLocalizedMessage(), e);
                } catch (Exception e) {
                    sendResponse("Progress not saved! Out of Bounds!", e);
                }
                return null;
            }
        }.execute((Void) null);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return MagazineFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return pageCount;
        }
    }

    public class ReaderTask extends AsyncTask<File, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            setVisibility(loader, true);
        }

        @Override
        protected Boolean doInBackground(File... files) {
            File epubFile = files[0];

            try {
                reader = new Reader();
                reader.setMaxContentPerSection(Indexable.MAX_STRING_LENGTH);
                reader.setCssStatus(CssStatus.INCLUDE);
                reader.setIsIncludingTextContent(true);
                reader.setIsOmittingTitleTag(true);
                reader.setFullContent(epubFile.getPath());
                return true;
            } catch (ReadingException e) {
                sendResponse(e.getLocalizedMessage(), e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                setVisibility(loader, false);
                viewPager.setAdapter(sectionsPagerAdapter);
                if (reader != null && reader.isSavedProgressFound()) {
                    try {
                        viewPager.setCurrentItem(reader.loadProgress());
                    } catch (ReadingException e) {
                        sendResponse(e.getLocalizedMessage(), e);
                    }
                }
            }
        }
    }
}
