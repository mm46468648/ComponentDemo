package com.mooc.battle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.mooc.battle.R;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.battle.model.GameUserInfo;

public class GameRightUserScore extends FrameLayout {

    Context mContext;

    public GameRightUserScore(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GameRightUserScore(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameRightUserScore(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    View view;

    private void init(Context context) {
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.user_game_right_score, null, false);
        addView(view);
    }

    public void setData(GameResultResponse.GameSummary summary, GameUserInfo userInfo) {
        TextView tvName = view.findViewById(R.id.name);
        ImageView header = view.findViewById(R.id.img_user_header);

        if (userInfo != null) {
            tvName.setText(userInfo.nickname);
            Glide.with(mContext)
                    .load(userInfo.cover)
                    .placeholder(R.mipmap.common_ic_user_head_default)
                    .error(R.mipmap.common_ic_user_head_default)
                    .into(header);
        }

        TextView tvScore = view.findViewById(R.id.tv_score);
        tvScore.setText(summary.total_score + "");

        TextView tvRightNum = view.findViewById(R.id.tv_answer_right_num);

        if (summary.answer_streak <= 1) {
            tvRightNum.setVisibility(View.INVISIBLE);
        } else {
            tvRightNum.setVisibility(View.VISIBLE);
            tvRightNum.setText("连续答对" + summary.answer_streak + "题");
        }


    }
}
