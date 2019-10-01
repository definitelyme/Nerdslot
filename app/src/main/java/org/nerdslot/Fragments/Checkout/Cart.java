package org.nerdslot.Fragments.Checkout;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.nerdslot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment {

    private CheckoutInterface mListener;

    public Cart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CheckoutInterface) {
            mListener = (CheckoutInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CheckoutInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
