package org.nerdslot.Adapters;

import androidx.cardview.widget.CardView;

import org.nerdslot.Fragments.RootInterface;

public interface AdapterInterface extends RootInterface {
    default CardView getCardViewAt(int position) {
        return null;
    }
    default int getCount() {
        return 0;
    }
}
