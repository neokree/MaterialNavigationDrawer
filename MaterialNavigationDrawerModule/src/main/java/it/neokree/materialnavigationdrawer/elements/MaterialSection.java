package it.neokree.materialnavigationdrawer.elements;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;
import it.neokree.materialnavigationdrawer.R;
import it.neokree.materialnavigationdrawer.util.Utils;

/**
 * Navigation Drawer section with Material Design style
 *
 * Created by neokree on 08/11/14.
 */
@SuppressWarnings("unused")
public class MaterialSection<Fragment> implements View.OnTouchListener, View.OnClickListener {

    public static final int TARGET_FRAGMENT = 0;
    public static final int TARGET_ACTIVITY = 1;
    public static final int TARGET_LISTENER = 2;

    public static final int ICON_NO_ICON = 0;
    public static final int ICON_24DP = 1;
    public static final int ICON_40DP = 2;

    private int position;
    private int targetType;
    private View view;
    private TextView text;
    private TextView notifications;
    private ImageView icon;
    private MaterialRippleLayout ripple;
    private MaterialSectionListener listener;
    private boolean isSelected;
    private boolean hasSectionColor;
    private boolean hasColorDark;
    private boolean rippleSupport = false;
    private boolean realColor;
    private boolean touchable;

    // COLORS
    private int colorPressed;
    private int colorUnpressed;
    private int colorSelected;
    private int sectionColor;
    private int iconColor;
    private int colorDark;
    private int textColor;
    private int notificationColor;

    private int numberNotifications;

    private String title;

    // TARGETS
    private Fragment targetFragment;
    private Intent targetIntent;
    private MaterialSectionListener targetListener;

    public MaterialSection(Context ctx, int iconType, boolean hasRippleSupport,  int target ) {
        rippleSupport = hasRippleSupport;

        if(rippleAnimationSupport()) {
            // section with ripple effect

            switch(iconType) {
                case ICON_NO_ICON:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_ripple, null);
                    break;
                case ICON_24DP:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon_ripple, null);

                    icon = (ImageView) view.findViewById(R.id.section_icon);
                    break;
                case ICON_40DP:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon_large_ripple, null);

