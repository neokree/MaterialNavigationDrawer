MaterialNavigationDrawer
========================

Navigation Drawer Activity with material design style and simplified methods<br>
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialNavigationDrawer-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1114)&ensp;&ensp;&ensp;&ensp;&ensp;[![Donate](https://www.paypalobjects.com/en_GB/i/btn/btn_donate_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K4GJELZKNEF68)

It requires 14+ API and android support v7 (Toolbar)<br>

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
<h6>Add Section methods</h6>
```java
    // only text section, it opens an activity
    public MaterialSection newSection(String title,Intent target)
    
    // only text section, it opens a fragment
    public MaterialSection newSection(String title,Fragment target)
    
    // icon bitmap and text section, it opens an activity
    public MaterialSection newSection(String title, Bitmap icon,Intent target)
    
    // icon bitmap and text section, it opens a fragment
    public MaterialSection newSection(String title, Bitmap icon,Fragment target)
    
    // icon drawable and text section, it opens an activity
    public MaterialSection newSection(String title, Drawable icon, Intent target)
    
    // icon drawable and text section, it opens a fragment
    public MaterialSection newSection(String title, Drawable icon, Fragment target)
```

<h6>Add Bottom Section methods</h6>
```java
    // only text section, it opens an activity
    public MaterialSection newBottomSection(String title,Intent target)
    
    // only text section, it opens a fragment
    public MaterialSection newBottomSection(String title,Fragment target)
    
    // icon bitmap and text section, it opens an activity
    public MaterialSection newBottomSection(String title, Bitmap icon,Intent target)
    
    // icon bitmap and text section, it opens a fragment
    public MaterialSection newBottomSection(String title, Bitmap icon,Fragment target)
    
    // icon drawable and text section, it opens an activity
    public MaterialSection newBottomSection(String title, Drawable icon, Intent target)
    
    // icon drawable and text section, it opens a fragment
    public MaterialSection newBottomSection(String title, Drawable icon, Fragment target)
```

<h6>Add separator method</h6>
```java
@Override
    public void init(Bundle savedInstanceState) {
        this.addDivisor();
    }
```

<h3>How to import </h3>
<h6>Android Studio</h6>
Download and add MaterialNavigationDrawerModule to your project as module.<br>
Add this to your build.gradle:
```java 
dependencies {
    compile project(':MaterialNavigationDrawerModule')
}
```

<h3>Limitations</h3>
Actually, this library have some limitations: 
- Tablet Material Design style not implemented

These problems are currently in development

<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen1.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen2.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen3.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen4.png" alt="screenshot" width="300px" height="auto" />
