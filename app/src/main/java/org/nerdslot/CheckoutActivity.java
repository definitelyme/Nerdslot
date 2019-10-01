package org.nerdslot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.nerdslot.Fragments.Checkout.CheckoutInterface;

public class CheckoutActivity extends AppCompatActivity implements CheckoutInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
    }
}
