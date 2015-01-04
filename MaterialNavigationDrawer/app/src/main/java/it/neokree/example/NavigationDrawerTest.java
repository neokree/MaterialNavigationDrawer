package it.neokree.example;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;
import it.neokree.materialnavigationdrawer.MaterialSectionListener;

/**
 * Created by neokree on 30/12/14.
 */

public class NavigationDrawerTest extends MaterialNavigationDrawer implements MaterialAccountListener, MaterialSectionListener {

    MaterialAccount account;
    MaterialSection section1, section2, section3, section4, section5, section6, recorder, night, last, settingsSection;

    @Override
    public void init(Bundle savedInstanceState) {


        this.allowArrowAnimation();
        // add first account

        account = new MaterialAccount("Hello " + getUsername(), "", new ColorDrawable(Color.parseColor("#9e9e9e")), this.getResources().getDrawable(R.drawable.mat2));
        this.addAccount(account);

        // set listener
        this.setAccountListener(this);

        // create sections
        section1 = this.newSection("String1", new FragmentIndex());
        section2 = this.newSection("String2", new FragmentIndex());
        section3 = this.newSection("Help", new FragmentIndex());
        section4 = this.newSection("Send Feedback", new FragmentIndex());
        section5 = this.newSection("Rate our app", new FragmentIndex());
        section6 = this.newSection("About", new FragmentIndex());

        Intent i = null;
        settingsSection = this.newSection("Settings", this.getResources().getDrawable(R.drawable.ic_settings_black_24dp), i);

        // add your sections to the drawer
        this.addSubheader("Header1");
        this.addSection(section1);
        this.addSection(section2);

        this.addSubheader("General");
        this.addSection(section3);
        this.addSection(section4);
        this.addSection(section5);
        this.addSection(section6);

        this.addBottomSection(settingsSection);

        this.addMultiPaneSupport();
    }

    private String getUsername() {
        return "Username";
    }

    @Override
    public void onAccountOpening(MaterialAccount account) {

    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {

    }
}
