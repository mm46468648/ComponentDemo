package com.mooc.commonbusiness.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mooc.commonbusiness.R;


public class MoreButtonDialog extends Dialog {
    private Context mContext;
    private TextView tvCancel;
    private TextView tvMinimize;
    private TextView tvQuit;
    private View lineTwo;
    private View lineOne;
    private String strTitle;
    private TextView tvTitle;
    private String strLeft;
    private String strMinimize;
    private String strRight;
    private int colorLeft = 0;
    private int colorRight = 0;
    private SpannableString strSpan;

    private InfoCallback callback;
    private InfoMoreCallback moreCallback;

    public MoreButtonDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public MoreButtonDialog(Context context, int theme, InfoCallback callBack) {
        super(context, theme);
        mContext = context;
        this.callback = callBack;
    }

    public MoreButtonDialog(Context context, int theme, InfoMoreCallback callBack) {
        super(context, theme);
        mContext = context;
        this.moreCallback = callBack;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout_more_dialog);
        tvTitle = findViewById(R.id.layout_title_more);
        tvCancel = findViewById(R.id.layout_cancel_more);
        tvMinimize = findViewById(R.id.layout_minimize_more);
        tvQuit = findViewById(R.id.layout_quit_more);
        lineTwo = findViewById(R.id.layout_line_two_more);
        lineOne = findViewById(R.id.layout_line_one_more);
        initListener();
    }

    public void initListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

                // Intent intent = new Intent(mContext, Change)
                if (!TextUtils.isEmpty(strMinimize)) {
                    if (moreCallback != null) {
                        moreCallback.onLeft();
                    }
                } else {
                    if (callback != null) {
                        callback.onLeft();
                    }
                }
            }
        });
        tvMinimize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (!TextUtils.isEmpty(strMinimize)) {
                    if (moreCallback != null) {
                        moreCallback.onMinimize();
                    }
                }
            }
        });
        tvQuit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

                if (!TextUtils.isEmpty(strMinimize)) {
                    if (moreCallback != null) {
                        moreCallback.onRight();
                    }
                } else {
                    if (callback != null) {
                        callback.onRight();
                    }
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (!TextUtils.isEmpty(strTitle)) {
            tvTitle.setText(strTitle);
        }
        if (strSpan != null) {
            tvTitle.setText(strSpan);
        }
        if (!TextUtils.isEmpty(strLeft)) {
            tvCancel.setVisibility(View.VISIBLE);
            lineOne.setVisibility(View.VISIBLE);
            tvCancel.setText(strLeft);
        } else {
            tvCancel.setVisibility(View.GONE);
            lineOne.setVisibility(View.GONE);
        }
        if (colorLeft == 0) {
            tvCancel.setTextColor(mContext.getResources().getColor(R.color.color_9B9B9B));
        } else {
            tvCancel.setTextColor(colorLeft);
        }
        if (!TextUtils.isEmpty(strMinimize)) {
            tvMinimize.setVisibility(View.VISIBLE);
            lineTwo.setVisibility(View.VISIBLE);
            tvMinimize.setText(strMinimize);
        } else {
            tvMinimize.setVisibility(View.GONE);
            lineTwo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(strRight)) {
            tvQuit.setText(strRight);
        }
        if (colorRight == 0) {
            tvQuit.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            tvQuit.setTextColor(colorRight);
        }
        if (callback != null) {
            callback.show();
        }
    }

    public void setDialogTitle(String str) {
        strTitle = str;
    }

    public void setStrLeft(String strLeft) {
        this.strLeft = strLeft;
    }

    public void setStrMinimize(String strMinimize) {
        this.strMinimize = strMinimize;
    }

    public void setStrRight(String strRight) {
        this.strRight = strRight;
    }

    public void setColorLeft(int colorLeft) {
        this.colorLeft = colorLeft;
    }

    public void setColorRight(int colorRight) {
        this.colorRight = colorRight;
    }

    public void setStrSpan(SpannableString strSpan) {
        this.strSpan = strSpan;
    }

    public interface InfoMoreCallback {
        void onLeft();

        void onMinimize();

        void onRight();

        void show();
    }

    public interface InfoCallback {
        void onLeft();

        void onRight();

        void show();
    }
}
