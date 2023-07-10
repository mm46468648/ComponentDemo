package com.mooc.splash.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.UrlConstants;
import com.mooc.commonbusiness.route.Paths;
import com.mooc.splash.R;

/**
 * 用户隐私权限弹窗
 */
public class DialogAgreeMeant extends Dialog {
    private final Context mContext;
    private TextView tvRight;
    private TextView tvLeft;
    private TextView tip2;
    private TextView textRead;
    private Button btnRead;

    private String strTitle;
    private TextView tvTitle;
    private TextView tvDes;
    private String strRight;
    private String strLeft;
    private int tvDescColor;
    private int tvLeftColor;
    private int tvRightColor;
    private InfoCallback callback;

    boolean hasAgree = false;
    private View touchAgreeView;
    private TextView tvTip;

    public DialogAgreeMeant(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public DialogAgreeMeant(Context context, int theme, InfoCallback callBack) {
        super(context, theme);
        mContext = context;
        this.callback = callBack;
        setCancelable(false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_dialog_agreement);
        tvTitle = findViewById(R.id.title);
        tvRight = findViewById(R.id.layout_right_dialog);
        tvDes = findViewById(R.id.layout_desc_dialog);
        tvLeft = findViewById(R.id.layout_left_dialog);
        tip2 = findViewById(R.id.tip2);
        touchAgreeView = findViewById(R.id.touch_view);
        textRead = findViewById(R.id.text_read);
        btnRead = findViewById(R.id.btn_read);
        tvTip = findViewById(R.id.message_read);
        setTipSpannable();
        setDesSpannable();

        initListener();
    }

    private void setTipSpannable() {
        SpannableString spannableString = new SpannableString(getContext().getResources().getString(R.string.text_agree_tip));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ARouter.getInstance().build(Paths.PAGE_WEB_PRIVACY)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE, mContext.getString(R.string.text_str_privacy_policy))
                        .navigation();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new UnderlineSpan(), 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_0085D0)), 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ARouter.getInstance().build(Paths.PAGE_WEB)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE, mContext.getString(R.string.text_str_user_service_intro))
                        .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.USER_SERVICE_AGREDDMENT_URL)
                        .navigation();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan1, 11, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_0085D0)), 11, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tip2.setText(spannableString);
        tip2.setMovementMethod(LinkMovementMethod.getInstance());


        SpannableString spannableString2 = new SpannableString(getContext().getResources().getString(R.string.text_str_read_privacy_agreement));
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                ARouter.getInstance().build(Paths.PAGE_WEB_PRIVACY)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE, mContext.getString(R.string.text_str_privacy_policy))
                        .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.PRIVACY_POLICY_URL)
                        .navigation();

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString2.setSpan(clickableSpan2, 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        spannableString2.setSpan(new UnderlineSpan(), 8, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_0085D0)), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                ARouter.getInstance().build(Paths.PAGE_WEB)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE, mContext.getString(R.string.text_str_user_service_intro))
                        .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.USER_SERVICE_AGREDDMENT_URL)
                        .navigation();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        spannableString2.setSpan(clickableSpan3, 15, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString2.setSpan(new UnderlineSpan(), 15, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString2.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_0085D0)), 15, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textRead.setText(spannableString2);
        textRead.setMovementMethod(LinkMovementMethod.getInstance());


    }


    private void setDesSpannable() {
        SpannableString spannableString = new SpannableString(getContext().getResources().getString(R.string.text_agree_ment));
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_2)), 39, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 39, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 86, 92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_2)), 86, 92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 103, 111, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_2)), 103, 111, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvDes.setText(spannableString);
        tvDes.setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void initListener() {
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasAgree) {
                    tvTip.setVisibility(View.VISIBLE);
                    return;
                }

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

//        touchAgreeView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                hasAgree = !hasAgree;
//                if (hasAgree) {
//                    btnRead.setBackgroundResource(R.mipmap.splash_ic_green_box);
//                } else {
//                    btnRead.setBackgroundResource(R.mipmap.splash_ic_grey_box);
//                }
//                return false;
//            }
//        });

        touchAgreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasAgree = !hasAgree;
                if (hasAgree) {
                    btnRead.setBackgroundResource(R.mipmap.splash_ic_green_box);
                } else {
                    btnRead.setBackgroundResource(R.mipmap.splash_ic_grey_box);
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
        if (tvDescColor == 0) {
            tvDes.setTextColor(mContext.getResources().getColor(R.color.color_3));
        } else {
            tvDes.setTextColor(tvDescColor);
        }
        if (!TextUtils.isEmpty(strRight)) {
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText(strRight);
        } else {
            tvRight.setVisibility(View.GONE);
        }
        if (tvRightColor == 0) {
            tvRight.setTextColor(mContext.getResources().getColor(R.color.color_white));
        } else {
            tvRight.setTextColor(tvRightColor);
        }
        if (!TextUtils.isEmpty(strLeft)) {
            tvLeft.setText(strLeft);
        } else {
            tvLeft.setVisibility(View.GONE);
        }
        if (tvLeftColor == 0) {
            tvLeft.setTextColor(mContext.getResources().getColor(R.color.color_616161));
        } else {
            tvLeft.setTextColor(tvLeftColor);
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

    public interface InfoCallback {
        void onRight();

        void onLeft();

    }
}
