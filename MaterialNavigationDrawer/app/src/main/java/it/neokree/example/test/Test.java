package it.neokree.example.test;

import android.graphics.Color;
import android.os.Bundle;

import it.neokree.example.R;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.FragmentIndex;
import it.neokree.example.mockedFragments.FragmentList;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

/**
 * Created by neokree on 22/01/15.
 */
public class Test extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {

        MaterialAccount account = new MaterialAccount(this.getResources(),"NeoKree","neokree@gmail.com",R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount(this.getResources(),"Hatsune Miky","hatsune.miku@example.com",R.drawable.photo2,R.drawable.mat2);
        this.addAccount(account2);

        MaterialAccount account3 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
        this.addAccount(account3);

        MaterialAccount account4 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
        this.addAccount(account4);

        // create sections
        this.addSection(newSection("Section 1", new TestFragment()));
        this.addSection(newSection("Section 2",new FragmentList()));
        this.addSection(newSection("Section 3", R.drawable.ic_mic_white_24dp,new TestFragment()).setSectionColor(Color.parseColor("#9c27b0")));
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new TestFragment()).setSectionColor(Color.parseColor("#03a9f4")));

    }

}
