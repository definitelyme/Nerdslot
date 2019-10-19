package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;

import org.nerdslot.Foundation.FireUtil;
import org.nerdslot.Fragments.RootInterface;
import org.nerdslot.Models.Category;
import org.nerdslot.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class CreateCategory extends Fragment implements RootInterface {

    private AdminInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;
    private DatabaseReference databaseReference;

    private TextInputLayout categoryNameLayout, categoryDescLayout;
    private TextInputEditText categoryNameInput, categoryDescriptionInput;
    private Button createCategoryBtn;
    private String categoryName, categoryDescription;
    private View[] viewGroup;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        Toolbar toolbar = view.findViewById(R.id.admin_toolbar);
        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);
        activity.getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, activity.getTheme()));

        categoryNameLayout = view.findViewById(R.id.category_name_layout);
        categoryDescLayout = view.findViewById(R.id.category_desc_layout);

        categoryNameInput = view.findViewById(R.id.category_name_editText);
        categoryDescriptionInput = view.findViewById(R.id.category_description_edidText);
        createCategoryBtn = view.findViewById(R.id.create_category_btn);

        viewGroup = new View[]{categoryNameLayout, categoryDescLayout, createCategoryBtn};

        createCategoryBtn.setOnClickListener(v -> validateFields());
    }

    private void setupActionBar(NavController navController) {
        NavigationUI.setupActionBarWithNavController(activity, navController);
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

    private void validateFields() {
        resetError(categoryNameLayout);

        setEnabled(false, viewGroup);

        boolean cancel = false;
        View focusView = null;

        categoryName = String.valueOf(categoryNameInput.getText());
        categoryDescription = String.valueOf(categoryDescriptionInput.getText());

        if (TextUtils.isEmpty(categoryName)) {
            setError(categoryNameLayout);
            focusView = categoryNameInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't save data
            // form field with an error.
            focusView.requestFocus();
            setEnabled(true, viewGroup);
        } else createCategory();
    }

    private void createCategory() {
        DatabaseReference reference = FireUtil.firebaseDatabase.getReference().child(new Category().getNode());
        String key = reference.push().getKey();

        Category category = new Category.Builder()
                .setId(key)
                .setName(categoryName)
                .setDescription(categoryDescription)
                .build();

        reference.child(key).setValue(category);

        resetViews(categoryNameInput, categoryDescriptionInput);
        setEnabled(true, viewGroup);
        sendToast(activity, "Category " + category.getName() + " created!");
    }
}
