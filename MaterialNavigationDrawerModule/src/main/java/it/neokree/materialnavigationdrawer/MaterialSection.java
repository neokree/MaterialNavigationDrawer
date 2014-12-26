package it.neokree.materialnavigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Navigation Drawer section with Material Design style
 *
 * Created by neokree on 08/11/14.
 */
public class MaterialSection<Fragment> implements View.OnTouchListener {

    public static final int TARGET_FRAGMENT = 0;
    public static final int TARGET_ACTIVITY = 1;
    public static final int TARGET_LISTENER = 2;

    private int position;
    private int targetType;
    private View view;
    private TextView text;
    private TextView notifications;
    private ImageView icon;
    private MaterialSectionListener listener;
    private boolean isSelected;
    private boolean sectionColor;
    private boolean hasColorDark;

    private int colorPressed;
    private int colorUnpressed;
    private int colorSelected;
    private int iconColor;
    private int colorDark;

    private int numberNotifications;

    private String title;

    private Fragment targetFragment;
    private Intent targetIntent;
    private MaterialSectionListener targetListener;

    public MaterialSection(Context ctx, boolean hasIcon, int target ) {

        if(!hasIcon) {
            view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section,null);

            text = (TextView) view.findViewById(R.id.section_text);
            notifications = (TextView) view.findViewById(R.id.section_notification);
        }
        else {
            view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon,null);

            text = (TextView) view.findViewById(R.id.section_text);
            icon = (ImageView) view.findViewById(R.id.section_icon);
            notifications = (TextView) view.findViewById(R.id.section_notification);
        }

        view.setOnTouchListener(this);


        colorPressed = Color.parseColor("#16000000");
        colorUnpressed = Color.parseColor("#00FFFFFF");
        colorSelected = Color.parseColor("#0A000000");
        iconColor = Color.BLACK;
        isSelected = false;
        sectionColor = false;
        hasColorDark = false;
        targetType = target;
        numberNotifications = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            view.setBackgroundColor(colorPressed);
            return true;
        }

        if( event.getAction() == MotionEvent.ACTION_CANCEL) {
            if(isSelected)
                view.setBackgroundColor(colorSelected);
            else
                view.setBackgroundColor(colorUnpressed);

            return true;
        }


        if( event.getAction() == MotionEvent.ACTION_UP) {
            isSelected = true;
            view.setBackgroundColor(colorSelected);

            if (sectionColor) {
                text.setTextColor(iconColor);

                if(icon != null){
                    icon.setColorFilter(iconColor);
                    icon.setAlpha(1f);
                }
            }

            if(listener != null)
                listener.onClick(this);

            // si fa arrivare il click anche allo sviluppatore
            if(this.getTarget() == TARGET_LISTENER && targetListener != null)
                this.targetListener.onClick(this);

            return true;
        }

        return false;
    }

    public void select() {
        isSelected = true;
        view.setBackgroundColor(colorSelected);

        if(sectionColor) {
            text.setTextColor(iconColor);

            if(icon != null) {
                icon.setColorFilter(iconColor);
                icon.setAlpha(1f);
            }
        }
    }

    public void unSelect() {
        isSelected = false;
        view.setBackgroundColor(colorUnpressed);

        if (sectionColor) {
            text.setTextColor(Color.BLACK);

            if(icon != null) {
                icon.setColorFilter(Color.BLACK);
                icon.setAlpha(0.54f);
            }
        }
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
        //if(icon != null)
        //    this.icon.setColorFilter(color);
        
        return this;
    }

    public MaterialSection setSectionColor(int color,int colorDark) {
        setSectionColor(color);
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

    public int getNotifications() {
        return numberNotifications;
    }

}
