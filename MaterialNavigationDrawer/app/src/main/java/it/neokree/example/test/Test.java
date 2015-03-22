package it.neokree.example.test;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import it.neokree.example.R;
import it.neokree.example.mockedFragments.FragmentButton;
import it.neokree.example.mockedFragments.FragmentIndex;
import it.neokree.example.mockedFragments.FragmentList;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

/**
 * Created by neokree on 22/01/15.
 */
public class Test extends MaterialNavigationDrawer {

    MaterialAccount account;
    MaterialSection target;

    @Override
    public void init(Bundle savedInstanceState) {
        account = new MaterialAccount(this.getResources(),"NeoKree","neokree@gmail.com",R.drawable.photo, R.drawable.bamboo);
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount(this.getResources(),"Hatsune Miky","hatsune.miku@example.com",R.drawable.photo2,R.drawable.mat2);
        this.addAccount(account2);

        MaterialAccount account3 = new MaterialAccount(this.getResources(),"Example","example@example.com",R.drawable.photo,R.drawable.mat3);
        this.addAccount(account3);

        // create sections
        this.addSection(newSection("Section 1", new TestFragment()));
        this.addSection(newSection("Section 2",new FragmentList()));
        target = newSection("Section 3", R.drawable.ic_mic_white_24dp,new TestFragment()).setSectionColor(Color.parseColor("#9c27b0"));
        this.addSection(target);
        this.addSection(newSection("Section",R.drawable.ic_hotel_grey600_24dp,new TestFragment()).setSectionColor(Color.parseColor("#03a9f4")));

        enableToolbarElevation();

        thread.start();
    }


    private Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    removeAccount(account);
//                    notifyAccountDataChanged();
//                    removeSection(target);
                    setSection(target);
                }
            });
        }
    });

}
