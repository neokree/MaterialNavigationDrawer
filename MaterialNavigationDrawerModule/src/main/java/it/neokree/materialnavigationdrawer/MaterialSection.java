package it.neokree.materialnavigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Navigation Drawer section with Material Design style
 *
 * Created by neokree on 08/11/14.
 */
public class MaterialSection implements View.OnTouchListener {

    public static final boolean TARGET_FRAGMENT = true;
    public static final boolean TARGET_ACTIVITY = false;

    private int position;
    private View view;
    private TextView text;
    private ImageView icon;
    private MaterialSectionListener listener;
    private boolean isSelected;
    private boolean targetType;

    private int colorPressed;
    private int colorUnpressed;
    private int colorSelected;
    private int iconColor;

    private String title;

    private Fragment targetFragment;
    private Intent targetIntent;

    public MaterialSection(Context ctx, int position, boolean hasIcon, boolean target ) {

        if(!hasIcon) {
            view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section,null);

            text = (TextView) view.findViewById(R.id.section_text);
        }
        else {
            view = LayoutInflater.from(ctx).inflate(R.layout.layout_material_section_icon,null);

            text = (TextView) view.findViewById(R.id.section_text);
            icon = (ImageView) view.findViewById(R.id.section_icon);
        }

        view.setOnTouchListener(this);


        colorPressed = Color.parseColor("#16000000");
        colorUnpressed = Color.parseColor("#00FFFFFF");
        colorSelected = Color.parseColor("#0A000000");
        iconColor = Color.rgb(98,98,98);
        this.position = position;
        isSelected = false;
        targetType = target;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            view.setBackgroundColor(colorPressed);
            return true;
        }

        if( event.getAction() == MotionEvent.ACTION_UP) {
            isSelected = true;
            view.setBackgroundColor(colorSelected);

            if(listener != null)
                listener.onClick(this);

            return true;
        }

        return false;
    }

    public void select() {
        isSelected = true;
        view.setBackgroundColor(colorSelected);
    }

    public void unSelect() {
        isSelected = false;
        view.setBackgroundColor(colorUnpressed);
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

    public void setTarget(Intent intent) {
        this.targetIntent = intent;
    }

    public boolean getTarget() {
        return targetType;
    }

    public Fragment getTargetFragment() {
        return targetFragment;
    }
    public Intent getTargetIntent() {
        return targetIntent;
    }

}
