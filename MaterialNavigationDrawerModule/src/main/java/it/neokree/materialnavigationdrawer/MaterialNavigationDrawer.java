package it.neokree.materialnavigationdrawer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.util.LinkedList;
import java.util.List;

/**
 * Activity that implements ActionBarActivity with a Navigation Drawer with Material Design style
 *
 * @author created by neokree
 */
@SuppressLint("InflateParams")
public abstract class MaterialNavigationDrawer<Fragment> extends ActionBarActivity implements MaterialSectionListener {

    public static final int BOTTOM_SECTION_START = 100;

    private DrawerLayout layout;
    private ActionBar actionBar;
    private ActionBarDrawerToggle pulsante;
    private Toolbar toolbar;
    private RelativeLayout drawer;
    private ImageView userphoto;
    private ImageView userSecondPhoto;
    private ImageView userThirdPhoto;
    private ImageView usercover;
    private TextView username;
    private TextView usermail;
    private LinearLayout sections;
    private LinearLayout bottomSections;

    private List<MaterialSection> sectionList;
    private List<MaterialSection> bottomSectionList;
    private List<MaterialAccount> accountManager;
    private MaterialSection currentSection;
    private MaterialAccount currentAccount;

    private CharSequence title;
    private static int indexFragment = 0;
    private float density;
    private int primaryColor;

    private View.OnClickListener currentAccountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // enter into account properties

            if(accountListener != null) {
                accountListener.onAccountOpening(currentAccount);
            }

