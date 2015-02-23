package it.neokree.materialnavigationdrawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.MaterialSubheader;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;
import it.neokree.materialnavigationdrawer.util.MaterialActionBarDrawerToggle;
import it.neokree.materialnavigationdrawer.util.MaterialDrawerLayout;
import it.neokree.materialnavigationdrawer.util.TypefaceManager;
import it.neokree.materialnavigationdrawer.util.Utils;

/**
 * Activity that extends ActionBarActivity with a Navigation Drawer with Material Design style
 *
 * @author created by neokree
 */
@SuppressWarnings("unused")
@SuppressLint("InflateParams")
public abstract class MaterialNavigationDrawer<Fragment> extends ActionBarActivity implements MaterialSectionListener,MaterialAccount.OnAccountDataLoaded {

    public static final int BOTTOM_SECTION_START = 10000;
    private static final int USER_CHANGE_TRANSITION = 500;

    public static final int BACKPATTERN_BACK_ANYWHERE = 0;
    public static final int BACKPATTERN_BACK_TO_FIRST = 1;
    public static final int BACKPATTERN_CUSTOM = 2;

    private static final int DRAWERHEADER_ACCOUNTS = 0;
    private static final int DRAWERHEADER_IMAGE = 1;
    private static final int DRAWERHEADER_CUSTOM = 2;
    private static final int DRAWERHEADER_NO_HEADER = 3;

    private static final int ELEMENT_TYPE_SECTION = 0;
    private static final int ELEMENT_TYPE_DIVISOR = 1;
    private static final int ELEMENT_TYPE_SUBHEADER = 2;
    private static final int ELEMENT_TYPE_BOTTOM_SECTION = 3;

    private static final String STATE_SECTION = "section";
    private static final String STATE_ACCOUNT = "account";

    private MaterialDrawerLayout layout;
    private ActionBar actionBar;
    private MaterialActionBarDrawerToggle pulsante;
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
    private ImageButton userButtonSwitcher;
    private LinearLayout customDrawerHeader;
    private LinearLayout sections;
    private LinearLayout bottomSections;

    // Lists
    private List<MaterialSection> sectionList;
    private List<MaterialSection> bottomSectionList;
    private List<MaterialAccount> accountManager;
    private List<MaterialSection> accountSectionList;
    private List<MaterialSubheader> subheaderList;
    private List<Integer> elementsList;
    private List<Fragment> childFragmentStack;
    private List<String> childTitleStack;

    // current pointers
    private MaterialSection currentSection;
    private MaterialAccount currentAccount;

    private CharSequence title;
    private float density;
    private int primaryColor;
    private int primaryDarkColor;
    private int drawerColor;
    private boolean drawerTouchLocked = false;
    private boolean slidingDrawerEffect = false;
    private boolean multiPaneSupport;
    private boolean rippleSupport;
    private boolean uniqueToolbarColor;
    private boolean singleAccount;
    private boolean accountSwitcher = false;
    private boolean isCurrentFragmentChild = false;
    private boolean kitkatTraslucentStatusbar = false;
    private static boolean learningPattern = true;
    private int backPattern = BACKPATTERN_BACK_ANYWHERE;
    private int drawerHeaderType;

    // fragment request


    // resources
    private Resources resources;
    private TypefaceManager fontManager;

