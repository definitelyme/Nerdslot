package org.nerdslot.Fragments.Admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Adapters.FeaturedImageAdapter;
import org.nerdslot.R;
import org.nerdslot.ViewModels.FeaturedImagesViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminInterface} interface
 * to handle interaction events.
 */
public class FeaturedImages extends Fragment {

    private AdminInterface mListener;
    private AppCompatActivity activity;
    private NavController navController;

    private RecyclerView recyclerView;
    private FeaturedImageAdapter adapter;

    public FeaturedImages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_featured_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.admin_toolbar);
        activity.setSupportActionBar(toolbar);
        setupActionBar(navController);
        activity.getSupportActionBar().setHomeAsUpIndicator(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_back, activity.getTheme()));

        recyclerView = view.findViewById(R.id.featured_image_recyclerView);
        adapter = new FeaturedImageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((AppCompatActivity) getActivity());

        if (activity != null)
            navController = Navigation.findNavController(activity, R.id.admin_fragments);

        if (context instanceof AdminInterface) mListener = (AdminInterface) context;
        else throw new RuntimeException(context.toString()
                + " must implement AdminInterface");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FeaturedImagesViewModel viewModel = ViewModelProviders.of(activity).get(FeaturedImagesViewModel.class);
        viewModel.getFeaturedImages().observeForever(images -> {
            adapter.addFeaturedImages(images);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupActionBar(NavController navController) {
        NavigationUI.setupActionBarWithNavController(activity, navController);
    }
}
