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

    @Override
    public void init(Bundle savedInstanceState) {

        // add first account
        account = new MaterialAccount("NeoKree","neokree@gmail.com",new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.bamboo));
        this.addAccount(account);

        // set listener
        this.setAccountListener(this);

        // add your sections
        this.addSection(this.newSection("Section 1",new FragmentIndex()));
        this.addSection(this.newSection("Section 2",new FragmentIndex()));
        this.addDivisor();
        this.addSection(this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp),new FragmentIndex()).setNotifications(10));

        // add custom colored section with icon
        this.addSection(this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_hotel_grey600_24dp), new FragmentIndex())
                .setSectionColor(Color.parseColor("#2196f3")).setNotifications(150)); // material blue 500

        this.addDivisor();
        // add custom colored section with only text
        this.addSection(this.newSection("Last Section", new FragmentIndex()).setSectionColor((Color.parseColor("#ff9800")))); // material orange 500

        Intent i = new Intent(this,Settings.class);
        this.addBottomSection(this.newBottomSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),i));

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

    @Override
    protected void onResume() {
        super.onResume();

        t.start();
    }

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
                    }
                });
                Log.w("PHOTO","user account photo setted");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
}
