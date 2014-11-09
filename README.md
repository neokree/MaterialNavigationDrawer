MaterialNavigationDrawer
========================

Navigation Drawer Activity with material design style and simplified methods

It requires 14+ API and android support v7 (Toolbar)<br>

[Download example apk](https://raw.github.com/neokree/MaterialNavigationDrawer/master/example.apk)<br>

<h3>How to use</h3>
In your Activity...
```java
public class MyActivity extends MaterialNavigationDrawer {

    @Override
    public Fragment getCurrentFragment(int position) {
        switch(position) {
            case MaterialNavigationDrawer.SECTION_START:
              return new Fragment1();
            case MaterialNavigationDrawer.SECTION_START + 1:
              return new Fragment2();
              
            case MaterialNavigationDrawer.BOTTOM_SECTION_START:
              return new FragmentSettings();
              
            default: return new Fragment();
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // set cover background
        this.setDrawerBackground(this.getResources().getDrawable(R.drawable.mat1));
        // set user photo and data
        this.setUserPhoto(this.getResources().getDrawable(R.drawable.photo));
        this.setUsername("NeoKree");
        this.setUserEmail("neokree@gmail.com");
        
        // add your sections
        this.addSection(this.newSection("Section 1"));
        this.addSection(this.newSection("Section 2",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp)));
        this.addBottomSection(this.newBottomSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp)));
    }

    @Override
    public String getCurrentTitle(int position) {
        switch(position) {
            case MaterialNavigationDrawer.SECTION_START:
              return "My First Section";
            case MaterialNavigationDrawer.SECTION_START + 1:
              return "My Second Section";
              
            case MaterialNavigationDrawer.BOTTOM_SECTION_START:
              return "Settings";
              
            default: return "";
        }
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
Download and add MaterialNavigationDrawerModule to your project as module.<br>
Add this to your build.gradle:
```java 
dependencies {
    compile project(':MaterialNavigationDrawerModule')
}
```

<h3>Limitations</h3>
Actually, this library have some limitations: 
- There are no possibility to add separators
- There are no possibility to choose the color of section

These problems are currently in development

<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen1.png" alt="screenshot" width="300px" height="auto" />
<img src="https://raw.github.com/neokree/MaterialNavigationDrawer/master/screen3.png" alt="screenshot" width="300px" height="auto" />
