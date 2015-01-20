package it.neokree.example.functionalities.master_child;

import android.os.Bundle;

import it.neokree.example.R;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 20/01/15.
 */
public class MasterChildActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {

        this.setDrawerHeaderImage(R.drawable.mat3);

        this.addSection(this.newSection("Section 1", new MasterFragment()));
        this.addSection(this.newSection("Section 2",new MasterFragment()));

    }

    @Override
    protected void onStart() {
        super.onStart();

        // set the indicator for child fragments
        // N.B. call this method AFTER the init() to leave the time to instantiate the ActionBarDrawerToggle
        this.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public void onHomeAsUpSelected() {
        // when the back arrow is selected this method is called

    }
}