                    icon = (ImageView) view.findViewById(R.id.section_icon);
                    break;
            }

            text = (TextView) view.findViewById(R.id.section_text);
            notifications = (TextView) view.findViewById(R.id.section_notification);
            ripple = (MaterialRippleLayout) view.findViewById(R.id.section_ripple);

        }
        else {
            // section with normal background

            switch (iconType) {
                case ICON_NO_ICON:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section, null);

                    text = (TextView) view.findViewById(R.id.section_text);
                    notifications = (TextView) view.findViewById(R.id.section_notification);
                    break;
                case ICON_24DP:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon, null);

                    text = (TextView) view.findViewById(R.id.section_text);
                    icon = (ImageView) view.findViewById(R.id.section_icon);
                    notifications = (TextView) view.findViewById(R.id.section_notification);
                    break;
                case ICON_40DP:
                    view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon_large, null);

                    text = (TextView) view.findViewById(R.id.section_text);
                    icon = (ImageView) view.findViewById(R.id.section_icon);
                    notifications = (TextView) view.findViewById(R.id.section_notification);
                    break;
            }
        }

        // resolve attributes from current theme
        Resources.Theme theme = ctx.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.sectionStyle,typedValue,true);
        TypedArray values = theme.obtainStyledAttributes(typedValue.resourceId,R.styleable.MaterialSection);
        try {
            colorPressed = values.getColor(R.styleable.MaterialSection_sectionBackgroundColorPressed,0x16000000);
            colorUnpressed = values.getColor(R.styleable.MaterialSection_sectionBackgroundColor,0x00FFFFFF);
            colorSelected = values.getColor(R.styleable.MaterialSection_sectionBackgroundColorSelected,0x0A000000);

            iconColor = values.getColor(R.styleable.MaterialSection_sectionColorIcon,0x000);
            textColor = values.getColor(R.styleable.MaterialSection_sectionColorText,0x000);
            notificationColor = values.getColor(R.styleable.MaterialSection_sectionColorNotification,0x000);

            // set text color into the view
            if(textColor != 0x000) {
                text.setTextColor(textColor);
            }
            if(notificationColor != 0x000) {
                notifications.setTextColor(notificationColor);
            }

            // set background color into the view
            if(!rippleAnimationSupport())
                view.setBackgroundColor(colorUnpressed);
            else {
                ripple.setRippleBackground(colorUnpressed);
                ripple.setRippleColor(colorPressed);
            }

        }
        finally {
            values.recycle();
        }

        if(rippleAnimationSupport()) {
            ripple.setOnClickListener(this);
        }
        else {
            view.setOnTouchListener(this);
        }

        isSelected = false;
        hasSectionColor = false;
        hasColorDark = false;
        touchable = true;
        realColor = false;
        targetType = target;
        numberNotifications = 0;
    }

    // methods for customizations

    public MaterialSection setSectionColor(int color) {
        hasSectionColor = true;
        sectionColor = color;

        return this;
    }

    public MaterialSection setSectionColor(int color,int colorDark) {
        setSectionColor(color);
        hasColorDark = true;
        this.colorDark = colorDark;

        return this;
    }

    /**
     * Set the number of notification for this section
     * @param notifications the number of notification active for this section
     * @return this section
     */
    public MaterialSection setNotifications(int notifications) {
        String textNotification;

        textNotification = String.valueOf(notifications);

        if(notifications < 1) {
            textNotification = "";
        }
        if(notifications > 99) {
            textNotification = "99+";
        }

        this.notifications.setText(textNotification);
        numberNotifications = notifications;

        return this;
    }

    public MaterialSection setNotificationsText(String text) {
        this.notifications.setText(text);
        return this;
    }

    public MaterialSection useRealColor() {
        realColor = true;
        if(icon != null) {
            Utils.setAlpha(icon,1f);
        }

        return this;
    }


    // internal methods

    // touch event without ripple support
    @Override
    @SuppressLint("NewApi")
    public boolean onTouch(View v, MotionEvent event) {
        if(touchable) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setBackgroundColor(colorPressed);

                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (isSelected)
                    view.setBackgroundColor(colorSelected);
                else
                    view.setBackgroundColor(colorUnpressed);

                return true;
            }


            if (event.getAction() == MotionEvent.ACTION_UP) {

                view.setBackgroundColor(colorSelected);
                afterClick();

                return true;
            }
        }

        return false;
    }

    //click event with ripple support
    @Override
    public void onClick(View v) {
        if(touchable) {
            ripple.setRippleBackground(colorSelected);

            afterClick();
        }
    }

    public void select() {
        isSelected = true;
        if(!rippleAnimationSupport())
            view.setBackgroundColor(colorSelected);
        else
            ripple.setRippleBackground(colorSelected);

        if(hasSectionColor) {
            text.setTextColor(sectionColor);

            if(icon != null && !realColor) {
                icon.setColorFilter(sectionColor);
                Utils.setAlpha(icon, 1f);
            }
        }
    }

    public void unSelect() {
        isSelected = false;
        if(!rippleAnimationSupport()) {
            view.setBackgroundColor(colorUnpressed);
        }
        else {
            ripple.setRippleBackground(colorUnpressed);
        }

        if (hasSectionColor) {
            text.setTextColor(textColor);

            if (icon != null && !realColor) {
                icon.setColorFilter(iconColor);
                Utils.setAlpha(icon, 0.54f);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    // setter

    public void setPosition(int position) {
        this.position = position;
    }

    public void setOnClickListener(MaterialSectionListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
        this.text.setText(title);
    }

    public void setIcon(Drawable icon) {
        this.icon.setImageDrawable(icon);
        if(!realColor)
            this.icon.setColorFilter(iconColor);
    }

    public void setIcon(Bitmap icon) {
        this.icon.setImageBitmap(icon);
        if(!realColor)
            this.icon.setColorFilter(iconColor);
    }
    
    public void setTarget(Fragment target) {
        this.targetFragment = target;
    }

    public void setTarget(Intent target) {
        this.targetIntent = target;
    }

    public void setTarget(MaterialSectionListener target) {
        this.targetListener = target;
    }

    public void setTypeface(Typeface typeface) {
        this.text.setTypeface(typeface);
        this.notifications.setTypeface(typeface);
    }

    public void setTouchable(boolean isTouchable) {
        touchable = isTouchable;
    }

    public void setPressingColor(int color) {
        colorPressed = color;

        if(rippleAnimationSupport())
            ripple.setRippleColor(colorPressed);
    }

    // alias of setColorUnpressed
    public void setBackgroundColor(int color) {
        colorUnpressed = color;

        if(!isSelected()) {
            if (rippleAnimationSupport()) {
                ripple.setRippleBackground(colorUnpressed);
            }
            else {
                view.setBackgroundColor(colorUnpressed);
            }
        }
    }

    public void setColorSelected(int color) {
        colorSelected = color;

        if(isSelected()) {
            if(rippleAnimationSupport())
                ripple.setRippleBackground(colorSelected);
            else
                view.setBackgroundColor(colorSelected);
        }
    }

    // getter

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public int getTarget() {
        return targetType;
    }

    public Fragment getTargetFragment() {
        return targetFragment;
    }

    public Intent getTargetIntent() {
        return targetIntent;
    }

    public MaterialSectionListener getTargetListener() {
        return targetListener;
    }

    public boolean hasSectionColor() {
        return hasSectionColor;
    }

    public boolean hasSectionColorDark() {
        return hasColorDark;
    }

    public int getSectionColor() {
        return sectionColor;
    }

    public int getSectionColorDark() {
        return colorDark;
    }

    public int getNotifications() {
        return numberNotifications;
    }

    public String getNotificationsText() {
        return this.notifications.getText().toString();
    }

    // private methods

    private void afterClick() {
        isSelected = true;

        if (hasSectionColor) {
            text.setTextColor(sectionColor);

            if (icon != null && !realColor) {
                icon.setColorFilter(sectionColor);
                Utils.setAlpha(icon, 1f);
            }
        }

        if (listener != null)
            listener.onClick(this);

        // se la sezione ha come target l'activity, dopo che questa viene avviata si deseleziona.
        if(this.getTarget() == TARGET_ACTIVITY)
            this.unSelect();

        // si fa arrivare il click anche allo sviluppatore
        if (this.getTarget() == TARGET_LISTENER && targetListener != null)
            this.targetListener.onClick(this);
    }

    private boolean rippleAnimationSupport() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && rippleSupport;
    }

}
