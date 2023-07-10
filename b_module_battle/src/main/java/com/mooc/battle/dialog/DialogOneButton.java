package com.mooc.battle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mooc.battle.R;


/**
 * 只有一个按钮的弹框
 */
public class DialogOneButton extends Dialog {
    private Context mContext;
    private TextView tvRight;
    private String strTitle;
    private TextView tvTitle;
    private String strRight = "确定";
    private String strLeft;
    private int tvDescColor;
    private int tvLeftColor;
    private int tvRightColor;
    private InfoCallback callback;
    private int bgResource;
    private int buttonBgResource;
    private ConstraintLayout rlRoot;

    public DialogOneButton(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogOneButton(Context context, int theme, InfoCallback callBack) {
        super(context, theme);
        mContext = context;
        this.callback = callBack;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_two_one_dialog);
        tvTitle = findViewById(R.id.layout_desc_dialog);
        tvRight = findViewById(R.id.layout_right_dialog);
        rlRoot = findViewById(R.id.rlRoot);

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
    }

    @Override
    public void show() {
        super.show();
        if (!TextUtils.isEmpty(strTitle)) {
            tvTitle.setText(strTitle);
        }
        if (tvDescColor == 0) {
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

        if (bgResource != 0) {
            rlRoot.setBackgroundResource(bgResource);
        }

        if (buttonBgResource != 0) {
            tvRight.setBackgroundResource(buttonBgResource);
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

    public void setTvRightColor(int tvRightColor) {
        this.tvRightColor = tvRightColor;
    }

    public void setBackGroundResource(int bgDrawable) {
        this.bgResource = bgDrawable;
    }

    public void setButtonBgResource(int bgResource) {
        this.buttonBgResource = bgResource;
    }

    public interface InfoCallback {
        void onRight();

        void show();
    }
}
