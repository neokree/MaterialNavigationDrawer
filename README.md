MaterialNavigationDrawer
========================

Navigation Drawer Activity with material design style and simplified methods<br>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialNavigationDrawer-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1114)&ensp;&ensp;&ensp;&ensp;&ensp;[![Donate](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K4GJELZKNEF68)

It requires 11+ API and android support v7 (Toolbar)<br>

[Download example apk](https://raw.github.com/neokree/MaterialNavigationDrawer/master/example.apk)<br>

<h3>How to use</h3>
In your Activity...
```java
public class MyActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle savedInstanceState) {
        // set cover background
        this.setDrawerBackground(this.getResources().getDrawable(R.drawable.mat1));
        // set user photo and data
        this.setUserPhoto(this.getResources().getDrawable(R.drawable.photo));
        this.setUsername("NeoKree");
        this.setUserEmail("neokree@gmail.com");

        // add your sections
        this.addSection(this.newSection("Section 1",new FragmentIndex()));
        this.addSection(this.newSection("Section 2",new FragmentIndex()));
        this.addDivisor();
        this.addSection(this.newSection("Section 2",this.getResources().getDrawable(R.drawable.section2),new FragmentIndex()));
        this.addBottomSection(this.newBottomSection("Settings",this.getResources().getDrawable(R.drawable.settings),new FragmentSettings()));
    }

}
```
In your styles.xml
```xml
<resources>

    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/light_blue_500</item>
        <item name="colorPrimaryDark">@color/light_blue_800</item>
        <item name="colorAccent">@color/grey_1000</item>
        <item name="windowActionBar">false</item>
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
    compile 'it.neokree:MaterialNavigationDrawer:1.0.1'
}
```

You don't know how to do something? Visit the [wiki](https://github.com/neokree/MaterialNavigationDrawer/wiki)!

<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen1.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen2.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen3.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen4.png" alt="screenshot" width="300px" height="auto" />
