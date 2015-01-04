package it.neokree.example;


import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;
import it.neokree.materialnavigationdrawer.MaterialSectionListener;

public class NavigationDrawerWithAccountActivity extends MaterialNavigationDrawer implements MaterialAccountListener{

    MaterialAccount account;
    MaterialSection section1, section2, recorder, night, last, settingsSection;

    @Override
    public void init(Bundle savedInstanceState) {


        // add first account
        account = new MaterialAccount("NeoKree","neokree@gmail.com",new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.bamboo));
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount("Hatsune Miky","hatsune.miku@example.com",getResources().getDrawable(R.drawable.photo2),this.getResources().getDrawable(R.drawable.mat2));
        this.addAccount(account2);

        MaterialAccount account3 = new MaterialAccount("Example","example@example.com",getResources().getDrawable(R.drawable.photo),this.getResources().getDrawable(R.drawable.mat3));
        this.addAccount(account3);

        // set listener
        this.setAccountListener(this);

        //this.replaceDrawerHeader(this.getResources().getDrawable(R.drawable.mat2));

        // create sections
        section1 = this.newSection("Section 1",new FragmentIndex());
        section2 = this.newSection("Section 2",new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                Toast.makeText(NavigationDrawerWithAccountActivity.this,"Section 2 Clicked",Toast.LENGTH_SHORT).show();

                section.unSelect();
            }
        });
        // recorder section with icon and 10 notifications
        recorder = this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp),new FragmentIndex()).setNotifications(10);
        // night section with icon, section color and notifications
        night = this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_hotel_grey600_24dp), new FragmentIndex())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0")).setNotifications(150);
        // night section with section color
        last = this.newSection("Last Section", new FragmentIndex()).setSectionColor(Color.parseColor("#ff9800"),Color.parseColor("#ef6c00"));

        Intent i = new Intent(this,Profile.class);
        settingsSection = this.newSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),i);

        // add your sections to the drawer
        this.addSection(section1);
        this.addSection(section2);
        this.addSubheader("Subheader");
        this.addSection(recorder);
        this.addSection(night);
        this.addDivisor();
        this.addSection(last);
        this.addBottomSection(settingsSection);

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);

        // start thread
        t.start();

    }

    @Override
    protected MaterialSection backToSection(MaterialSection currentSection) {

        if(currentSection == night) {
            return last;
        }

        if(currentSection == last) {
            return section1;
        }

        return currentSection;
    }

    @Override
    public void onAccountOpening(MaterialAccount account) {
        // open profile activity
        Intent i = new Intent(this,Profile.class);
        startActivity(i);
    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {
        // when another account is selected
    }

    // after 5 second (async task loading photo from website) change user photo
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                account.setPhoto(getResources().getDrawable(R.drawable.photo));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAccountDataChanged();
                        Toast.makeText(getApplicationContext(),"Loaded 'from web' user image",Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
}