            // close drawer
            layout.closeDrawer(drawer);

        }
    };
    private View.OnClickListener secondAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // account change
            MaterialAccount account = findAccountNumber(MaterialAccount.SECOND_ACCOUNT);
            if(account != null) {
                if (accountListener != null)
                    accountListener.onChangeAccount(account);

                switchAccounts(account);
            }
            else  // if there is no second account user clicked for open it
                accountListener.onAccountOpening(currentAccount);


            // close drawer
            layout.closeDrawer(drawer);
        }
    };
    private View.OnClickListener thirdAccountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // account change
            MaterialAccount account = findAccountNumber(MaterialAccount.THIRD_ACCOUNT);
            if(account != null) {
                if (accountListener != null)
                    accountListener.onChangeAccount(account);

                switchAccounts(account);
            }
            else  // if there is no second account user clicked for open it
                accountListener.onAccountOpening(currentAccount);

            // close drawer
            layout.closeDrawer(drawer);
        }
    };
    private MaterialAccountListener accountListener;

    @Override
    /**
     * Do not Override this method!!! <br>
     * Use init() instead
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_navigation_drawer);

        // init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // init drawer components
        drawer = (RelativeLayout) this.findViewById(R.id.drawer);
        username = (TextView) this.findViewById(R.id.user_nome);
        usermail = (TextView) this.findViewById(R.id.user_email);
        userphoto = (ImageView) this.findViewById(R.id.user_photo);
        userSecondPhoto = (ImageView) this.findViewById(R.id.user_photo_2);
        userThirdPhoto = (ImageView) this.findViewById(R.id.user_photo_3);
        usercover = (ImageView) this.findViewById(R.id.user_cover);
        sections = (LinearLayout) this.findViewById(R.id.sections);
        bottomSections = (LinearLayout) this.findViewById(R.id.bottom_sections);

        // init lists
        sectionList = new LinkedList<>();
        bottomSectionList = new LinkedList<>();
        accountManager = new LinkedList<>();

        // init listeners
        userphoto.setOnClickListener(currentAccountListener);
        usercover.setOnClickListener(currentAccountListener);
        userSecondPhoto.setOnClickListener(secondAccountListener);
        userThirdPhoto.setOnClickListener(thirdAccountListener);

        //get density
        density = this.getResources().getDisplayMetrics().density;

        // get primary color
        Resources.Theme theme = this.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        primaryColor = typedValue.data;

        init(savedInstanceState);

        if(sectionList.size() == 0) {
            throw new RuntimeException("You must add at least one Section to top list.");
        }

        // INIT ACTION BAR
        this.setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // Si preleva il titolo dell'activity
        title = sectionList.get(indexFragment).getTitle();

        // si collega il DrawerLayout al codice e gli si setta l'ombra all'apertura
        layout = (DrawerLayout) this.findViewById(R.id.drawer_layout);

        pulsante = new ActionBarDrawerToggle(this,layout,R.string.nothing,R.string.nothing) {

            public void onDrawerClosed(View view) {
                actionBar.setTitle(title);
                invalidateOptionsMenu(); // termina il comando
            }

            public void onDrawerOpened(View drawerView) {
                //actionBar.setTitle(getCurrentTitle(-1));
                invalidateOptionsMenu(); // termina il comando
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}

        };

        layout.setDrawerListener(pulsante);

        // init account views
        switch(accountManager.size()) {
            case 3:
                this.setThirdAccountPhoto(accountManager.get(2).getCircularPhoto());
            case 2:
                this.setSecondAccountPhoto(accountManager.get(1).getCircularPhoto());
            case 1:
                MaterialAccount account = accountManager.get(0);
                this.setFirstAccountPhoto(account.getCircularPhoto());
                this.setDrawerBackground(account.getBackground());
                this.setUsername(account.getTitle());
                this.setUserEmail(account.getSubTitle());
                currentAccount = account;
            default:
        }


        // init section
        MaterialSection section = sectionList.get(0);
        currentSection = section;
        section.select();
        setFragment((Fragment) section.getTargetFragment(),section.getTitle());
    }



    // Gestione dei Menu -----------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return super.onCreateOptionsMenu(menu);
    }

    /* Chiamata dopo l'invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se dal drawer si seleziona un oggetto
        if (pulsante.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        pulsante.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {// al cambio di orientamento dello schermo
        super.onConfigurationChanged(newConfig);

        // Passa tutte le configurazioni al drawer
        pulsante.onConfigurationChanged(newConfig);

    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        this.getSupportActionBar().setTitle(title);
    }

    private void setFragment(Fragment fragment,String title) {
        
        
        // change for default Fragment / support Fragment
        if(fragment instanceof android.app.Fragment)
            getFragmentManager().beginTransaction().replace(R.id.frame_container, (android.app.Fragment)fragment).commit();
        else if(fragment instanceof android.support.v4.app.Fragment)
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,(android.support.v4.app.Fragment) fragment).commit();
        else
            throw new RuntimeException("Fragment must be android.app.Fragment or android.support.v4.app.Fragment");
        
        // si setta il titolo e si chiude il drawer
        //lista.setItemChecked(i, true);
        setTitle(title);
        layout.closeDrawer(drawer);
    }

    private MaterialAccount findAccountNumber(int number) {
        for(MaterialAccount account : accountManager)
            if(account.getAccountNumber() == number)
                return account;


        return null;
    }

    private void switchAccounts( MaterialAccount newAccount ) {
        // new account will be currentAccount

        // switch numbers
        currentAccount.setAccountNumber(newAccount.getAccountNumber());
        newAccount.setAccountNumber(MaterialAccount.FIRST_ACCOUNT);

        // change pointer to newAccount
        currentAccount = newAccount;

        // refresh views
        switch(accountManager.size()) {
            case 3:
                this.setThirdAccountPhoto(findAccountNumber(MaterialAccount.THIRD_ACCOUNT).getCircularPhoto());
            case 2:
                this.setSecondAccountPhoto(findAccountNumber(MaterialAccount.SECOND_ACCOUNT).getCircularPhoto());
            case 1:
                this.setFirstAccountPhoto(currentAccount.getCircularPhoto());
                this.setDrawerBackground(currentAccount.getBackground());
                this.setUsername(currentAccount.getTitle());
                this.setUserEmail(currentAccount.getSubTitle());
            default:
        }
    }

    private void setUserEmail(String email) {
        this.usermail.setText(email);
    }

    private void setUsername(String username) {
        this.username.setText(username);
    }

    private void setFirstAccountPhoto(Bitmap photo) {
        userphoto.setImageBitmap(photo);
    }

    private void setSecondAccountPhoto(Bitmap photo) {
        userSecondPhoto.setImageBitmap(photo);
    }

    private void setThirdAccountPhoto(Bitmap photo) {
        userThirdPhoto.setImageBitmap(photo);
    }

    private void setDrawerBackground(Bitmap background) {
        usercover.setImageBitmap(background);
    }

    @Override
    public void onClick(MaterialSection section) {
        currentSection = section;

        if(section.getTarget() == MaterialSection.TARGET_FRAGMENT)
        {
            setFragment((Fragment)section.getTargetFragment(),section.getTitle());

            // setting toolbar color if is setted
            if(section.hasSectionColor()) {
                this.getToolbar().setBackgroundColor(section.getSectionColor());
            }
            else {
                this.getToolbar().setBackgroundColor(primaryColor);
            }
        }
        else {
            this.startActivity(section.getTargetIntent());
        }

        int position = section.getPosition();

        for(MaterialSection mySection : sectionList) {
            if(position != mySection.getPosition())
                mySection.unSelect();
        }
        for(MaterialSection mySection : bottomSectionList) {
            if(position != mySection.getPosition())
                mySection.unSelect();
        }

    }

    public void setAccountListener(MaterialAccountListener listener) {
        this.accountListener = listener;
    }

    // Method used for customize layout

    public void addSection(MaterialSection section) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48 * density));
        sectionList.add(section);
        sections.addView(section.getView(),params);
    }

    public void addBottomSection(MaterialSection section) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48 * density));
        bottomSectionList.add(section);
        bottomSections.addView(section.getView(),params);
    }

    public void addDivisor() {
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        // height 1 px
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));

        sections.addView(view, params);
    }

    public void addAccount(MaterialAccount account) {
        if (accountManager.size() == 3)
            throw new RuntimeException("Currently are supported max 3 accounts");

        account.setAccountNumber(accountManager.size());
        accountManager.add(account);
    }

    // create sections

    public MaterialSection newSection(String title, Drawable icon, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),true,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),true,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),true,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),true,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),false,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,sectionList.size(),false,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Drawable icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),true,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Drawable icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),true,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Bitmap icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),true,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Bitmap icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),true,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),false,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newBottomSection(String title, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,BOTTOM_SECTION_START + bottomSectionList.size(),false,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    // abstract methods

    public abstract void init(Bundle savedInstanceState);

    // get methods

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MaterialSection getCurrentSection() {
        return currentSection;
    }

    public MaterialAccount getCurrentAccount() {
        return currentAccount;
    }
}