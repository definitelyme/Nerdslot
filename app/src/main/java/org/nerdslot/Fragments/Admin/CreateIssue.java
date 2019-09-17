package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import org.nerdslot.Fragments.Admin.Navigation.HomeViewModel;
import org.nerdslot.Models.Category;
import org.nerdslot.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class CreateIssue extends Fragment implements AdminInterface {

    private AdminInterface mListener;
    private HomeViewModel mViewModel;
    private AppCompatActivity activity;
    private Category aCategory;

    private TextInputEditText issueTitleTextView, issueDescTextView, issuePriceTextView;
    private AutoCompleteTextView categorySpinner, currencySpinner;
    private SwitchMaterial isFeatured;
    private ImageView coverImage, successImageView;
    private ImageButton coverUploadBtn, selectFileBtn;
    private ProgressBar coverUploadProgressBar, fileUploadProgressBar;

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
        mViewModel = ViewModelProviders.of(activity).get(HomeViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViewsById(view);

        String[] COUNTRIES = new String[]{"NGN", "USD", "YEN", "EURO"};

        ArrayAdapter<String> currencyAdapter =
                new ArrayAdapter<>(
                        activity,
                        R.layout.spinner_item,
                        COUNTRIES);


        currencySpinner = view.findViewById(R.id.currency_spinner);
        currencySpinner.setAdapter(currencyAdapter);
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
    public void onResume() {
        super.onResume();
        mViewModel.getLiveCategories().observe(this, categories -> {
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (Category category : mViewModel.categories) {
                stringArrayList.add(category.getName());
            }
            populateSpinner(stringArrayList);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void findViewsById(View view) {
        categorySpinner = view.findViewById(R.id.categories_spinner);
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
    }

    private void populateSpinner(ArrayList<String> names) {
        ArrayAdapter<String> categoriesAdapter =
                new ArrayAdapter<>(
                        activity,
                        R.layout.spinner_item,
                        names);

        categorySpinner.setAdapter(categoriesAdapter);
    }
}
