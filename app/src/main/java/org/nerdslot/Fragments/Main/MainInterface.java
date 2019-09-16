package org.nerdslot.Fragments.Main;

import org.nerdslot.Fragments.RootInterface;

import q.rorbin.badgeview.Badge;

public interface MainInterface extends RootInterface {
    int NAVIGATION_HOME = 0;
    int NAVIGATION_DISCOVER = 1;
    int NAVIGATION_ACCOUNT = 2;
    int NAVIGATION_MENU = 3;
    Badge makeBadge(int position, int number);
    void updateBadge(int position, int number);
    void removeBadge(int position);
}