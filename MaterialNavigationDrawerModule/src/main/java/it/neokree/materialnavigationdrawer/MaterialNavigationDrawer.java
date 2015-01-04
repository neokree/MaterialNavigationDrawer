package it.neokree.materialnavigationdrawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity that implements ActionBarActivity with a Navigation Drawer with Material Design style
 *
 * @author created by neokree
 */
@SuppressLint("InflateParams")
public abstract class MaterialNavigationDrawer<Fragment> extends ActionBarActivity implements MaterialSectionListener {

    public static final int BOTTOM_SECTION_START = 100;
    private static final int USER_CHANGE_TRANSITION = 500;

    public static final int BACKPATTERN_BACK_ANYWHERE = 0;
    public static final int BACKPATTERN_BACK_TO_FIRST = 1;
    public static final int BACKPATTERN_CUSTOM = 2;

    private static final int DRAWERHEADER_ACCOUNTS = 0;
    private static final int DRAWERHEADER_IMAGE = 1;
    private static final int DRAWERHEADER_CUSTOM = 2;
    private static final int DRAWERHEADER_NO_HEADER = 3;

    private static final String STATE_SECTION = "section";
    private static final String STATE_ACCOUNT = "account";

    private MaterialDrawerLayout layout;
    private ActionBar actionBar;
    private ActionBarDrawerToggle pulsante;
    private ImageView statusBar;
    private Toolbar toolbar;
    private RelativeLayout content;
    private RelativeLayout drawer;
    private ImageView userphoto;
    private ImageView userSecondPhoto;
    private ImageView userThirdPhoto;
    private ImageView usercover;
    private ImageView usercoverSwitcher;
    private TextView username;
    private TextView usermail;
    private LinearLayout customDrawerHeader;
    private LinearLayout sections;
    private LinearLayout bottomSections;


    private List<MaterialSection> sectionList;
    private List<MaterialSection> bottomSectionList;
    private List<MaterialAccount> accountManager;
    private MaterialSection currentSection;
    private MaterialAccount currentAccount;

    private CharSequence title;
    private float density;
    private int primaryColor;
    private int primaryDarkColor;
    private boolean drawerTouchLocked = false;
    private boolean slidingDrawerEffect = false;
    private boolean multiPaneSupport = false;
    private boolean rippleSupport;
    private boolean kitkatTraslucentStatusbar = false;
    private static boolean learningPattern = true;
    private Resources resources;
    private int backPattern = BACKPATTERN_BACK_ANYWHERE;
    private int drawerHeaderType;

