package it.neokree.example;


import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer implements MaterialAccountListener{

    MaterialAccount account;
    MaterialSection section1, section2, recorder, night, last, settingsSection;

    @Override
    public void init(Bundle savedInstanceState) {

        // add first account
        account = new MaterialAccount("NeoKree","neokree@gmail.com",new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.bamboo));
        this.addAccount(account);

        MaterialAccount account2 = new MaterialAccount("NeoKree","neokree@gmail.com",getResources().getDrawable(R.drawable.photo2),this.getResources().getDrawable(R.drawable.bamboo));
        this.addAccount(account2);

        // set listener
        this.setAccountListener(this);

        // create sections
        section1 = this.newSection("Section 1",new F1());
        section2 = this.newSection("Section 2",new F2());
        // recorder section with icon and 10 notifications
        recorder = this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp),new F3()).setNotifications(10);
        // night section with icon, section color and notifications
        night = this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_hotel_grey600_24dp), new FragmentIndex())
                .setSectionColor(Color.parseColor("#2196f3")).setNotifications(150);
        // night section with section color
        last = this.newSection("Last Section", new FragmentIndex()).setSectionColor((Color.parseColor("#ff9800")));
        settingsSection = this.newSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),new FragmentIndex());

        // add your sections to the drawer
        this.addSection(section1);
        this.addSection(section2);
        this.addDivisor();
        this.addSection(recorder);
        this.addSection(night);
        this.addDivisor();
        this.addSection(last);
        this.addBottomSection(settingsSection);

        // start thread
        t.start();

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
                //Log.w("PHOTO","user account photo setted");


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
}
