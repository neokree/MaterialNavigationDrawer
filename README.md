MaterialNavigationDrawer
========================

Navigation Drawer Activity with material design style and simplified methods<br>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialNavigationDrawer-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1114)&ensp;&ensp;&ensp;&ensp;&ensp;[![Donate](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K4GJELZKNEF68)

It requires 11+ API and android support v7 (Toolbar)<br>

[Download example apk](https://raw.github.com/neokree/MaterialNavigationDrawer/master/example.apk)<br>

If you are using MaterialNavigationDrawer in your app and would like to be listed here, please let me know via [email](mailto:neokree@gmail.com)! <br>

### How to add to your project
In your Activity...
```java
public class MyActivity extends MaterialNavigationDrawer implements MaterialAccountListener {

    @Override
    public void init(Bundle savedInstanceState) {

        // add first account
        MaterialAccount account = new MaterialAccount("NeoKree","neokree@gmail.com",this.getResources().getDrawable(R.drawable.photo),this.getResources().getDrawable(R.drawable.bamboo));
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
        this.addSection(this.newSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),i));

    }

}
```
In your styles.xml choose your version:
```xml
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="MaterialNavigationDrawerTheme">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    <!-- Light version theme. -->
    <style name="AppTheme" parent="MaterialNavigationDrawerTheme.Light">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    <!-- Light version with Black actionbar -->
    <style name="AppTheme" parent="MaterialNavigationDrawerTheme.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#8bc34a</item>
        <item name="colorPrimaryDark">#558b2f</item>
        <item name="colorAccent">@color/grey_1000</item>
    </style>
    
</resources>
```
N.B. Not override <code>OnCreate</code> method! Use <code>init</code> method instead.<br>

<h3>How to import </h3>
<h6>Android Studio</h6>
Add this to your build.gradle:
```java 
repositories {
    mavenCentral()
}

dependencies {
    compile 'it.neokree:MaterialNavigationDrawer:1.2.8'
}
```

You don't know how to do something? Visit the [wiki](https://github.com/neokree/MaterialNavigationDrawer/wiki)!

### External libraries used
[Calligraphy](https://github.com/chrisjenx/Calligraphy)
[Android-UI](https://github.com/markushi/android-ui)

<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen1.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen2.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen3.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen4.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen5.png" alt="screenshot" width="600px" height="auto" />
