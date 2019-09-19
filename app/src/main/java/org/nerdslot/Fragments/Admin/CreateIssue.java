package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Models.Category;
import org.nerdslot.Models.Issue.Issue;
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
public class CreateIssue extends Fragment implements AdminInterface, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private AdminInterface mListener;
    private CategoryViewModel categoryViewModel;
    private AppCompatActivity activity;

    private TextInputEditText issueTitleTextView, issueDescTextView, issuePriceTextView;
    private AutoCompleteTextView categorySpinner, currencySpinner;
    private SwitchMaterial isFeaturedSwitch, isFreeSwitch;
    private ImageView coverImage, successImageView;
    private MaterialButton coverUploadBtn, selectFileBtn, createIssueBtn;
    private ProgressBar coverUploadProgressBar, fileUploadProgressBar;

    private Issue issue;
    private IndexList<Category> categories;
    private ArrayList<String> categoryNames;
    private String id, title, description, category_id, magazine_id, currency, price, coverUrl;
    private boolean isFeatured, isFree;

    private DatabaseReference issueNodeReference = FireUtil.databaseReference(new Issue());

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
            this.categories = new IndexList<>(Category::getName);

            categoryNames = new ArrayList<>();
            categoryNames.clear();

            for (Category category : categories) {
                categoryNames.add(category.getName());
                this.categories.add(category);
            }

            populateCategorySpinner(categoryNames);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewsById(view);

        createIssueBtn.setOnClickListener(v -> {
            validate();
            sendSnackbar(view, "Creating " + issueTitleTextView.getText().toString());
        });

        setupListeners();
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

        isFeaturedSwitch = view.findViewById(R.id.is_featured_switch);
        isFreeSwitch = view.findViewById(R.id.is_free_switch);

        createIssueBtn = view.findViewById(R.id.create_issue_btn);
    }

    private void populateCategorySpinner(ArrayList<String> categoryNames) {

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, categoryNames);

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

    private void validate() {
        boolean cancel = false;
        View focusView = null;

        title = issueTitleTextView.getText().toString();
        description = issueDescTextView.getText().toString();
        price = issuePriceTextView.getText().toString();

        if (TextUtils.isEmpty(title)){
            issueTitleTextView.setError(getString(R.string.required_field));
            focusView = issueTitleTextView;
            cancel = true;
        }

        if (TextUtils.isEmpty(price) && !isFreeSwitch.isChecked()){
            issuePriceTextView.setError(getString(R.string.required_field));
            focusView = issuePriceTextView;
            cancel = true;
        }

        if (TextUtils.isEmpty(category_id)){
            categorySpinner.setError(getString(R.string.required_field));
            cancel = true;
        }

        if (TextUtils.isEmpty(currency)){
            currencySpinner.setError(getString(R.string.required_field));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't create Issue
            // form field with an error.
            focusView.requestFocus();
        } else {
            createIssue();
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.is_featured_switch: {
                isFeatured = isChecked;
                break;
            }
            case R.id.is_free_switch: {
                isFree = isChecked;
                issuePriceTextView.setText(isChecked ? "FREE" : "0");
                setEnabled(issuePriceTextView, !isChecked);
                setVisibility(issuePriceTextView, isChecked ? View.GONE : View.VISIBLE);
                break;
            }
        }
    }

    private void setupListeners() {
        isFeaturedSwitch.setOnCheckedChangeListener(this);
        isFreeSwitch.setOnCheckedChangeListener(this);

        currencySpinner.setOnItemClickListener((adapterView, view, i, l) -> {
            String currency = adapterView.getItemAtPosition(i).toString();
            this.currency = currency;
        });
        categorySpinner.setOnItemClickListener((adapterView, view, i, l) -> {
            String categoryName = adapterView.getItemAtPosition(i).toString();
            Category category = categories.get(categories.indexOf(categoryName));
            category_id = category.getId();
        });
    }

    private void createIssue(){
        issue = new Issue.Builder()
                .setId(issueNodeReference.push().getKey())
                .setCategory_id(category_id)
                .setMagazine_id(magazine_id)
                .setTitle(title)
                .setDescription(description)
                .setCurrency(currency)
                .setPrice(price)
                .setFeatured(isFeatured)
                .setIssueImageUri(coverUrl)
                .setRateCount(0.0)
                .build();
        Log.i(TAG, "createIssue: Successful!");
    }
}
