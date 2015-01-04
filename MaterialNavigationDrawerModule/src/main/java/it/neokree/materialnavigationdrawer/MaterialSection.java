package it.neokree.materialnavigationdrawer;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import at.markushi.ui.RevealColorView;

/**
 * Navigation Drawer section with Material Design style
 *
 * Created by neokree on 08/11/14.
 */
public class MaterialSection<Fragment> implements View.OnTouchListener {

    public static final int TARGET_FRAGMENT = 0;
    public static final int TARGET_ACTIVITY = 1;
    public static final int TARGET_LISTENER = 2;

    private static final int REVEAL_DURATION = 250;

    private int position;
    private int targetType;
    private View view;
    private TextView text;
    private TextView notifications;
    private ImageView icon;
    private RevealColorView ripple;
    private MaterialSectionListener listener;
    private boolean isSelected;
    private boolean sectionColor;
    private boolean hasColorDark;
    private boolean rippleSupport = false;
    private boolean touchable;

    private Point lastTouchedPoint;

    // COLORS
    private int colorPressed;
    private int colorUnpressed;
    private int colorSelected;
    private int iconColor;
    private int colorDark;

    private int numberNotifications;

    private String title;

    // TARGETS
    private Fragment targetFragment;
    private Intent targetIntent;
    private MaterialSectionListener targetListener;

    @Deprecated
    public MaterialSection(Context ctx, boolean hasIcon, int target ) {
        this(ctx,hasIcon,false,target);
    }

    public MaterialSection(Context ctx, boolean hasIcon,boolean hasRippleSupport, int target ) {
        rippleSupport = hasRippleSupport;

        if(rippleAnimationSupport()) {
            // section with ripple effect

            if (!hasIcon) {
                view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_ripple, null);

                text = (TextView) view.findViewById(R.id.section_text);
                notifications = (TextView) view.findViewById(R.id.section_notification);
                ripple = (RevealColorView) view.findViewById(R.id.section_ripple);
            } else {
                view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon_ripple, null);

                text = (TextView) view.findViewById(R.id.section_text);
                icon = (ImageView) view.findViewById(R.id.section_icon);
                notifications = (TextView) view.findViewById(R.id.section_notification);
                ripple = (RevealColorView) view.findViewById(R.id.section_ripple);
            }

        }
        else {
            if (!hasIcon) {
                view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section, null);

                text = (TextView) view.findViewById(R.id.section_text);
                notifications = (TextView) view.findViewById(R.id.section_notification);
            } else {
                view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon, null);

                text = (TextView) view.findViewById(R.id.section_text);
                icon = (ImageView) view.findViewById(R.id.section_icon);
                notifications = (TextView) view.findViewById(R.id.section_notification);
            }
        }

        view.setOnTouchListener(this);


        colorPressed = Color.parseColor("#16000000");
        colorUnpressed = Color.parseColor("#00FFFFFF");
        colorSelected = Color.parseColor("#0A000000");
        iconColor = Color.BLACK;
        isSelected = false;
        sectionColor = false;
        hasColorDark = false;
        touchable = true;
        targetType = target;
        numberNotifications = 0;
    }

    @Override
    @SuppressLint("NewApi")
    public boolean onTouch(View v, MotionEvent event) {
        if(touchable) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                if (!rippleAnimationSupport())
                    view.setBackgroundColor(colorPressed);
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!rippleAnimationSupport())
                    if (isSelected)
                        view.setBackgroundColor(colorSelected);
                    else
                        view.setBackgroundColor(colorUnpressed);

                return true;
            }


            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (!rippleAnimationSupport()) {
                    view.setBackgroundColor(colorSelected);
                    afterClick();
                } else {
                    // get the point
                    lastTouchedPoint = new Point();
                    lastTouchedPoint.x = (int) event.getX();
                    lastTouchedPoint.y = (int) event.getY();

                    this.ripple.reveal(lastTouchedPoint.x, lastTouchedPoint.y, colorPressed, 0, REVEAL_DURATION, new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ripple.reveal(lastTouchedPoint.x, lastTouchedPoint.y, colorSelected, 0, REVEAL_DURATION, null);
                            afterClick();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            onAnimationEnd(animation);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
                return true;
            }
        }

        return false;
    }

    public void select() {
        isSelected = true;
        if(!rippleAnimationSupport())
            view.setBackgroundColor(colorSelected);
        else
            ripple.reveal(0,0,colorSelected,0,0,null);

        if(sectionColor) {
            text.setTextColor(iconColor);

            if(icon != null) {
                icon.setColorFilter(iconColor);
                setAlpha(icon, 1f);
            }
        }
    }

    public void unSelect() {
        isSelected = false;
        if(!rippleAnimationSupport()) {
            view.setBackgroundColor(colorUnpressed);
        }
        else {
            ripple.hide(0,0,colorUnpressed,0,0,null);
        }

        if (sectionColor) {
            text.setTextColor(Color.BLACK);

            if (icon != null) {
                icon.setColorFilter(Color.BLACK);
                setAlpha(icon, 0.54f);
            }
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setOnClickListener(MaterialSectionListener listener) {
        this.listener = listener;
    }

    public View getView() {
        return view;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.text.setText(title);
    }

    public void setIcon(Drawable icon) {
        this.icon.setImageDrawable(icon);
        this.icon.setColorFilter(iconColor);
    }

    public void setIcon(Bitmap icon) {
        this.icon.setImageBitmap(icon);
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

    @Deprecated
    public MaterialSection setSectionColor(int color) {
        sectionColor = true;
        iconColor = color;
        
        return this;
    }

    public MaterialSection setSectionColor(int color,int colorDark) {
        sectionColor = true;
        iconColor = color;
        hasColorDark = true;
        this.colorDark = colorDark;

        return this;
    }

    public boolean hasSectionColor() {
        return sectionColor;
    }

    public boolean hasSectionColorDark() {
        return hasColorDark;
    }

    public int getSectionColor() {
        return iconColor;
    }

    public int getSectionColorDark() {
        return colorDark;
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

    public int getNotifications() {
        return numberNotifications;
    }

    public String getNotificationsText() {
        return this.notifications.getText().toString();
    }

    public void setTouchable(boolean isTouchable) {
        touchable = isTouchable;
    }

    // private methods

    void setAlpha(View v, float alpha) {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
           v.setAlpha(alpha);
       } else {
           AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
           animation.setDuration(0);
           animation.setFillAfter(true);
           v.startAnimation(animation);
       }
    }

    private void afterClick() {
        isSelected = true;

        if (sectionColor) {
            text.setTextColor(iconColor);

            if (icon != null) {
                icon.setColorFilter(iconColor);
                setAlpha(icon, 1f);
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && rippleSupport)
            return true;
        else
            return false;
    }

}
