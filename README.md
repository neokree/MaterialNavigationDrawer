MaterialNavigationDrawer
========================

Navigation Drawer Activity with material design style and simplified methods<br>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialNavigationDrawer-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1114)&ensp;&ensp;&ensp;&ensp;&ensp;[![Donate](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K4GJELZKNEF68)

It requires 10+ API and android support v7 (Toolbar)<br>

[Download example apk](https://raw.github.com/neokree/MaterialNavigationDrawer/master/example.apk)<br>

If you are using MaterialNavigationDrawer in your app and would like to be listed here, please let me know via [email](mailto:neokree@gmail.com)! <br>

### How to add to your project
In your styles.xml choose your version:
```xml
<resources>

    <!-- Base application theme. -->
    <style name="NavigationDrawer" parent="MaterialNavigationDrawerTheme">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    <!-- Light version theme. -->
    <style name="NavigationDrawer" parent="MaterialNavigationDrawerTheme.Light">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    <!-- Light version with Black actionbar -->
    <style name="NavigationDrawer" parent="MaterialNavigationDrawerTheme.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    
</resources>
```
Remember to set your theme in your _AndroidManifest.xml_:
```xml
<activity android:name=".NavigationDrawerActivity" android:theme="@style/NavigationDrawer"/>
```

In your Activity...
```java
public class MyActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    MaterialSection section1, section2, recorder, night, last, settingsSection;

    @Override
    public void init(Bundle savedInstanceState) {

        // add first account
        MaterialAccount account = new MaterialAccount("NeoKree","neokree@gmail.com",this.getResources().getDrawable(R.drawable.photo),this.getResources().getDrawable(R.drawable.bamboo));
        this.addAccount(account);

        // set listener
        this.setAccountListener(this);

        // create sections
        section1 = this.newSection("Section 1",new FragmentIndex());
        section2 = this.newSection("Section 2",new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection section) {
                Toast.makeText(NavigationDrawerActivityRipple.this, "Section 2 Clicked", Toast.LENGTH_SHORT).show();

                // deselect section when is clicked
                section.unSelect();
            }
        });
        // recorder section with icon and 10 notifications
        recorder = this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp),new FragmentIndex()).setNotifications(10);
        // night section with icon, section color and notifications
        night = this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_hotel_grey600_24dp), new FragmentIndex())
                .setSectionColor(Color.parseColor("#2196f3"),Color.parseColor("#1565c0")).setNotifications(150);
        // night section with section color
        last = this.newSection("Last Section", new FragmentButton()).setSectionColor(Color.parseColor("#ff9800"),Color.parseColor("#ef6c00"));

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

        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
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
}
```
#### Do NOT

- Override <code>OnCreate</code> method! Use <code>init</code> method instead.
- Call <code>setContentView</code> in the activity since the library will do it with its own layout (move the call into the fragment).

<h3>How to import </h3>
<h6>Android Studio</h6>
Add this to your build.gradle:
```java 
repositories {
    mavenCentral()
}

dependencies {
    compile 'it.neokree:MaterialNavigationDrawer:1.2.9'
}
```

You don't know how to do something? Visit the [wiki](https://github.com/neokree/MaterialNavigationDrawer/wiki)!<br>
You need some examples? See the [example project](https://github.com/neokree/MaterialNavigationDrawer/tree/master/MaterialNavigationDrawer)!

### External libraries used
[Calligraphy](https://github.com/chrisjenx/Calligraphy) <br>
[Android-UI](https://github.com/markushi/android-ui)

<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen1.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen2.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen3.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen4.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen6.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen7.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen8.jpg" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen5.jpg" alt="screenshot" width="600px" height="auto" />
