package it.neokree.materialnavigationdrawer.elements;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.neokree.materialnavigationdrawer.R;
import it.neokree.materialnavigationdrawer.util.Utils;

/**
 * Created by neokree on 17/01/15.
 */
public class MaterialSubheader {

    private CharSequence title;
    private int titleColor;

    private TextView text;
    private View view;

    public MaterialSubheader(Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;

        // create layout
        LinearLayout layout = new LinearLayout(ctx);
        layout.setOrientation(LinearLayout.VERTICAL);

        // inflate the line
        View view = new View(ctx);
        view.setBackgroundColor(Color.parseColor("#e0e0e0"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        params.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));
        layout.addView(view,params);

        // inflate the text
        text = new TextView(ctx);
        Utils.setAlpha(text,0.54f);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        text.setGravity(Gravity.START);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsText.setMargins((int) (16 * density),0, (int) (16 * density) , (int) (4 * density));

        layout.addView(text,paramsText);
        this.view = layout;

        // get attributes from current theme
        Resources.Theme theme = ctx.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.sectionStyle,typedValue,true);
        TypedArray values = theme.obtainStyledAttributes(typedValue.resourceId,R.styleable.MaterialSection);
        try {
            titleColor = values.getColor(R.styleable.MaterialSubheader_subheaderTitleColor,0x000);


        }
        finally {
            values.recycle();
        }

        // set attributes to the view
        text.setTextColor(Color.BLACK);

    }

    public void setTitleFont(Typeface font) {
        text.setTypeface(font);
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        text.setText(title);
    }

    public void setTitleColor(int color) {
        titleColor = color;
        text.setTextColor(color);
    }

    public int getTitleColor() {
        return titleColor;
    }

    public CharSequence getTitle() {
        return title;
    }

    public View getView() {
        return view;
    }

}
