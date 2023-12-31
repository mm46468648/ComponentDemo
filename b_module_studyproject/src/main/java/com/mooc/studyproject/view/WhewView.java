package com.mooc.studyproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 模仿咻一咻
 *
 * @author LGL
 */
public class WhewView extends View {
    private Paint paint;
    private int maxWidth = 900;
    // 是否运行
    private boolean isStarting = false;
    private List<String> alphaList = new ArrayList<>();
    private List<String> startWidthList = new ArrayList<>();

    public WhewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WhewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public WhewView(Context context) {
        super(context);
        init();
    }

    float centerY = 0;

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    private void init() {
        paint = new Paint();
// 设置博文的颜色
        paint.setColor(0x10955b);
        alphaList.add("255");// 圆心的不透明度
        startWidthList.add("0");
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);// 颜色：完全透明
// 依次绘制 同心圆
        for (int i = 0; i < alphaList.size(); i++) {
            int alpha = Integer.parseInt(alphaList.get(i));
// 圆半径
            int startWidth = Integer.parseInt(startWidthList.get(i));
            paint.setAlpha(alpha);
// 这个半径决定你想要多大的扩散面积
            canvas.drawCircle(getWidth() / 2, centerY, startWidth + 300,
                    paint);
// 同心圆扩散
            if (isStarting && alpha > 0 && startWidth < maxWidth) {
                alphaList.set(i, (alpha - 1) + "");
                startWidthList.set(i, (startWidth + 1) + "");
            }
        }
        if (isStarting
                && Integer
                .parseInt(startWidthList.get(startWidthList.size() - 1)) == maxWidth / 10) {
            alphaList.add("255");
            startWidthList.add("0");
        }
// 同心圆数量达到10个，删除最外层圆
        if (isStarting && startWidthList.size() == 30) {
            startWidthList.remove(0);
            alphaList.remove(0);
        }
// 刷新界面
        invalidate();
    }

    // 执行动画
    public void start() {
        isStarting = true;
    }

    // 停止动画
    public void stop() {
        isStarting = false;
    }

    // 判断是都在不在执行
    public boolean isStarting() {
        return isStarting;
    }
}