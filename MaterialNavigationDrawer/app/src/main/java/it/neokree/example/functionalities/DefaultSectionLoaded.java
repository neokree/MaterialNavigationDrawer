package it.neokree.example.functionalities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.Settings;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.FragmentIndex;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 08/03/15.
 */
public class DefaultSectionLoaded extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        this.addSection(newSection("Section 1", new FragmentIndex())); // number 0
        this.addSection(newSection("Section 2",new FragmentIndex())); // number 1
        this.addSection(newSection("Section 3", R.drawable.ic_mic_white_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#9c27b0"))); // number 2
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#03a9f4"))); // number 3

        // create bottom section
        this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

        // Programmatical call (unused because it is already setted by theme)
        // setDefaultSectionLoaded(1);
    }
}
