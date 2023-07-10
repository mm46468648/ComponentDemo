package com.mooc.battle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mooc.battle.R;


public class DialogMatchTwoButton extends Dialog {
    private Context mContext;
    private TextView tvRight;
    private TextView tvLeft;
    private String strTitle;
    private TextView tvTitle;
    private RelativeLayout rlRoot;
    private String strRight;
    private String strLeft;
    private int tvDescColor;
    private int tvLeftColor;
    private int tvRightColor;
    private int bgResource;
    private int tvLeftBgResource;
    private int tvRightBgResource;
    private int buttonMargin;   //按钮的间距
    private InfoCallback callback;

    public DialogMatchTwoButton(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public DialogMatchTwoButton(Context context, int theme, InfoCallback callBack) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        mContext = context;
        this.callback = callBack;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_match_two_button_dialog);
        tvTitle = findViewById(R.id.layout_desc_dialog);
        rlRoot = findViewById(R.id.rlRoot);
        tvRight = findViewById(R.id.layout_right_dialog);
        tvLeft = findViewById(R.id.layout_left_dialog);
        initListener();
    }

    public void initListener() {
        tvRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // Intent intent = new Intent(mContext, Change)
                if (callback != null) {
                    callback.onRight();
                }
                dismiss();
            }
        });
        tvLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onLeft();
                }
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (!TextUtils.isEmpty(strTitle)) {
            tvTitle.setText(strTitle);
        }
        if (tvDescColor == 0) {
//            tvTitle.setTextColor(mContext.getResources().getColor(R.color.color_2));
        } else {
            tvTitle.setTextColor(tvDescColor);
        }
        if (!TextUtils.isEmpty(strRight)) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(strRight);
        } else {
            tvRight.setVisibility(View.GONE);
        }
        if (tvRightColor == 0) {
//            tvRight.setTextColor(mContext.getResources().getColor(R.color.color_616161));
        } else {
            tvRight.setTextColor(tvRightColor);
        }
        if (!TextUtils.isEmpty(strLeft)) {
            tvLeft.setText(strLeft);
        } else {
            tvLeft.setVisibility(View.GONE);
        }
        if (tvLeftColor == 0) {
//            tvLeft.setTextColor(mContext.getResources().getColor(R.color.color_616161));
        } else {
            tvLeft.setTextColor(tvLeftColor);
        }

        if (bgResource != 0) {
            rlRoot.setBackgroundResource(bgResource);
        }

        if (tvLeftBgResource != 0) {
            tvLeft.setBackgroundResource(tvLeftBgResource);
        }

        if (tvRightBgResource != 0) {
            tvRight.setBackgroundResource(tvRightBgResource);
        }

        if (buttonMargin != 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tvLeft.getLayoutParams();
            layoutParams.setMargins(0, 0, buttonMargin, 0);
        }

        if (callback != null) {
            callback.show();
        }


    }

    public void setDialogTitle(String str) {
        strTitle = str;
    }

    public void setStrRight(String strRight) {
        this.strRight = strRight;
    }

    public void setStrLeft(String strLeft) {
        this.strLeft = strLeft;
    }

    public void setTvDescColor(int tvDescColor) {
        this.tvDescColor = tvDescColor;
    }

    public void setTvLeftColor(int tvLeftColor) {
        this.tvLeftColor = tvLeftColor;
    }

    public void setTvLeftResource(int bgResource) {
        this.tvLeftBgResource = bgResource;
    }

    public void setTvRightBgResource(int bgResource) {
        this.tvRightBgResource = bgResource;
    }


    public void setButtonMargin(int buttomMargin) {
        this.buttonMargin = buttomMargin;
    }

    public void setBackGroundResource(int bgDrawable) {
        this.bgResource = bgDrawable;
    }


    public interface InfoCallback {
        void onRight();

        void onLeft();

        void show();
    }
}
