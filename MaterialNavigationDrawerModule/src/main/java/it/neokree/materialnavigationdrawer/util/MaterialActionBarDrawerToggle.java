package it.neokree.materialnavigationdrawer.util;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

/**
 * Created by neokree on 14/02/15.
 */
public class MaterialActionBarDrawerToggle<Fragment> extends ActionBarDrawerToggle {

    private String titleRequested;
    private Fragment fragmentRequested;
    private Fragment oldFragment;
    private boolean request;

    public MaterialActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    public void addFragmentRequest(Fragment fragment, String title,Fragment oldFragment) {
        request = true;

        titleRequested = title;
        fragmentRequested = fragment;
        this.oldFragment = oldFragment;
    }

    public boolean isFragmentRequested() {
        return request;
    }

    public Fragment getFragmentRequested() {
        return fragmentRequested;
    }

    public String getTitleRequested() {
        return titleRequested;
    }

    public Fragment getOldFragment() {
        return oldFragment;
    }

    public void removeFragmentRequest() {
        request = false;

        titleRequested = null;
        fragmentRequested = null;
        oldFragment = null;
    }
}
