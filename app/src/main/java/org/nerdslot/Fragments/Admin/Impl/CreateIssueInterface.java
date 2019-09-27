package org.nerdslot.Fragments.Admin.Impl;

import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import org.nerdslot.Fragments.Admin.AdminInterface;

public interface CreateIssueInterface extends AdminInterface, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener, TextWatcher {

    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
}
