package it.neokree.example.light;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import it.neokree.example.R;
import it.neokree.example.mockedActivity.Settings;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.FragmentIndex;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by neokree on 18/01/15.
 */
public class CustomDrawerHeader extends MaterialNavigationDrawer {
    @Override
    public void init(Bundle savedInstanceState) {

        // create and set the header
        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer,null);
        setDrawerHeaderCustom(view);

        // create sections
        this.addSection(newSection("Section 1", new FragmentIndex()));
        this.addSection(newSection("Section 2",new FragmentIndex()));
        this.addSection(newSection("Section 3", R.drawable.ic_mic_white_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new FragmentButton()).setSectionColor(Color.parseColor("#03a9f4")));

        // create bottom section
        this.addBottomSection(newSection("Bottom Section",R.drawable.ic_settings_black_24dp,new Intent(this,Settings.class)));

    }
}
