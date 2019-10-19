package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Adapters.CoverImageAdapter;
import org.nerdslot.Foundation.Helper.IndexList;
import org.nerdslot.Foundation.Helper.Upload;
import org.nerdslot.Foundation.Reference;
import org.nerdslot.Fragments.Admin.Impl.CreateIssueInterface;
import org.nerdslot.Models.Category;
import org.nerdslot.Models.Issue.Featured;
import org.nerdslot.Models.Issue.Issue;
import org.nerdslot.Models.Issue.Magazine;
import org.nerdslot.R;
import org.nerdslot.ViewModels.CategoryViewModel;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class CreateIssue extends Fragment implements CreateIssueInterface {

    private AdminInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;

    private TextInputLayout titleInputLayout, priceInputLayout, categorySpinnerLayout;
    private TextInputEditText issueTitleTextView, issueDescTextView, issuePriceTextView;
    private AutoCompleteTextView categorySpinner;
    private SwitchMaterial isFeaturedSwitch, isFreeSwitch;
    private ImageView coverImage;
    private Group isFeaturedGroup;
    private MaterialButton coverUploadBtn, selectFileBtn, createIssueBtn;
    private ProgressBar fileUploadProgressBar;
    private View[] viewGroup;
    private View container;

    private RecyclerView coverRecycler;
    private CoverImageAdapter imageAdapter;

    private Issue issue;
    private Category category;
    private IndexList<Category> categories;
    private ArrayList<String> categoryNames;
    private String id, title, description, category_id, currency, price;
    private boolean isFeatured, isFree, isEditing;

    private Upload upload;

    public CreateIssue() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            issue = CreateIssueArgs.fromBundle(getArguments()).getIssue();
            isEditing = issue != null;
        }
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

        CategoryViewModel categoryViewModel = ViewModelProviders.of(activity).get(CategoryViewModel.class);
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

        Toolbar toolbar = view.findViewById(R.id.admin_toolbar);
        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);
        activity.getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, activity.getTheme()));

        findViewsById(view);

        coverUploadBtn.setOnClickListener(this);
        selectFileBtn.setOnClickListener(this);
        createIssueBtn.setOnClickListener(this);
        coverImage.setOnClickListener(this);

        setupListeners();
        upload = new Upload(this);
        currency = "NGN";

        if (issue != null) populateFields_IssueUpdate(issue);
    }

    private void findViewsById(@NotNull View view) {
        container = view.findViewById(R.id.create_issue_fragment);
        categorySpinnerLayout = view.findViewById(R.id.category_spinner_layout);
        categorySpinner = view.findViewById(R.id.categories_spinner);

        titleInputLayout = view.findViewById(R.id.issue_title_input_layout);
        priceInputLayout = view.findViewById(R.id.issue_price_input_layout);

        issueTitleTextView = view.findViewById(R.id.issue_title_text_edit);
        issueDescTextView = view.findViewById(R.id.issue_description_text_edit);
        issuePriceTextView = view.findViewById(R.id.issue_price_text_edit);

        coverRecycler = view.findViewById(R.id.cover_recyclerView);

        coverImage = view.findViewById(R.id.cover_image);
        coverUploadBtn = view.findViewById(R.id.cover_upload_btn);

        selectFileBtn = view.findViewById(R.id.select_file_btn);
        fileUploadProgressBar = view.findViewById(R.id.file_upload_progress_bar);

        isFeaturedSwitch = view.findViewById(R.id.is_featured_switch);
        isFreeSwitch = view.findViewById(R.id.is_free_switch);

        createIssueBtn = view.findViewById(R.id.create_issue_btn);

        viewGroup = new View[]{categorySpinnerLayout, titleInputLayout, priceInputLayout};
        isFeaturedGroup = view.findViewById(R.id.addFeaturedGroup);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        coverRecycler.setLayoutManager(layoutManager);
        imageAdapter = new CoverImageAdapter();
    }

    private void populateCategorySpinner(ArrayList<String> categoryNames) {
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(activity, R.layout.spinner_item, categoryNames);
        categorySpinner.setAdapter(categoriesAdapter);
    }

    private void validate() {
        resetError(viewGroup);
        setEnabled(createIssueBtn, false); //Disable Button

        boolean cancel = false;
        View focusView = null;

        title = String.valueOf(issueTitleTextView.getText());
        description = String.valueOf(issueDescTextView.getText());
        price = String.valueOf(issuePriceTextView.getText());
        upload.imageUris = imageAdapter.getImageUris();

        if (TextUtils.isEmpty(title)) {
            setError(titleInputLayout);
            focusView = issueTitleTextView;
            cancel = true;
        }

        if (TextUtils.isEmpty(price)) {
            if (isFreeSwitch.isChecked()) {
                price = String.valueOf(isFree);
                return;
            }
            setError(priceInputLayout);
            focusView = issuePriceTextView;
            cancel = true;
        }

        if (TextUtils.isEmpty(category_id)) {
            setError(categorySpinnerLayout);
            cancel = true;
        }

        if (upload.epubUri == null) {
            sendToast(activity, "Please select Epub!");
            cancel = true;
        }

        if (upload.imageUris.isEmpty()) {
            sendToast(activity, "Select an Image first!");
            cancel = true;
        }

        if (!cancel) { // Cancel == false
            if (isEditing) {
                updateIssue();
                return;
            }
            createIssue();
        } else {
            // There was an error; don't create Issue
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
            setEnabled(createIssueBtn, true); // Enable Button if errors exists
        }
    }

    private void populateFields_IssueUpdate(@NonNull Issue issue) {
        // Set Properties
        id = issue.getId();
        title = issue.getTitle();
        description = issue.getDescription();
        price = issue.getPrice();
        currency = issue.getCurrency();
        category = issue.getCategory();
        category_id = category.getId();
        String[] uriList = issue.getMagazine().getImages().split(STRING_URI_SEPERATOR);
        upload.epubUri = Uri.parse(issue.getMagazine().getMagazineUri());

        // Set Views
        issueTitleTextView.setText(title);
        issueDescTextView.setText(description);
        issuePriceTextView.setText(price);
        isFreeSwitch.setChecked(issue.getPrice().equalsIgnoreCase(getString(R.string.upp_free_string)));
        categorySpinner.setText(category.getName());
        for (String uri : uriList) {
            imageAdapter.addImage(Uri.parse(uri));
            coverRecycler.setAdapter(imageAdapter);
        }
        ContextCompat.getDrawable(activity, R.drawable.ic_ok);
        selectFileBtn.setText(String.format("File uploaded - %s", title));
        createIssueBtn.setText(getString(R.string.update_issue_string));
    }

    private void setupListeners() {
        isFeaturedSwitch.setOnCheckedChangeListener(this);
        isFreeSwitch.setOnCheckedChangeListener(this);

        issueTitleTextView.addTextChangedListener(this);
        issuePriceTextView.addTextChangedListener(this);

        issueTitleTextView.setOnFocusChangeListener(this);
        issuePriceTextView.setOnFocusChangeListener(this);

        categorySpinner.setOnItemClickListener((adapterView, view, i, l) -> {
            resetError(categorySpinnerLayout);
            String categoryName = adapterView.getItemAtPosition(i).toString();
            category = categories.get(categories.indexOf(categoryName));
            category_id = category.getId();
        });
    }

    private void setupActionBar(NavController navController) {
        NavigationUI.setupActionBarWithNavController(activity, navController);
    }

    private void createIssue() {
        mListener.showOverlay("Upload in progress... \nEnsure strong internet connection!\nPlease wait.");
        DatabaseReference issueRef = new Reference.Builder().setNode(Issue.class).getDatabaseReference();

        id = issueRef.push().getKey();
        issue = new Issue.Builder()
                .setId(id).setCategory_id(category_id).setCategory(category)
                .setMagazine_id(upload.getMagazineSessionKey())
                .setTitle(title).setDescription(description).setCurrency(currency)
                .setPrice(price).setRateCount(0.0)
                .build();

        issueRef.child(id).setValue(issue);

        upload.setOnCompleteListener((status, data) -> {
            mListener.hideOverlay();
            if (!status) sendFullResponse(activity, container, "Oops! Poor Internet connection.");
            else {
                sendFullResponse(activity, container, title + " created successfully!");
            }
            resetAll();
        });
        upload.cover__(id);
        upload.magazine__();
    }

    private void createFeaturedIssue() {
        upload.imageUris = imageAdapter.getImageUris();
        mListener.showOverlay("Good internet connection required!!\n\nPlease wait.");
        if (upload.imageUris.isEmpty()) {
            sendToast(activity, "Select an Image from File!");
            mListener.hideOverlay();
            return;
        }

        upload.setOnCompleteListener((status, data) -> {
            mListener.hideOverlay();
            if (status) {
                id = (String) data[0];
                Featured featured = new Featured.Builder().setId(id).setImage((String) data[1]).build();
                new Reference.Builder().setNode(Featured.class).getDatabaseReference().child(id).setValue(featured);
            }
            if (!status) {
                sendResponse(activity, "Slider image(s) uploaded!");
                resetAll();
            }
        });

        mListener.showOverlay("Good internet connection required!!\n\nPlease wait.");
        upload.slider__();
    }

    private void updateIssue() {
        mListener.showOverlay("Upload in progress... \nEnsure strong internet connection!\nPlease wait.");

        upload.setOnCompleteListener((status, data) -> {
            if (status) {
                Magazine magazine = (Magazine) data[1];
                issue = new Issue.Builder()
                        .setId(id).setCategory_id(category_id).setCategory(category)
                        .setTitle(title).setDescription(description)
                        .setMagazine_id(magazine.getId())
                        .setIssueImageUri((String) data[0])
                        .setMagazine(magazine).setCurrency(currency)
                        .setPrice(price).setRateCount(0.0)
                        .build();
                new Reference.Builder().setNode(Issue.class)
                        .getDatabaseReference().child(id).setValue(issue);

                mListener.hideOverlay();
                sendFullResponse(activity, container, title + " updated successfully!");
            }

            if (!status) sendFullResponse(activity, container, "Oops! Poor Internet connection.");
            resetAll();
        });
        upload.updateIssue(issue);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.admin_fragments);

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_issue_btn: {
                if (isFeatured) {
                    createFeaturedIssue();
                    break;
                }
                validate();
                break;
            }

            case R.id.cover_upload_btn:
            case R.id.cover_image: {
                upload.__construct(MIME_TYPE.IMAGE);
                break;
            }

            case R.id.select_file_btn: {
                upload.__construct(MIME_TYPE.EPUB);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            if (upload.getMimeType().equals(MIME_TYPE.IMAGE.toString())) {
                imageAdapter.addImage(uri);
                coverRecycler.setAdapter(imageAdapter);
            } else if (upload.getMimeType().equals(MIME_TYPE.EPUB.toString())) {
                upload.epubUri = uri;

                String[] segments = uri.getPath().split("/");
                String lastSegment = segments[segments.length - 1];

                ContextCompat.getDrawable(activity, R.drawable.ic_ok);
                selectFileBtn.setText(String.format("%s", lastSegment));
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.is_featured_switch: {
                isFeatured = isChecked;
                setVisibility(!isChecked, isFeaturedGroup);
                resetView(createIssueBtn, isChecked ? "Add featured Images" : getString(R.string.create_issue_string));
                resetView(coverUploadBtn, isChecked ? getString(R.string.upload_slider_string) : getString(R.string.upload_cover_string));
                break;
            }
            case R.id.is_free_switch: {
                isFree = isChecked;
                resetView(issuePriceTextView, isChecked ? getString(R.string.upp_free_string) : "");
                setEnabled(priceInputLayout, !isChecked);
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()) {
            case R.id.issue_title_text_edit: {
                if (!hasFocus)
                    validateTextInput(titleInputLayout, String.valueOf(((TextInputEditText) view).getText()));
                break;
            }
            case R.id.issue_price_text_edit: {
                if (!hasFocus)
                    validateTextInput(priceInputLayout, String.valueOf(((TextInputEditText) view).getText()));
                break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (issueTitleTextView.getText().hashCode() == editable.hashCode())
            resetError(titleInputLayout);
        else if (issuePriceTextView.getText().hashCode() == editable.hashCode())
            resetError(priceInputLayout);
    }

    private void resetAll() {
        resetViews(issueTitleTextView, issuePriceTextView);
        resetView(issueDescTextView, "No description available!");
        resetView(createIssueBtn, getString(R.string.create_issue_string));
        setEnabled(createIssueBtn, true);
        isFreeSwitch.setChecked(false);
        isFeaturedSwitch.setChecked(false);

        setVisibility(coverImage, false);
        setVisibility(coverUploadBtn, true);
        setVisibility(selectFileBtn, true);
        setVisibility(false, fileUploadProgressBar);

        selectFileBtn.setIcon(ContextCompat.getDrawable(activity, R.drawable.ic_upload_ios)); // Set Icon from Button
        selectFileBtn.setIconGravity(MaterialButton.ICON_GRAVITY_START); // Set Icon Gravity
        selectFileBtn.setIconTintResource(R.color.white); // Set Icon TINT
        selectFileBtn.setText(String.format("%s", getString(R.string.select_file_string))); // Set Text
        imageAdapter.resetAdapter();
    }
}