    // listeners
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
                    if(accountSwitcherListener != null && !singleAccount)
                        accountSwitcherListener.onClick(null);
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
                    if(accountSwitcherListener != null && !singleAccount)
                        accountSwitcherListener.onClick(null);
                }
            }
        }
    };
    private View.OnClickListener accountSwitcherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!drawerTouchLocked) {

                // si rimuovono le viste  || Views are removed
                sections.removeAllViews();
                bottomSections.removeAllViews();

                if (!accountSwitcher) {
                    // si cambia l'icona del pulsante || Change the icon of the button
                    userButtonSwitcher.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);

                    for (MaterialAccount account : accountManager) {
                        // si inseriscono tutti gli account ma non quello attualmente in uso || Add all account without the current one
                        if(account.getAccountNumber() != MaterialAccount.FIRST_ACCOUNT) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (56 * density));
                            sections.addView(account.getSectionView(MaterialNavigationDrawer.this, fontManager.getRobotoMedium(),accountSectionListener,rippleSupport,account.getAccountNumber()),params);
                        }
                    }
                    for (MaterialSection section : accountSectionList) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                        sections.addView(section.getView(), params);
                    }

                    // si attiva l'account switcher per annotare che si visualizzano gli account. || accountSwitcher is enabled for checking the account list is showed.
                    accountSwitcher = true;
                } else {
                    userButtonSwitcher.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);

                    int indexSection = 0 ,indexSubheader = 0;
                    for(int type : elementsList) {
                        switch(type) {
                            case ELEMENT_TYPE_SECTION:
                                MaterialSection section = sectionList.get(indexSection);
                                indexSection++;
                                LinearLayout.LayoutParams paramSection = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                                sections.addView(section.getView(), paramSection);
                                break;
                            case ELEMENT_TYPE_DIVISOR:
                                View view = new View(MaterialNavigationDrawer.this);
                                view.setBackgroundColor(Color.parseColor("#e0e0e0"));
                                // height 1 px
                                LinearLayout.LayoutParams paramDivisor = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                                paramDivisor.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));
                                sections.addView(view,paramDivisor);
                                break;
                            case ELEMENT_TYPE_SUBHEADER:
                                MaterialSubheader subheader = subheaderList.get(indexSubheader);
                                indexSubheader++;
                                sections.addView(subheader.getView());
                                break;
                            case ELEMENT_TYPE_BOTTOM_SECTION:
                                break; // le bottom section vengono gestite dopo l'inserimento degli altri elementi
                        }
                    }

                    int width = drawer.getWidth();
                    int heightCover = 0;
                    switch(drawerHeaderType) {
                        default:
                        case DRAWERHEADER_ACCOUNTS:
                        case DRAWERHEADER_IMAGE:
                        case DRAWERHEADER_CUSTOM:
                            // si fa il rapporto in 16 : 9 || 16:9 rate
                            heightCover = (9 * width) / 16;
                            break;
                        case DRAWERHEADER_NO_HEADER:
                            break;
                    }

                    // adding status bar height
                    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                        heightCover += (int) (24 * density); // on lollipop status bar is only 24 dp height
                    }
                    else {
                        heightCover += (int) (25 * density);
                    }

                    int heightDrawer = (int) (( ( 8 + 8 ) * density ) + 1 + heightCover + sections.getHeight() + ((density * 48) * bottomSectionList.size()) +  (subheaderList.size() * (35 * density)));

                    View divisor = new View(MaterialNavigationDrawer.this);
                    divisor.setBackgroundColor(Color.parseColor("#e0e0e0"));

                    // si aggiungono le bottom sections
                    if (heightDrawer >= Utils.getScreenHeight(MaterialNavigationDrawer.this)) {

                        LinearLayout.LayoutParams paramDivisor = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                        paramDivisor.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));
                        sections.addView(divisor,paramDivisor);

                        for (MaterialSection section : bottomSectionList) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                            sections.addView(section.getView(), params);
                        }
                    } else {
                        LinearLayout.LayoutParams paramDivisor = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                        bottomSections.addView(divisor,paramDivisor);

                        for (MaterialSection section : bottomSectionList) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                            bottomSections.addView(section.getView(), params);
                        }
                    }

                    accountSwitcher = false;
                }

            }
        }
    };
    private MaterialSectionListener accountSectionListener = new MaterialSectionListener() {
        @Override
        public void onClick(MaterialSection section) {
            section.unSelect(); // remove the selected color

            if(!drawerTouchLocked) {
                int accountPosition = section.getPosition();
                MaterialAccount account = findAccountNumber(accountPosition);

                // switch accounts position
                currentAccount.setAccountNumber(accountPosition);
                account.setAccountNumber(MaterialAccount.FIRST_ACCOUNT);
                currentAccount = account;

                notifyAccountDataChanged();

                // call change account method
                if (accountListener != null)
                    accountListener.onChangeAccount(account);

                // change account list
                accountSwitcherListener.onClick(null);

                // close drawer
                if (!deviceSupportMultiPane())
                    layout.closeDrawer(drawer);
            }
        }
    };
    private View.OnClickListener toolbarToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isCurrentFragmentChild) {
                onHomeAsUpSelected();
                onBackPressed();
            }
        }
    };
    private MaterialAccountListener accountListener;
    private DrawerLayout.DrawerListener drawerListener;


    @SuppressWarnings("unchecked")
    @Override
    /**
     * Do not Override this method!!! <br>
     * Use init() instead
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources.Theme theme = this.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.drawerType,typedValue,true);
        drawerHeaderType = typedValue.data;
        theme.resolveAttribute(R.attr.rippleBackport,typedValue,false);
        rippleSupport = typedValue.data != 0;
        theme.resolveAttribute(R.attr.uniqueToolbarColor,typedValue,false);
        uniqueToolbarColor = typedValue.data != 0;
        theme.resolveAttribute(R.attr.singleAccount,typedValue,false);
        singleAccount = typedValue.data != 0;
        theme.resolveAttribute(R.attr.multipaneSupport,typedValue,false);
        multiPaneSupport = typedValue.data != 0;
        theme.resolveAttribute(R.attr.drawerColor,typedValue,true);
        drawerColor = typedValue.data;

        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS)
            super.setContentView(R.layout.activity_material_navigation_drawer);
        else
            super.setContentView(R.layout.activity_material_navigation_drawer_customheader);

        // init Typeface
        fontManager = new TypefaceManager(this.getAssets());

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
            userButtonSwitcher = (ImageButton) this.findViewById(R.id.user_switcher);

            // set Roboto Fonts
            username.setTypeface(fontManager.getRobotoMedium());
            usermail.setTypeface(fontManager.getRobotoRegular());

            // get and set username and mail text colors
            theme.resolveAttribute(R.attr.accountStyle,typedValue,true);
            TypedArray attributes = theme.obtainStyledAttributes(typedValue.resourceId,R.styleable.MaterialAccount);
            try {
                username.setTextColor(attributes.getColor(R.styleable.MaterialAccount_titleColor,0xFFF));
                usermail.setTextColor(attributes.getColor(R.styleable.MaterialAccount_subtitleColor,0xFFF));
            }
            finally {
                attributes.recycle();
            }


            // set the button image
            if(!singleAccount) {
                userButtonSwitcher.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                userButtonSwitcher.setOnClickListener(accountSwitcherListener);
            }

        }
        else
            customDrawerHeader = (LinearLayout) this.findViewById(R.id.drawer_header);
        sections = (LinearLayout) this.findViewById(R.id.sections);
        bottomSections = (LinearLayout) this.findViewById(R.id.bottom_sections);

        // init lists
        sectionList = new LinkedList<>();
        bottomSectionList = new LinkedList<>();
        accountManager = new LinkedList<>();
        accountSectionList = new LinkedList<>();
        subheaderList = new LinkedList<>();
        elementsList = new LinkedList<>();
        childFragmentStack = new LinkedList<>();
        childTitleStack = new LinkedList<>();

        // init listeners
        if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
            userphoto.setOnClickListener(currentAccountListener);
            if(singleAccount)

                usercover.setOnClickListener(currentAccountListener);
            else
                usercover.setOnClickListener(accountSwitcherListener);
            userSecondPhoto.setOnClickListener(secondAccountListener);
            userThirdPhoto.setOnClickListener(thirdAccountListener);
        }

        // set drawer backgrond color
        drawer.setBackgroundColor(drawerColor);

        //get resources and density
        resources = this.getResources();
        density = resources.getDisplayMetrics().density;

        // set the right drawer width
        DrawerLayout.LayoutParams drawerParams = (android.support.v4.widget.DrawerLayout.LayoutParams) drawer.getLayoutParams();
        drawerParams.width = Utils.getDrawerWidth(resources);
        drawer.setLayoutParams(drawerParams);

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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // INIT ACTION BAR
        this.setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        // DEVELOPER CALL TO INIT
        init(savedInstanceState);

        if(sectionList.size() == 0) {
            throw new RuntimeException("You must add at least one Section to top list.");
        }

        if(deviceSupportMultiPane()) {
            // se il multipane e' attivo, si e' in landscape e si e' un tablet allora si passa in multipane mode
            layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN,drawer);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins((int) (320 * density),0,0,0);
            content.setLayoutParams(params);
            layout.setScrimColor(Color.TRANSPARENT);
            layout.openDrawer(drawer);
            layout.setMultipaneSupport(true);
        }
        else {
            // se non si sta lavorando in multiPane allora si inserisce il pulsante per aprire/chiudere

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            pulsante = new MaterialActionBarDrawerToggle<Fragment>(this,layout,toolbar,R.string.nothing,R.string.nothing) {

                @Override
                public void onDrawerClosed(View view) {
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                    // abilita il touch del drawer
                    setDrawerTouchable(true);

                    if(drawerListener != null)
                        drawerListener.onDrawerClosed(view);

                    if(hasRequest()) {
                        MaterialSection section = getRequestedSection();
                        changeToolbarColor(section);
                        setFragment((Fragment) section.getTargetFragment(), section.getTitle(), (Fragment) currentSection.getTargetFragment());
                        afterFragmentSetted((Fragment) section.getTargetFragment(),section.getTitle());
                        this.removeRequest();
                    }
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                    if(drawerListener != null)
                        drawerListener.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                    if(!isCurrentFragmentChild) { // if user seeing a master fragment

                        // if user wants the sliding arrow it compare
                        if (slidingDrawerEffect)
                            super.onDrawerSlide(drawerView, slideOffset);
                        else
                            super.onDrawerSlide(drawerView, 0);
                    }
                    else {// if user seeing a child fragment always shows the back arrow
                        super.onDrawerSlide(drawerView,1f);
                    }

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
            pulsante.setToolbarNavigationClickListener(toolbarToggleListener);

            layout.setDrawerListener(pulsante);
            layout.setMultipaneSupport(false);
        }

        // si procede con gli altri elementi dopo la creazioen delle viste
        ViewTreeObserver vto = drawer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // quando l'immagine e' stata caricata

                // change user space to 16:9
                int width = drawer.getWidth();

                int heightCover = 0;
                switch(drawerHeaderType) {
                    default:
                    case DRAWERHEADER_ACCOUNTS:
                    case DRAWERHEADER_IMAGE:
                    case DRAWERHEADER_CUSTOM:
                        // si fa il rapporto in 16 : 9
                        heightCover = (9 * width) / 16;

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            heightCover += (int) (24 * density);

                        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && kitkatTraslucentStatusbar)
                            heightCover += (int) (25 * density);

                        break;
                    case DRAWERHEADER_NO_HEADER:
                        break;
                }

                // set user space
                if(drawerHeaderType == DRAWERHEADER_ACCOUNTS) {
                    usercover.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                    usercoverSwitcher.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                }
                else {
                    customDrawerHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, heightCover));
                }

                // adding status bar height for other version of android that not have traslucent status bar
                if((Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !kitkatTraslucentStatusbar) || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
                    heightCover += (int) (25 * density);
                }

                //  heightCover (DRAWER HEADER) + 8 (PADDING) + sections + 8 (PADDING) + 1 (DIVISOR) + bottomSections + subheaders
                int heightDrawer = (int) (( ( 8 + 8 ) * density ) + 1 + heightCover + sections.getHeight() + ((density * 48) * bottomSectionList.size()) +  (subheaderList.size() * (35 * density)));

                // create the divisor
                View divisor = new View(MaterialNavigationDrawer.this);
                divisor.setBackgroundColor(Color.parseColor("#e0e0e0"));

                // si aggiungono le bottom sections
                if(heightDrawer >= Utils.getScreenHeight(MaterialNavigationDrawer.this)) {

                    // add the divisor to the section view
                    LinearLayout.LayoutParams paramDivisor = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                    paramDivisor.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));
                    sections.addView(divisor,paramDivisor);


                    for (MaterialSection section : bottomSectionList) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                        sections.addView(section.getView(), params);
                    }
                }
                else {
                    // add the divisor to the bottom section listview
                    LinearLayout.LayoutParams paramDivisor = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
                    bottomSections.addView(divisor,paramDivisor);

                    for (MaterialSection section : bottomSectionList) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
                        bottomSections.addView(section.getView(), params);
                    }
                }


                ViewTreeObserver obs = drawer.getViewTreeObserver();

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
            if(section.getTarget() != MaterialSection.TARGET_FRAGMENT)
                throw new RuntimeException("The first section added must have a fragment as target");
        }
        else {

            ArrayList<Integer> accountNumbers = savedInstanceState.getIntegerArrayList(STATE_ACCOUNT);

            // ripristina gli account
            for(int i = 0; i< accountNumbers.size(); i++) {
                MaterialAccount account = accountManager.get(i);
                account.setAccountNumber(accountNumbers.get(i));
                if(account.getAccountNumber() == MaterialAccount.FIRST_ACCOUNT) {
                    currentAccount = account;
                }
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

        }
        title = section.getTitle();
        currentSection = section;
        section.select();
        setFragment((Fragment) section.getTargetFragment(), section.getTitle(), null,savedInstanceState != null);

        // change the toolbar color for the first section
        changeToolbarColor(section);

        // add the first section to the child stack
        childFragmentStack.add((Fragment) section.getTargetFragment());
        childTitleStack.add(section.getTitle());

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



    @Override
    public void setContentView(View view) {
        throw new RuntimeException("The library have it's own content, please move all content inside section's fragments");

        //super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        throw new RuntimeException("The library have it's own content, please move all content inside section's fragments");

        //super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new RuntimeException("The library have it's own content, please move all content inside section's fragments");

        //super.setContentView(view, params);
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

        if(layout.isDrawerOpen(drawer)) {
            menu.clear();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se dal drawer si seleziona un oggetto
        if(!deviceSupportMultiPane()) {
            if (pulsante.onOptionsItemSelected(item)) {
                return true;
            }
        }
        else {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    toolbarToggleListener.onClick(null);
                    return true;
            }

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
        if(!isCurrentFragmentChild) {
            switch (backPattern) {
                default:
                case BACKPATTERN_BACK_ANYWHERE:
                    super.onBackPressed();
                    break;
                case BACKPATTERN_BACK_TO_FIRST:
                    MaterialSection section = sectionList.get(0);
                    if (currentSection == section)
                        super.onBackPressed();
                    else {
                        section.select();
                        //onClick(section);

                        changeToolbarColor(section);
                        setFragment((Fragment) section.getTargetFragment(), section.getTitle(), (Fragment) currentSection.getTargetFragment());
                        afterFragmentSetted((Fragment) section.getTargetFragment(),section.getTitle());
                        syncSectionsState(section);
                    }
                    break;
                case BACKPATTERN_CUSTOM:
                    MaterialSection backedSection = backToSection(getCurrentSection());

                    if (currentSection == backedSection)
                        super.onBackPressed();
                    else {
                        if (backedSection.getTarget() != MaterialSection.TARGET_FRAGMENT) {
                            throw new RuntimeException("The restored section must have a fragment as target");
                        }
                        backedSection.select();
                        //onClick(backedSection);

                        setFragment((Fragment) backedSection.getTargetFragment(), backedSection.getTitle(), (Fragment) currentSection.getTargetFragment());
                        afterFragmentSetted((Fragment) backedSection.getTargetFragment(),backedSection.getTitle());
                        syncSectionsState(backedSection);
                    }
                    break;
            }
        }
        else {
            if(childFragmentStack.size() <= 1) {
                isCurrentFragmentChild = false;
                onBackPressed();
            }
            else {
                // reload the child before
                Fragment newFragment = childFragmentStack.get(childFragmentStack.size() - 2);
                String newTitle = childTitleStack.get(childTitleStack.size() - 2);

                // get and remove the last child
                Fragment currentFragment = childFragmentStack.remove(childFragmentStack.size() - 1);
                childTitleStack.remove(childTitleStack.size() - 1);


                setFragment(newFragment,newTitle,currentFragment);

                if(childFragmentStack.size() == 1) {
                    // user comed back to master section
                    isCurrentFragmentChild = false;

                    if(!deviceSupportMultiPane())
                        pulsante.setDrawerIndicatorEnabled(true);
                    else
                        actionBar.setDisplayHomeAsUpEnabled(false);
                }
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // recycle bitmaps
        recycleAccounts();
    }

    /**
     * Method used with BACKPATTERN_CUSTOM to retrieve the section which is restored
     * @param currentSection the section used at this time
     * @return the Section to restore that has Fragment as target (or currentSection for exit from activity)
     */
    protected MaterialSection backToSection(MaterialSection currentSection) {
        return currentSection;
    }

    /**
     * Set the section informations.<br />
     * In short:
     * <ul>
     *     <li>set the section title into the toolbar</li>
     *     <li>set the section color to the toolbar</li>
     *     <li>open/call the target</li>
     * </ul>
     *
     * This method is equal to a user tap on a drawer section.
     * @param section the section which is replaced
     */
    public void setSection(MaterialSection section) {
        this.onClick(section);

        setDrawerTouchable(true);
    }

    /**
     * Set the fragment to the activity content.<br />
     * N.B. If you want to support the master/child flow, please consider to use setFragmentChild instead
     *
     * @param fragment to replace into the main content
     * @param title to set into Toolbar
     */
    public void setFragment(Fragment fragment,String title) {
        setFragment(fragment,title,null);

        if(!isCurrentFragmentChild) {// remove the last child from the stack
            childFragmentStack.remove(childFragmentStack.size() - 1);
            childTitleStack.remove(childTitleStack.size() - 1);
        }
        else for(int i = childFragmentStack.size()-1;i >= 0;i++) { // if a section is clicked when user is into a child remove all childs from stack
            childFragmentStack.remove(i);
            childTitleStack.remove(i);
        }

        // add to the childStack the Fragment and title
        childFragmentStack.add(fragment);
        childTitleStack.add(title);

        isCurrentFragmentChild = false;
    }

    private void setFragment(Fragment fragment,String title, Fragment oldFragment) {
        setFragment(fragment,title,oldFragment,false);
    }

    private void setFragment(Fragment fragment,String title,Fragment oldFragment,boolean hasSavedInstanceState) {
        // si setta il titolo
        setTitle(title);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // before honeycomb there is not android.app.Fragment

            if(!hasSavedInstanceState) {
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (oldFragment != null && oldFragment != fragment)
                    ft.remove((android.support.v4.app.Fragment) oldFragment);

                ft.replace(R.id.frame_container, (android.support.v4.app.Fragment) fragment).commit();
            }
        }
        else if (fragment instanceof android.app.Fragment) {
            if (oldFragment instanceof android.support.v4.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");

            if(!hasSavedInstanceState) {// se non e' avvenuta una rotazione

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (oldFragment != null && fragment != oldFragment)
                    ft.remove((android.app.Fragment) oldFragment);

                ft.replace(R.id.frame_container, (android.app.Fragment) fragment).commit();
            }
        }
        else if(fragment instanceof android.support.v4.app.Fragment) {
            if(oldFragment instanceof android.app.Fragment)
                throw new RuntimeException("You should use only one type of Fragment");

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(oldFragment != null && oldFragment != fragment)
                ft.remove((android.support.v4.app.Fragment) oldFragment);

            if(!hasSavedInstanceState) // se non e' avvenuta una rotazione
                ft.replace(R.id.frame_container, (android.support.v4.app.Fragment) fragment).commit();
        }
        else
            throw new RuntimeException("Fragment must be android.app.Fragment or android.support.v4.app.Fragment");

    }

    private void afterFragmentSetted(Fragment fragment,String title) {
        // remove the last child from the stack
        if(!isCurrentFragmentChild) {
            childFragmentStack.remove(childFragmentStack.size() - 1);
            childTitleStack.remove(childTitleStack.size() - 1);
        }
        else for(int i = childFragmentStack.size()-1;i >= 0;i--) { // if a section is clicked when user is into a child remove all childs from stack
            childFragmentStack.remove(i);
            childTitleStack.remove(i);
        }

        // add to the childStack the Fragment and title
        childFragmentStack.add(fragment);
        childTitleStack.add(title);
        isCurrentFragmentChild = false;
        if(!deviceSupportMultiPane())
            pulsante.setDrawerIndicatorEnabled(true);
        else {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void setFragmentChild(Fragment fragment,String title) {
        isCurrentFragmentChild = true;

        // replace the fragment
        setFragment(fragment,title,childFragmentStack.get(childFragmentStack.size() - 1));

        // add to the stack the child
        childFragmentStack.add(fragment);
        childTitleStack.add(title);

        // sync the toolbar toggle state
        if(!deviceSupportMultiPane())
            pulsante.setDrawerIndicatorEnabled(false);
        else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // private methods

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
            photoClicked.setImageDrawable(currentAccount.getCircularPhoto());

            // si setta la nuova immagine di background da visualizzare sotto la vecchia
            usercoverSwitcher.setImageDrawable(newAccount.getBackground());

            userphoto.getGlobalVisibleRect(finalRect);

            // Si calcola l'offset finale (LARGHEZZA DEL CONTAINER GRANDE - LARGHEZZA DEL CONTAINER PICCOLO / 2) e lo si applica
            int offset = (((finalRect.bottom - finalRect.top) - (startingRect.bottom - finalRect.top)) / 2);
            finalRect.offset(offset, offset - statusBarHeight);
            startingRect.offset(0, -statusBarHeight);


            // Se il dispositivo usa un linguaggio RTL si rimuove l'offset della parte a sinistra dello schermo
            if(Utils.isRTL()) {
                // si rimuove dal conteggio la parte a sinistra del drawer.
                int leftOffset = resources.getDisplayMetrics().widthPixels - Utils.getDrawerWidth(resources);

                startingRect.left -= leftOffset;
                finalRect.left -= leftOffset;
            }

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
                    setDrawerHeaderImage(newAccount.getBackground());
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

    private boolean deviceSupportMultiPane() {
        if(multiPaneSupport && resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && Utils.isTablet(resources))
            return true;
        else
            return false;
    }

    private void setDrawerTouchable(boolean isTouchable) {
        drawerTouchLocked = !isTouchable;

        for(MaterialSection section : sectionList) {
            section.setTouchable(isTouchable);
        }
        for(MaterialSection section : bottomSectionList) {
            section.setTouchable(isTouchable);
        }
    }

    private void recycleAccounts() {
        for(MaterialAccount account : accountManager) {
            account.recycle();
        }
    }

    private void syncSectionsState(MaterialSection section) {
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
                // se l'utente clicca sulla stessa schermata in cui si trova si chiude il drawer e basta
                if(section == currentSection) {
                    layout.closeDrawer(drawer);
                    return;
                }

                if(deviceSupportMultiPane()) {
                    changeToolbarColor(section);
                    setFragment((Fragment) section.getTargetFragment(), section.getTitle(), (Fragment) currentSection.getTargetFragment());
                    afterFragmentSetted((Fragment) section.getTargetFragment(),section.getTitle());
                }
                else {
                    // si disattiva il touch sul drawer
                    setDrawerTouchable(false);
                    // la chiamata al fragment viene spostata dopo la chiusura del drawer
                    pulsante.addRequest(section);

                    layout.closeDrawer(drawer);

                }
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

                if (!deviceSupportMultiPane()) {
                    setDrawerTouchable(false);
                }
            default:
                break;
        }

        // se il target e' un activity la sezione corrente rimane quella precedente
        if(section.getTarget() != MaterialSection.TARGET_ACTIVITY ) {
            syncSectionsState(section);
        }
    }

    @Override
    public void onUserPhotoLoaded(MaterialAccount account) {
        if(account.getAccountNumber() <= MaterialAccount.THIRD_ACCOUNT)
            notifyAccountDataChanged();
    }

    @Override
    public void onBackgroundLoaded(MaterialAccount account) {
        if(account.getAccountNumber() <= MaterialAccount.THIRD_ACCOUNT)
            notifyAccountDataChanged();
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

    /**
     * Set the HomeAsUpIndicator that is visible when user navigate to a fragment child
     *
     * N.B. call this method AFTER the init() to leave the time to instantiate the ActionBarDrawerToggle
     * @param resId the id to resource drawable to use as indicator
     */
    public void setHomeAsUpIndicator(int resId) {
        setHomeAsUpIndicator(resources.getDrawable(resId));
    }

    /**
     * Set the HomeAsUpIndicator that is visible when user navigate to a fragment child
     * @param indicator the resource drawable to use as indicator
     */
    public void setHomeAsUpIndicator(Drawable indicator) {
        if(!deviceSupportMultiPane()) {
            pulsante.setHomeAsUpIndicator(indicator);
        }
        else {
            actionBar.setHomeAsUpIndicator(indicator);
        }
    }

    public void changeToolbarColor(MaterialSection section) {

        int sectionPrimaryColor;
        int sectionPrimaryColorDark;

        if (section.hasSectionColor() && !uniqueToolbarColor) {
            if (!section.hasSectionColorDark())
                sectionPrimaryColorDark = darkenColor(section.getSectionColor());
            else
                sectionPrimaryColorDark = section.getSectionColorDark();

            sectionPrimaryColor = section.getSectionColor();
        } else {
            sectionPrimaryColorDark = primaryDarkColor;
            sectionPrimaryColor = primaryColor;
        }

        this.getToolbar().setBackgroundColor(sectionPrimaryColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            this.statusBar.setImageDrawable(new ColorDrawable(sectionPrimaryColorDark));
    }

    public void changeToolbarColor(int primaryColor, int primaryDarkColor) {
        if(statusBar != null)
            statusBar.setImageDrawable(new ColorDrawable(primaryDarkColor));

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
        switch(drawerHeaderType) {
            case DRAWERHEADER_ACCOUNTS:
                usercover.setImageBitmap(background);
                break;
            case DRAWERHEADER_IMAGE:
                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setImageBitmap(background);

                customDrawerHeader.addView(image,params);
                break;
            default:
                throw new RuntimeException("Your drawer configuration don't support a background image, check in your styles.xml");
        }

    }

    public void setDrawerHeaderImage(int backgroundId) {
        setDrawerHeaderImage(resources.getDrawable(backgroundId));
    }

    public void setDrawerHeaderImage(Drawable background) {
        switch(drawerHeaderType) {
            case DRAWERHEADER_IMAGE:
                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setImageDrawable(background);

                customDrawerHeader.addView(image, params);
                break;
            case DRAWERHEADER_ACCOUNTS:
                usercover.setImageDrawable(background);
                break;
            default:
                throw new RuntimeException("Your drawer configuration don't support a background image, check in your styles.xml");
        }
    }

    // Method used for customize layout

    public void setUserEmail(String email) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        usermail.setText(email);
    }

    public void setUserEmailTextColor(int color) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        usermail.setTextColor(color);
    }

    public void setUsername(String username) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        this.username.setText(username);
    }

    public void setUsernameTextColor(int color) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        this.username.setTextColor(color);
    }

    public void setFirstAccountPhoto(Drawable photo) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        userphoto.setImageDrawable(photo);
    }

    public void setSecondAccountPhoto(Drawable photo) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        userSecondPhoto.setImageDrawable(photo);
    }

    public void setThirdAccountPhoto(Drawable photo) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        userThirdPhoto.setImageDrawable(photo);
    }

    public void setDrawerBackgroundColor(int color) {
        drawer.setBackgroundColor(color);
    }

    public void addSection(MaterialSection section) {
        section.setPosition(sectionList.size());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(48 * density));
        section.setTypeface(fontManager.getRobotoMedium());
        sectionList.add(section);

        ViewParent parent = section.getView().getParent();
        sections.addView(section.getView(),params);

        // add the element to the list
        elementsList.add(ELEMENT_TYPE_SECTION);
    }

    public void addBottomSection(MaterialSection section) {
        section.setPosition(BOTTOM_SECTION_START + bottomSectionList.size());
        section.setTypeface(fontManager.getRobotoRegular());
        bottomSectionList.add(section);

        // add the element to the list
        elementsList.add(ELEMENT_TYPE_BOTTOM_SECTION);
    }

    public void addAccountSection(MaterialSection section) {
        section.setPosition(accountSectionList.size());
        section.setTypeface(fontManager.getRobotoMedium());
        accountSectionList.add(section);
    }

    public void addDivisor() {
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        // height 1 px
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));

        sections.addView(view, params);

        // add the element to the list
        elementsList.add(ELEMENT_TYPE_DIVISOR);
    }

    public void addSubheader(CharSequence title) {
        MaterialSubheader subheader = new MaterialSubheader(this);
        subheader.setTitle(title);
        subheader.setTitleFont(fontManager.getRobotoRegular());


        subheaderList.add(subheader);
        sections.addView(subheader.getView());

        // add the element to the list
        elementsList.add(ELEMENT_TYPE_SUBHEADER);
    }

    public void addAccount(MaterialAccount account) {
        if(DRAWERHEADER_ACCOUNTS != drawerHeaderType) {
            throw new RuntimeException("Your header is not setted to Accounts, check in your styles.xml");
        }

        account.setAccountListener(this);
        account.setAccountNumber(accountManager.size());
        accountManager.add(account);
    }

    public void removeAccount(MaterialAccount account) {
        int size = accountManager.size();
        // si rimuovono le viste gia inserite che stanno per essere cambiate
        if(size <= 3 && size > 0) {
            this.setThirdAccountPhoto(null);
            this.setSecondAccountPhoto(null);
            this.setFirstAccountPhoto(null);
        }

        // si ricalcolano gli indici degli account
        for(int i = 0; i < size; i++ ) {
            MaterialAccount a = accountManager.get(i);

            if(a.getAccountNumber() > account.getAccountNumber()) {
                a.setAccountNumber(a.getAccountNumber() - 1);
            }
        }

        // si rimuove dalla lista degli account e si esegue il recycle sulle sue view
        accountManager.remove(account);
        account.recycle();

        if(account.getAccountNumber() == 0)
            currentAccount = findAccountNumber(0);

    }

    /**
     * Reload Application data from Account Information
     */
    public void notifyAccountDataChanged() {
        switch(accountManager.size()) {
            default:
            case 3:
                this.setThirdAccountPhoto(findAccountNumber(MaterialAccount.THIRD_ACCOUNT).getCircularPhoto());
            case 2:
                this.setSecondAccountPhoto(findAccountNumber(MaterialAccount.SECOND_ACCOUNT).getCircularPhoto());
            case 1:
                this.setFirstAccountPhoto(currentAccount.getCircularPhoto());
                this.setDrawerHeaderImage(currentAccount.getBackground());
                this.setUsername(currentAccount.getTitle());
                this.setUserEmail(currentAccount.getSubTitle());
            case 0:
        }
    }

    public void openDrawer() {
        layout.openDrawer(drawer);
    }

    public void closeDrawer() {
        layout.closeDrawer(drawer);
    }

    public boolean isDrawerOpen() {
        return layout.isDrawerOpen(drawer);
    }

    // create sections

    public MaterialSection newSection(String title, Drawable icon, Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title,Drawable icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title, Drawable icon, Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Drawable icon, MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title, Drawable icon, MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title, Bitmap icon,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title, Bitmap icon,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, Bitmap icon,MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSectionWithRealColor(String title, Bitmap icon,MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_24DP,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.useRealColor();
        section.setIcon(icon);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    public MaterialSection newSection(String title, int icon,Fragment target) {
        return newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSectionWithRealColor(String title, int icon,Fragment target) {
        return newSectionWithRealColor(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSection(String title, int icon,Intent target) {
        return newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSectionWithRealColor(String title, int icon,Intent target) {
        return newSectionWithRealColor(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSection(String title, int icon,MaterialSectionListener target) {
        return  newSection(title,resources.getDrawable(icon),target);
    }

    public MaterialSection newSectionWithRealColor(String title, int icon,MaterialSectionListener target) {
        return  newSectionWithRealColor(title,resources.getDrawable(icon),target);
    }

    @SuppressWarnings("unchecked")
    public MaterialSection newSection(String title,Fragment target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_NO_ICON,rippleSupport,MaterialSection.TARGET_FRAGMENT);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    @SuppressWarnings("unchecked")
    public MaterialSection newSection(String title,Intent target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_NO_ICON,rippleSupport,MaterialSection.TARGET_ACTIVITY);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    @SuppressWarnings("unchecked")
    public MaterialSection newSection(String title,MaterialSectionListener target) {
        MaterialSection section = new MaterialSection<Fragment>(this,MaterialSection.ICON_NO_ICON,rippleSupport,MaterialSection.TARGET_LISTENER);
        section.setOnClickListener(this);
        section.setTitle(title);
        section.setTarget(target);

        return section;
    }

    // abstract methods

    public abstract void init(Bundle savedInstanceState);

    public void onHomeAsUpSelected() {}

    // get methods

    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Get the section which the user see
     * @return the current section
     */
    public MaterialSection getCurrentSection() {
        return currentSection;
    }

    /**
     * Get a setted section knowing his position
     *
     * N.B. this search only into section list and bottom section list.
     * @param position is the position of the section
     * @return the section at position or null if the section is not found
     */
    public MaterialSection getSectionAtCurrentPosition(int position) {
        for(MaterialSection section : sectionList) {
            if(section.getPosition() == position)
                return section;
        }

        for(MaterialSection section : bottomSectionList) {
            if(section.getPosition() == position)
                return section;
        }

        return null;
    }

    /**
     * Get a setted section knowing his title
     *
     * N.B. this search only into section list and bottom section list.
     * @param title is the title of the section
     * @return the section with title or null if the section is not founded
     */
    public MaterialSection getSectionByTitle(String title) {

        for(MaterialSection section : sectionList) {
            if(section.getTitle().equals(title)) {
                return section;
            }
        }

        for(MaterialSection section : bottomSectionList) {
            if(section.getTitle().equals(title))
                return section;
        }

        return null;
    }

    /**
     * Get the section list
     *
     * N.B. The section list contains the bottom sections
     * @return the list of sections setted
     */
    public List<MaterialSection> getSectionList() {
        List<MaterialSection> list = new LinkedList<>();

        for(MaterialSection section : sectionList)
            list.add(section);

        for(MaterialSection section : bottomSectionList)
            list.add(section);

        return list;
    }

    /**
     * Get current account
     * @return the account at first position
     */
    public MaterialAccount getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Get the account list
     * @return the account lists
     */
    public List<MaterialAccount> getAccountList() {
        return accountManager;
    }

    /**
     * Get the account knowing his position
     * @param position the position of the account (it can change at runtime!)
     * @return the account
     */
    public  MaterialAccount getAccountAtCurrentPosition(int position) {

        if (position < 0 || position >= accountManager.size())
            throw  new RuntimeException("Account Index Overflow");

        return findAccountNumber(position);
    }

    /**
     * Get the account knowing his title
     * @param title the title of the account (it can change at runtime!)
     * @return the account founded or null if the account not exists
     */
    public MaterialAccount getAccountByTitle(String title) {
        for(MaterialAccount account : accountManager)
            if(currentAccount.getTitle().equals(title))
                return account;

        return null;
    }


}