package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.nerdslot.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class CreateIssue extends Fragment {

    private AdminInterface mListener;
    private AppCompatActivity activity;

    private AutoCompleteTextView categorySpinner, currencySpinner;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] categories = new String[]{"Item 1", "Item 2", "Item 3", "Item 4"};
        String[] COUNTRIES = new String[]{"NGN", "USD", "YEN", "EURO"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        activity,
                        R.layout.spinner_item,
                        categories);

        ArrayAdapter<String> currencyAdapter =
                new ArrayAdapter<>(
                        activity,
                        R.layout.spinner_item,
                        COUNTRIES);

        categorySpinner = view.findViewById(R.id.categories_spinner);
        categorySpinner.setAdapter(adapter);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
