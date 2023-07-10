package com.mooc.studyroom.window;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mooc.resource.ktextention.IntExtentionKt;
import com.mooc.studyroom.R;


public class ScoreEmptyView extends LinearLayout {
    private ImageView icon;
    private TextView title;

    public ScoreEmptyView(@NonNull Context context) {
        this(context, null);
    }

    public ScoreEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScoreEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int style) {
        super(context, attrs, defStyleAttr, style);
//        setOrientation(VERTICAL);
//        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.studyroom_scoure_layout_empty_view, this, true);

        title = findViewById(R.id.tv_header_title);
    }


    public void setEmptyIcon(@DrawableRes int iconRes) {
        icon.setImageResource(iconRes);
    }

    public void setTitle(String text) {
        if (TextUtils.isEmpty(text)) {
            title.setVisibility(GONE);
        } else {
            title.setText(text);
            title.setVisibility(VISIBLE);
        }

    }



    /**
     * 设置居顶
     *
     * @param topmargin 距离顶部距离
     */
    public void setGravityTop(final int topmargin) {
        post(new Runnable() {
            @Override
            public void run() {
                setGravity(Gravity.TOP);
                ((MarginLayoutParams) getLayoutParams()).setMargins(0, IntExtentionKt.dp2px(topmargin), 0, 0);
            }
        });
    }
}
