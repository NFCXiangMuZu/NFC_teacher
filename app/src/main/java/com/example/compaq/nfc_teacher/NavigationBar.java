package com.example.compaq.nfc_teacher;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Compaq on 2016/7/19.
 */
public class NavigationBar extends RelativeLayout implements View.OnClickListener {

    public static final int NAVIGATION_BUTTON_LEFT = 0;
    public static final int NAVIGATION_BUTTON_RIGHT = 1;

    private Context mContext;
    private NavigationBarListener mListener;

    public NavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NavigationBar(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-1, -2);
        this.setLayoutParams(lp);
        this.setBackgroundResource(R.drawable.navigation_bar_bg);
    }

    public void setLeftBarButton() {
        ImageButton oldButton = (ImageButton) this.findViewWithTag(new Integer(NAVIGATION_BUTTON_LEFT));
        if (oldButton != null)
            this.removeView(oldButton);

        ImageButton newButton = new ImageButton(mContext);
        newButton.setTag(new Integer(NAVIGATION_BUTTON_LEFT)); // used to determine which button is pressed and to remove old buttons

        // set OnClickListener
        newButton.setOnClickListener(this);

        //设置按钮图片
        newButton.setImageDrawable(getResources().getDrawable(R.drawable.wechat));
        //newButton.setMaxWidth(10);
        //newButton.setMaxHeight(10);

        // set LayoutParams
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.setMargins(10, 0, 10, 0);
        newButton.setLayoutParams(lp);

        // add button
        this.addView(newButton);
    }

    public void setRightBarButton() {

        ImageButton oldButton = (ImageButton) this.findViewWithTag(new Integer(NAVIGATION_BUTTON_RIGHT));
        if (oldButton != null)
            this.removeView(oldButton);

        ImageButton newButton = new ImageButton(mContext);
        newButton.setTag(new Integer(NAVIGATION_BUTTON_RIGHT)); // used to determine which button is pressed and to remove old button

        // set OnClickListener
        newButton.setOnClickListener(this);

        newButton.setImageDrawable(getResources().getDrawable(R.drawable.wechat));
        //newButton.setMaxHeight(10);
        //newButton.setMaxWidth(10);

        // set LayoutParams
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lp.setMargins(10, 0, 10, 0);
        newButton.setLayoutParams(lp);

        // set button drawable
       // newButton.setBackgroundResource(R.drawable.navigation_bar_btn);

        // add button
        this.addView(newButton);
    }

    public void setBarTitle(String title) {
        // remove old title (if exists)
        TextView oldTitle = (TextView) this.findViewWithTag("title");
        if (oldTitle != null)
            this.removeView(oldTitle);

        TextView newTitle = new TextView(mContext);
        newTitle.setTag("title");

        // set LayoutParams
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(-2, -2);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        lp.setMargins(0, 30, 0, 30);
        newTitle.setLayoutParams(lp);

        // set text
        newTitle.setText(title);
        newTitle.setTextSize(15);
        newTitle.setTextColor(Color.BLACK);

        // add title to NavigationBar
        this.addView(newTitle);
    }

    public void setNavigationBarListener(NavigationBarListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        int which = ((Integer) v.getTag()).intValue();
        if (mListener != null) {
            mListener.OnNavigationButtonClick(which);
        }
    }

    /**
     * Listener for NavigationBar.
     */
    public interface NavigationBarListener {

        /**
         * Called when the user presses either of the buttons on the NavigationBar.
         *
         * @param which - indicates which button was pressed, ie: NavigationBar.NAVIGATION_BUTTON_LEFT
         */
        public void OnNavigationButtonClick(int which);
    }
}
