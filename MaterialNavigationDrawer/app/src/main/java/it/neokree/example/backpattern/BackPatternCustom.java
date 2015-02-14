package it.neokree.example.backpattern;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.Settings;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.FragmentIndex;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by neokree on 14/02/15.
 */
public class BackPatternCustom extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        this.addSection(newSection("Section 1", new FragmentIndex()));
        this.addSection(newSection("Section 2",new FragmentIndex()));
        this.addSection(newSection("Section 3", R.drawable.ic_mic_white_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#03a9f4")));

        // create bottom section
        this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

        // add pattern
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);
    }

    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {
        MaterialSection section;
        switch(currentSection.getPosition()) {
            case 3:
                section =this.getSectionAtCurrentPosition(2);
                this.changeToolbarColor(section); // remember to change the toolbar color
                break;
            case 2:
                section =  this.getSectionAtCurrentPosition(1);
                this.changeToolbarColor(section); // remember to change the toolbar color
                break;
            case 1:
                section = this.getSectionAtCurrentPosition(0);
                this.changeToolbarColor(section); // remember to change the toolbar color
                break;
            default:
                section = super.backToSection(currentSection); // exit from activity
                break;
        }

        return section;
    }
}
