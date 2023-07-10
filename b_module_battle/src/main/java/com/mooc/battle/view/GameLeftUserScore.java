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
import com.mooc.battle.GameConstant;
import com.mooc.battle.R;
import com.mooc.battle.model.GameResultResponse;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.model.UserInfo;

public class GameLeftUserScore extends FrameLayout {

    Context mContext;

    public GameLeftUserScore(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GameLeftUserScore(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameLeftUserScore(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    View view;

    private void init(Context context) {
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.user_game_left_score, null, false);
        addView(view);
    }


    public void setData(GameResultResponse.GameSummary summary) {
//        UserBean userBean = SPUserUtils.getInstance().getUserInfo();

        UserInfo userBean = GlobalsUserManager.INSTANCE.getUserInfo();
        TextView tvName = view.findViewById(R.id.name);
        ImageView header = view.findViewById(R.id.img_user_header);

        tvName.setText(GameConstant.getSpecialFormatName(userBean.getName()));
        Glide.with(mContext)
                .load(userBean.getAvatar())
                .placeholder(R.mipmap.common_ic_user_head_default)
                .error(R.mipmap.common_ic_user_head_default)
                .into(header);


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
