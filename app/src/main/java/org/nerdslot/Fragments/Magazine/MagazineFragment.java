package org.nerdslot.Fragments.Magazine;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.nerdslot.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MagazineFragment extends Fragment {

    private static final String TAB_POSITION = "page-position";
    private AppCompatActivity activity;
    private View childView;
    private MagazineInterface listener;
    private int position;

    public MagazineFragment() {
    }

    public static MagazineFragment newInstance(int position) {
        MagazineFragment fragment = new MagazineFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) this.position = getArguments().getInt(TAB_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_magazine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout parentView = view.findViewById(R.id.issue_fragment);
        this.childView = this.listener.onFragmentReady(this.position);
        if (this.childView != null) {
            parentView.addView(this.childView);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (AppCompatActivity) getActivity();

        if (context instanceof MagazineInterface) listener = (MagazineInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement MagazineInterface");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = this.childView;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }
}