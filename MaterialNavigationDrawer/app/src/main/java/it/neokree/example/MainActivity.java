package it.neokree.example;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

public class MainActivity extends MaterialNavigationDrawer {


    @Override
    public Fragment getCurrentFragment(int position) {
        switch(position) {
            case MaterialNavigationDrawer.SECTION_START:
                return new FragmentIndex();
            case MaterialNavigationDrawer.SECTION_START + 1:
                return new FragmentIndex();

            case MaterialNavigationDrawer.BOTTOM_SECTION_START:
                return new FragmentSettings();

            default: return new Fragment();
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // set cover background
        this.setDrawerBackground(this.getResources().getDrawable(R.drawable.mat1));
        // set user photo and data
        this.setUserPhoto(this.getResources().getDrawable(R.drawable.photo));
        this.setUsername("NeoKree");
        this.setUserEmail("neokree@gmail.com");

        // add your sections
        this.addSection(this.newSection("Section 1"));
        this.addSection(this.newSection("Section 2",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp)));
        this.addBottomSection(this.newBottomSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp)));
    }

    @Override
    public String getCurrentTitle(int position) {
        switch(position) {
            case MaterialNavigationDrawer.SECTION_START:
                return "My First Section";
            case MaterialNavigationDrawer.SECTION_START + 1:
                return "My Second Section";

            case MaterialNavigationDrawer.BOTTOM_SECTION_START:
                return "Settings";

            default: return "";
        }
    }

    public class FragmentIndex extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            TextView text = new TextView(this.getActivity());
            text.setText("Section");
            text.setGravity(Gravity.CENTER);
            return text;
        }
    }

    public class FragmentSettings extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            TextView text = new TextView(this.getActivity());
            text.setText("Settings");
            text.setGravity(Gravity.CENTER);
            return text;
        }
    }
}
