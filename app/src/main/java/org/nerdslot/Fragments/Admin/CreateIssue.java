package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Models.Category;
import org.nerdslot.R;
import org.nerdslot.ViewModels.CategoryViewModel;
import org.nerdslot.ViewModels.IndexList;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class CreateIssue extends Fragment implements AdminInterface {

    private AdminInterface mListener;
    private CategoryViewModel categoryViewModel;
    private AppCompatActivity activity;

    private TextInputEditText issueTitleTextView, issueDescTextView, issuePriceTextView;
    private AutoCompleteTextView categorySpinner, currencySpinner;
    private SwitchMaterial isFeatured;
    private ImageView coverImage, successImageView;
    private MaterialButton coverUploadBtn, selectFileBtn, createIssueBtn;
    private ProgressBar coverUploadProgressBar, fileUploadProgressBar;

    private IndexList<Category> categories = new IndexList<>(Category::getName);
    private String title, description, currency, price;
    private boolean featured;

    public CreateIssue() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_issue, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        categoryViewModel = ViewModelProviders.of(activity).get(CategoryViewModel.class);
        categoryViewModel.all().observe(this, categories -> {
            this.categories = categories;
            Log.i(TAG, "onActivityCreated: Categories Size = " + categories.size());
            populateCategorySpinner();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewsById(view);

        createIssueBtn.setOnClickListener(v -> {
            sendSnackbar(view, "Creating " + issueTitleTextView.getText().toString());
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        if (context instanceof org.nerdslot.Fragments.Admin.AdminInterface)
            mListener = (org.nerdslot.Fragments.Admin.AdminInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement AdminInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCategorySpinner();
    }

    private void findViewsById(@NotNull View view) {
        categorySpinner = view.findViewById(R.id.categories_spinner);
        currencySpinner = view.findViewById(R.id.currency_spinner);
        issueTitleTextView = view.findViewById(R.id.issue_title_text_edit);
        issueDescTextView = view.findViewById(R.id.issue_description_text_edit);
        issuePriceTextView = view.findViewById(R.id.issue_price_text_edit);

        coverImage = view.findViewById(R.id.cover_image);
        coverUploadBtn = view.findViewById(R.id.cover_upload_btn);
        coverUploadProgressBar = view.findViewById(R.id.cover_upload_progress_bar);

        successImageView = view.findViewById(R.id.upload_success_imageView);
        selectFileBtn = view.findViewById(R.id.select_file_btn);
        fileUploadProgressBar = view.findViewById(R.id.file_upload_progress_bar);

        isFeatured = view.findViewById(R.id.is_featured_switch);

        createIssueBtn = view.findViewById(R.id.create_issue_btn);
    }

    private void populateCategorySpinner() {
        ArrayList<String> categoryNames = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            categories.iterator().forEachRemaining(category -> {
                categoryNames.add(category.getName());
            });
        } else {
            for (Category category : categories) {
                categoryNames.add(category.getName());
            }
        }

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, categoryNames);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            categorySpinner.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        }

        categorySpinner.setAdapter(categoriesAdapter);

        populateCurrencySpinner(null);
    }

    private void populateCurrencySpinner(ArrayList<String> data) {
        ArrayList<String> countries = new ArrayList<>();
        countries.add("EURO");
        countries.add("YEN");
        countries.add("USD");
        countries.add("NGN");
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, countries);
        currencySpinner.setAdapter(currencyAdapter);
    }
}