    private View.OnClickListener currentAccountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if(!drawerTouchLocked) {
                // enter into account properties
                if (accountListener != null) {
                    accountListener.onAccountOpening(currentAccount);
                }

                // close drawer
                if (!deviceSupportMultiPane())
                    layout.closeDrawer(drawer);
            }
        }
    };
    private View.OnClickListener secondAccountListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!drawerTouchLocked) {

                // account change
                MaterialAccount account = findAccountNumber(MaterialAccount.SECOND_ACCOUNT);
                if (account != null) {
                    if (accountListener != null)
                        accountListener.onChangeAccount(account);

                    switchAccounts(account);
                } else {// if there is no second account user clicked for open it
                    accountListener.onAccountOpening(currentAccount);
                    if (!deviceSupportMultiPane())
                        layout.closeDrawer(drawer);
                }
            }

        }
    };
    private View.OnClickListener thirdAccountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(!drawerTouchLocked) {

                // account change
                MaterialAccount account = findAccountNumber(MaterialAccount.THIRD_ACCOUNT);
                if (account != null) {
                    if (accountListener != null)
                        accountListener.onChangeAccount(account);

                    switchAccounts(account);
                } else {// if there is no second account user clicked for open it
                    accountListener.onAccountOpening(currentAccount);
                    if (!deviceSupportMultiPane())
                        layout.closeDrawer(drawer);
                }
            }
        }
    };
    private MaterialAccountListener accountListener;
    private DrawerLayout.DrawerListener drawerListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        // init the Calligraphy library
        super.attachBaseContext(new CalligraphyContextWrapper(newBase,R.attr.neokree_fontPath));
    }

    @Override
    /**
     * Do not Override this method!!! <br>
     * Use init() instead
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf", R.attr.neokree_fontPath);

        Resources.Theme theme = this.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.drawerType,typedValue,true);
        drawerHeaderType = typedValue.data;
        theme.resolveAttribute(R.attr.rippleBackport,typedValue,false);
        rippleSupport = typedValue.data != 0;

        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS)
            setContentView(R.layout.activity_material_navigation_drawer);
        else
            setContentView(R.layout.activity_material_navigation_drawer_customheader);

        // init toolbar & status bar
        statusBar = (ImageView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // init drawer components
        layout = (MaterialDrawerLayout) this.findViewById(R.id.drawer_layout);
        content = (RelativeLayout) this.findViewById(R.id.content);
        drawer = (RelativeLayout) this.findViewById(R.id.drawer);
        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
            username = (TextView) this.findViewById(R.id.user_nome);
            usermail = (TextView) this.findViewById(R.id.user_email);
            userphoto = (ImageView) this.findViewById(R.id.user_photo);
            userSecondPhoto = (ImageView) this.findViewById(R.id.user_photo_2);
            userThirdPhoto = (ImageView) this.findViewById(R.id.user_photo_3);
            usercover = (ImageView) this.findViewById(R.id.user_cover);
            usercoverSwitcher = (ImageView) this.findViewById(R.id.user_cover_switcher);
        }
        else
            customDrawerHeader = (LinearLayout) this.findViewById(R.id.drawer_header);
        sections = (LinearLayout) this.findViewById(R.id.sections);
        bottomSections = (LinearLayout) this.findViewById(R.id.bottom_sections);

        // init lists
        sectionList = new LinkedList<>();
        bottomSectionList = new LinkedList<>();
        accountManager = new LinkedList<>();

        // init listeners
        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
            userphoto.setOnClickListener(currentAccountListener);
            usercover.setOnClickListener(currentAccountListener);
            userSecondPhoto.setOnClickListener(secondAccountListener);
            userThirdPhoto.setOnClickListener(thirdAccountListener);
        }


        //get resources and density
        resources = this.getResources();
        density = resources.getDisplayMetrics().density;

        // get primary color
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        primaryColor = typedValue.data;
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        primaryDarkColor = typedValue.data;

        // if device is kitkat
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            TypedArray windowTraslucentAttribute = theme.obtainStyledAttributes(new int[]{android.R.attr.windowTranslucentStatus});
            kitkatTraslucentStatusbar = windowTraslucentAttribute.getBoolean(0, false);
            if(kitkatTraslucentStatusbar) {
                Window window = this.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                RelativeLayout.LayoutParams statusParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.traslucentStatusMargin));
                statusBar.setLayoutParams(statusParams);
                statusBar.setImageDrawable(new ColorDrawable(darkenColor(primaryColor)));

                if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
                    RelativeLayout.LayoutParams photoParams = (RelativeLayout.LayoutParams) userphoto.getLayoutParams();
                    photoParams.setMargins((int) (16 * density), resources.getDimensionPixelSize(R.dimen.traslucentPhotoMarginTop), 0, 0);
                    userphoto.setLayoutParams(photoParams);
                }
            }
        }

        // INIT ACTION BAR
        this.setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // DEVELOPER CALL TO INIT
        init(savedInstanceState);

        if(sectionList.size() == 0) {
            throw new RuntimeException("You must add at least one Section to top list.");
        }

        Configuration configuration = resources.getConfiguration();
        if(deviceSupportMultiPane()) {
            // se il multipane e' attivo, si e' in landscape e si e' un tablet allora si passa in multipane mode
            layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN,drawer);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins((int) (320 * density),0,0,0);
            content.setLayoutParams(params);
            layout.setScrimColor(Color.TRANSPARENT);
            layout.openDrawer(drawer);
            layout.requestDisallowInterceptTouchEvent(true);
        }
        else {
            // se non si sta lavorando in multiPane allora si inserisce il pulsante per aprire/chiudere

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            pulsante = new ActionBarDrawerToggle(this,layout,toolbar,R.string.nothing,R.string.nothing) {

                public void onDrawerClosed(View view) {
                    invalidateOptionsMenu(); // termina il comando
                    drawerTouchLocked = false;
                    setSectionsTouch(!drawerTouchLocked);

                    if(drawerListener != null)
                        drawerListener.onDrawerClosed(view);
                }

                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu(); // termina il comando

                    if(drawerListener != null)
                        drawerListener.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                    // if user wants the sliding arrow it compare
                    if(slidingDrawerEffect)
                        super.onDrawerSlide(drawerView, slideOffset);
                    else
                        super.onDrawerSlide(drawerView,0);

                    if(drawerListener != null)
                        drawerListener.onDrawerSlide(drawerView,slideOffset);
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);

                    if(drawerListener != null)
                        drawerListener.onDrawerStateChanged(newState);
                }
            };

            layout.setDrawerListener(pulsante);
        }

        // si attacca alla usercover un listener
        ViewTreeObserver vto;
        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS)
            vto = usercover.getViewTreeObserver();
        else
            vto = customDrawerHeader.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // quando l'immagine e' stata caricata

                // change user space to 16:9
                int width = drawer.getWidth();

                int heightCover;
                switch(drawerHeaderType) {
                    default:
                    case DRAWERHEADER_ACCOUNTS:
                    case DRAWERHEADER_IMAGE:
                    case DRAWERHEADER_CUSTOM:
                        // si fa il rapporto in 16 : 9
                        heightCover = (9 * width) / 16;
                        break;
                    case DRAWERHEADER_NO_HEADER:
                        heightCover = (int) (25 * density);
                        break;
                }


                // si toglie l'altezza in eccesso nelle versioni precenti a kitkat
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !kitkatTraslucentStatusbar)) {
                    heightCover -= (density * 25);
                }

                // set user space
                if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
                    usercover.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                    usercoverSwitcher.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                }
                else {
                    customDrawerHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                }

                //  heightCover (DRAWER HEADER) + 8 (PADDING) + sections + 8 (PADDING) + 1 (DIVISOR) + bottomSections
                int heightDrawer = (int) (( ( 8 + 8 + 1) * density ) + heightCover + sections.getHeight() + ((density * 48) * bottomSectionList.size()));

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !kitkatTraslucentStatusbar)) {
                    heightDrawer += (density * 25);
                }

                // si aggiungono le bottom sections
                if(heightDrawer >= getHeight()) {
                    addDivisor();
                    for (MaterialSection section : bottomSectionList) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                        sections.addView(section.getView(), params);
                    }
                }
                else
                    for (MaterialSection section : bottomSectionList) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                        bottomSections.addView(section.getView(), params);
                    }

                ViewTreeObserver obs;
                if(drawerHeaderType == DRAWERHEADER_ACCOUNTS)
                    obs = usercover.getViewTreeObserver();
                else
                    obs = customDrawerHeader.getViewTreeObserver();

                // si rimuove il listener
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });

        MaterialSection section;
        if (savedInstanceState == null) {

            // init account views
            if(accountManager.size() > 0) {
                currentAccount = accountManager.get(0);
                notifyAccountDataChanged();
            }

            // init section
            section = sectionList.get(0);
        }
        else {
            ArrayList<Integer> accountNumbers = savedInstanceState.getIntegerArrayList(STATE_ACCOUNT);

            // ripristina gli account
            for(int i = 0; i< accountNumbers.size(); i++) {
                MaterialAccount account = accountManager.get(i);
                account.setAccountNumber(accountNumbers.get(i));
                if(account.getAccountNumber() == MaterialAccount.FIRST_ACCOUNT)
                    currentAccount = account;
            }
            notifyAccountDataChanged();

            int accountSelected = savedInstanceState.getInt(STATE_SECTION);
            if(accountSelected >= BOTTOM_SECTION_START) {
                section = bottomSectionList.get(accountSelected-BOTTOM_SECTION_START);
            }
            else
                section = sectionList.get(accountSelected);

            if(section.getTarget() != MaterialSection.TARGET_FRAGMENT) {
                section = sectionList.get(0);
            }

            changeToolbarColor(section);
        }
        title = section.getTitle();
        currentSection = section;
        section.select();
        setFragment((Fragment) section.getTargetFragment(), section.getTitle(), null);

        // learning pattern
        if(learningPattern) {
            layout.openDrawer(drawer);
            disableLearningPattern();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!deviceSupportMultiPane())
            layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, drawer);
        else
            layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, drawer);
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
        if(pulsante != null)
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
        if(pulsante != null )
            pulsante.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {// al cambio di orientamento dello schermo
        super.onConfigurationChanged(newConfig);

        // Passa tutte le configurazioni al drawer
        if(pulsante != null ) {
            pulsante.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        this.getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        switch(backPattern) {
            default:
            case BACKPATTERN_BACK_ANYWHERE:
                super.onBackPressed();
                break;
            case BACKPATTERN_BACK_TO_FIRST:
                MaterialSection section = sectionList.get(0);
                if(currentSection == section)
                    super.onBackPressed();
                else {
                    onClick(section);
                }
                break;
            case BACKPATTERN_CUSTOM:
                MaterialSection backedSection = backToSection(getCurrentSection());

                if(currentSection == backedSection)
                    super.onBackPressed();
                else {
                    if(backedSection.getTarget() != MaterialSection.TARGET_FRAGMENT) {
                        throw new RuntimeException("The restored section must have a fragment as target");
                    }
                    onClick(backedSection);
                }

                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int position = this.getCurrentSection().getPosition();
        outState.putInt(STATE_SECTION,position);

        ArrayList<Integer> list = new ArrayList<>();
        for(MaterialAccount account : accountManager)
            list.add(account.getAccountNumber());

        outState.putIntegerArrayList(STATE_ACCOUNT,list);

        super.onSaveInstanceState(outState);
    }

    /**
     * Method used with BACKPATTERN_CUSTOM to retrieve the section which is restored
     * @param currentSection
     * @return the Section to restore that has Fragment as target (or currentSection for exit from activity)
     */
    protected MaterialSection backToSection(MaterialSection currentSection) {
        return currentSection;
    }

    private void setFragment(Fragment fragment,String title,Fragment oldFragment) {
        // si setta il titolo
        setTitle(title);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // before honeycomb there is not android.app.Fragment
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(oldFragment != null && oldFragment != fragment)
                ft.remove((android.support.v4.app.Fragment) oldFragment);

            ft.replace(R.id.frame_container, (android.support.v4.app.Fragment) fragment).commit();
        }
        else if (fragment instanceof android.app.Fragment) {
            if (oldFragment instanceof android.support.v4.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");


            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (oldFragment != null && fragment != oldFragment)
                ft.remove((android.app.Fragment) oldFragment);

            ft.replace(R.id.frame_container, (android.app.Fragment) fragment).commit();
        }
        else if(fragment instanceof android.support.v4.app.Fragment) {
            if(oldFragment instanceof android.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(oldFragment != null && oldFragment != fragment)
                ft.remove((android.support.v4.app.Fragment) oldFragment);

            ft.replace(R.id.frame_container, (android.support.v4.app.Fragment) fragment).commit();
        }
        else
            throw new RuntimeException("Fragment must be android.app.Fragment or android.support.v4.app.Fragment");

        // si chiude il drawer
        if(!deviceSupportMultiPane())
            layout.closeDrawer(drawer);
    }

    private MaterialAccount findAccountNumber(int number) {
        for(MaterialAccount account : accountManager)
            if(account.getAccountNumber() == number)
                return account;


        return null;
    }

    private void switchAccounts(final MaterialAccount newAccount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final ImageView floatingImage = new ImageView(this);

            // si calcolano i rettangoli di inizio e fine
            Rect startingRect = new Rect();
            Rect finalRect = new Rect();
            Point offsetHover = new Point();

            // 64dp primary user image / 40dp other user image = 1.6 scale
            float finalScale = 1.6f;

            final int statusBarHeight;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !kitkatTraslucentStatusbar)) {
                statusBarHeight = (int) (25 * density);
            } else {
                statusBarHeight = 0;
            }

            // si tiene traccia della foto cliccata
            ImageView photoClicked;
            if (newAccount.getAccountNumber() == MaterialAccount.SECOND_ACCOUNT) {
                photoClicked = userSecondPhoto;
            } else {
                photoClicked = userThirdPhoto;
            }
            photoClicked.getGlobalVisibleRect(startingRect, offsetHover);
            floatingImage.setImageDrawable(photoClicked.getDrawable());

            // si aggiunge una view nell'esatta posizione dell'altra
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(photoClicked.getWidth(), photoClicked.getHeight());
            params.setMargins(offsetHover.x, offsetHover.y - statusBarHeight, 0, 0);
            drawer.addView(floatingImage, params);

            // si setta la nuova foto di profilo sopra alla vecchia
            photoClicked.setImageBitmap(currentAccount.getCircularPhoto());

            // si setta la nuova immagine di background da visualizzare sotto la vecchia
            usercoverSwitcher.setImageBitmap(newAccount.getBackground());

            userphoto.getGlobalVisibleRect(finalRect);

            // Si calcola l'offset finale (LARGHEZZA DEL CONTAINER GRANDE - LARGHEZZA DEL CONTAINER PICCOLO / 2) e lo si applica
            int offset = (((finalRect.bottom - finalRect.top) - (startingRect.bottom - finalRect.top)) / 2);
            finalRect.offset(offset, offset - statusBarHeight);
            startingRect.offset(0, -statusBarHeight);

            // si animano le viste
            AnimatorSet set = new AnimatorSet();
            set
                    // si ingrandisce l'immagine e la si sposta a sinistra.
                    .play(ObjectAnimator.ofFloat(floatingImage, View.X, startingRect.left, finalRect.left))
                    .with(ObjectAnimator.ofFloat(floatingImage, View.Y, startingRect.top, finalRect.top))
                    .with(ObjectAnimator.ofFloat(floatingImage, View.SCALE_X, 1f, finalScale))
                    .with(ObjectAnimator.ofFloat(floatingImage, View.SCALE_Y, 1f, finalScale))
                    .with(ObjectAnimator.ofFloat(userphoto, View.ALPHA, 1f, 0f))
                    .with(ObjectAnimator.ofFloat(usercover, View.ALPHA, 1f, 0f))
                    .with(ObjectAnimator.ofFloat(photoClicked, View.SCALE_X, 0f, 1f))
                    .with(ObjectAnimator.ofFloat(photoClicked, View.SCALE_Y, 0f, 1f));
            set.setDuration(USER_CHANGE_TRANSITION);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {

                @SuppressLint("NewApi")
                @Override
                public void onAnimationEnd(Animator animation) {

                    // si carica la nuova immagine
                    ((View) userphoto).setAlpha(1);
                    setFirstAccountPhoto(newAccount.getCircularPhoto());

                    // si cancella l'imageview per l'effetto
                    drawer.removeView(floatingImage);

                    // si cambiano i dati utente
                    setUserEmail(newAccount.getSubTitle());
                    setUsername(newAccount.getTitle());

                    // si cambia l'immagine soprastante
                    setDrawerBackground(newAccount.getBackground());
                    // si fa tornare il contenuto della cover visibile (ma l'utente non nota nulla)
                    ((View) usercover).setAlpha(1);

                    // switch numbers
                    currentAccount.setAccountNumber(newAccount.getAccountNumber());
                    newAccount.setAccountNumber(MaterialAccount.FIRST_ACCOUNT);

                    // change pointer to newAccount
                    currentAccount = newAccount;

                    // si chiude il drawer
                    if (!deviceSupportMultiPane())
                        layout.closeDrawer(drawer);

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // se si annulla l'animazione si conclude e basta.
                    onAnimationEnd(animation);
                }
            });

            set.start();
        }
        else {
            // for minor version no animation is used.
            // switch numbers
            currentAccount.setAccountNumber(newAccount.getAccountNumber());
            newAccount.setAccountNumber(MaterialAccount.FIRST_ACCOUNT);
            // change pointer to newAccount
            currentAccount = newAccount;
            // refresh views
            notifyAccountDataChanged();

            if (!deviceSupportMultiPane())
                layout.closeDrawer(drawer);
        }
    }

    private int getHeight() {
        int height = 0;
        Display display = getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            height = display.getHeight();  // deprecated
        }
        return height;
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

    private boolean deviceSupportMultiPane() {
        if(multiPaneSupport && resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && resources.getConfiguration().smallestScreenWidthDp >= 600)
            return true;
        else
            return false;
    }

    private void setSectionsTouch(boolean isTouchable) {
        for(MaterialSection section : sectionList) {
            section.setTouchable(isTouchable);
        }
        for(MaterialSection section : bottomSectionList) {
            section.setTouchable(isTouchable);
        }
    }

    protected int darkenColor(int color) {
        if(color == primaryColor)
            return primaryDarkColor;

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        return Color.HSVToColor(hsv);
    }

    @Override
    public void onClick(MaterialSection section) {

        switch (section.getTarget()) {
            case MaterialSection.TARGET_FRAGMENT:
                setFragment((Fragment) section.getTargetFragment(), section.getTitle(), (Fragment) currentSection.getTargetFragment());

                changeToolbarColor(section);
                break;
            case MaterialSection.TARGET_ACTIVITY:
                this.startActivity(section.getTargetIntent());
                if (!deviceSupportMultiPane())
                    layout.closeDrawer(drawer);
                break;
            // TARGET_LISTENER viene gestito internamente
            case MaterialSection.TARGET_LISTENER:
                if (!deviceSupportMultiPane())
                    layout.closeDrawer(drawer);
            default:
                break;
        }
        currentSection = section;

        int position = section.getPosition();

        for (MaterialSection mySection : sectionList) {
            if (position != mySection.getPosition())
                mySection.unSelect();
        }
        for (MaterialSection mySection : bottomSectionList) {
            if (position != mySection.getPosition())
                mySection.unSelect();
        }

        if(!deviceSupportMultiPane()) {
            drawerTouchLocked = true;
            setSectionsTouch(!drawerTouchLocked);
        }
    }

    // method used for change supports

    public void setAccountListener(MaterialAccountListener listener) {
        this.accountListener = listener;
    }

    public void setDrawerListener(DrawerLayout.DrawerListener listener) {
        this.drawerListener = listener;
    }

    public void addMultiPaneSupport() {
        this.multiPaneSupport = true;
    }

    public void allowArrowAnimation() {
        slidingDrawerEffect = true;
    }

    public void disableLearningPattern() {
        learningPattern = false;
    }

    public void changeToolbarColor(MaterialSection section) {
        if (section.hasSectionColor()) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                if (!section.hasSectionColorDark())
                    this.statusBar.setImageDrawable(new ColorDrawable(darkenColor(section.getSectionColor())));
                else
                    this.statusBar.setImageDrawable(new ColorDrawable(section.getSectionColorDark()));
            } else
                this.statusBar.setImageDrawable(new ColorDrawable(section.getSectionColor()));
            this.getToolbar().setBackgroundColor(section.getSectionColor());
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                this.statusBar.setImageDrawable(new ColorDrawable(darkenColor(primaryColor)));
            else
                this.statusBar.setImageDrawable(new ColorDrawable(primaryColor));
            this.getToolbar().setBackgroundColor(primaryColor);
        }
    }

    public void changeToolbarColor(int primaryColor, int primaryDarkColor) {
        if(statusBar != null)
            this.statusBar.setImageDrawable(new ColorDrawable(primaryDarkColor));
        if(getToolbar() != null)
            this.getToolbar().setBackgroundColor(primaryColor);
    }

    public void setBackPattern(int backPattern) {
        this.backPattern = backPattern;
    }

    public void setDrawerHeaderCustom(View view) {
        if(drawerHeaderType != DRAWERHEADER_CUSTOM)
            throw new RuntimeException("Your header is not setted to Custom, check in your styles.xml");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        customDrawerHeader.addView(view,params);

    }

    public void setDrawerHeaderImage(Bitmap background) {
        if(drawerHeaderType != DRAWERHEADER_IMAGE)
            throw new RuntimeException("Your header is not setted to Image, check in your styles.xml");

        ImageView image = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageBitmap(background);

        customDrawerHeader.addView(image,params);
    }

    public void setDrawerHeaderImage(Drawable background) {
        if(drawerHeaderType != DRAWERHEADER_IMAGE)
            throw new RuntimeException("Your header is not setted to Image, check in your styles.xml");

        ImageView image = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageDrawable(background);

        customDrawerHeader.addView(image, params);
    }

    // Method used for customize layout

    public void addSection(MaterialSection section) {
        section.setPosition(sectionList.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48 * density));
        sectionList.add(section);
        sections.addView(section.getView(),params);
    }

    public void addBottomSection(MaterialSection section) {
        section.setPosition(BOTTOM_SECTION_START + bottomSectionList.size());
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48 * density));
        bottomSectionList.add(section);
        //bottomSections.addView(section.getView(),params);
    }

    public void addDivisor() {
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        // height 1 px
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));

        sections.addView(view, params);
    }

    public void addSubheader(CharSequence title) {
        View subheader = LayoutInflater.from(this).inflate(R.layout.layout_material_subheader,sections,false);
        TextView subheaderTitle = (TextView) subheader.findViewById(R.id.subheader_text);
        subheaderTitle.setText(title);

        // add custom separator on top of subheader
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(0,(int) (8 * density), 0 , 0);

        sections.addView(view,params);
        sections.addView(subheader);
    }

    public void addAccount(MaterialAccount account) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        if (accountManager.size() == 3)
            throw new RuntimeException("Currently are supported max 3 accounts");

        account.setAccountNumber(accountManager.size());
        accountManager.add(account);
    }

    /**
     * Reload Application data from Account Information
     */
    public void notifyAccountDataChanged() {
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

    // create sections

    public MaterialSection newSection(String title, Drawable icon, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,true,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, int icon,Fragment target) {
        return newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSection(String title, int icon,Intent target) {
        return newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSection(String title, int icon,MaterialSectionListener target) {
        return  newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSection(String title,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,false,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,false,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title,MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,false,rippleSupport,MaterialSection.TARGET_LISTENER);
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

    public  MaterialAccount getAccountAtCurrentPosition(int position) {

        if (position < 0 || position >= accountManager.size())
            throw  new RuntimeException("Account Index Overflow");

        return findAccountNumber(position);
    }
}