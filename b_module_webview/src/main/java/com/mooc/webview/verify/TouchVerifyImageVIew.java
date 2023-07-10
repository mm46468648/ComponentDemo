package com.mooc.webview.verify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.mooc.common.utils.ScreenUtil;
import com.mooc.webview.R;
import com.mooc.webview.interfaces.VerifyArticleCallBack;
import com.mooc.webview.model.Point;

import java.util.ArrayList;

public class TouchVerifyImageVIew extends View {

    int touchCount = 0;

    int width = 0;
    int height = 0;
    int id;

    VerifyArticleCallBack callBack;

    public VerifyArticleCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(VerifyArticleCallBack callBack) {
        this.callBack = callBack;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    ArrayList<Point> points;
    ArrayList<Point> drawablePoints;

    int topScale=30;

    public ArrayList<Point> getPoints() {
        return points;
    }

    public ArrayList<Point> getDrawablePoints() {
        return drawablePoints;
    }

    Paint paint = new Paint();
    Bitmap bitmap;

    public TouchVerifyImageVIew(Context context) {
        super(context);
        init(context);
    }

    public TouchVerifyImageVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TouchVerifyImageVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void setTextSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);// 给白纸设置宽高

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float ratioWidth = (float) screenWidth / 480;
        float ratioHeight = (float) screenHeight / 800;

        float RATIO = Math.min(ratioWidth, ratioHeight);


        int textSize = Math.round(20 * RATIO);
        topScale=(int)(10*RATIO);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(textSize);
    }

    private void init(Context context) {
        setTextSize(context);
        points = new ArrayList<>();
        drawablePoints = new ArrayList<>();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_web_verify_touch);
        paint.setColor(getContext().getResources().getColor(R.color.color_white));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (drawablePoints.size() > 0) {
            for (int i = 0; i < drawablePoints.size(); i++) {
                canvas.drawBitmap(bitmap, drawablePoints.get(i).getX() - bitmap.getWidth() / 2, drawablePoints.get(i).getY() - bitmap.getHeight() / 2-topScale, paint);
                canvas.drawText(i + 1 + "", drawablePoints.get(i).getX() - bitmap.getWidth() / 7, drawablePoints.get(i).getY() + bitmap.getHeight() / 6-topScale, paint);
            }
        }
        super.onDraw(canvas);


    }

    boolean setTag = false;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        Log.i("TAG", "width:->" + width + "  height:->" + height);

        if (width != 0 && !setTag) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width * 14 / 24;
            setLayoutParams(layoutParams);
            setTag = true;
            Log.i("TAG", "set  lp  width:->" + width + "  height:->" + layoutParams.height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchCount++;

            Log.i("TAG", "X:->" + event.getX() + "   Y:->" + event.getY());

            float withPercent = getWithPercent(event.getX());
            float heightPercent = getHeightPercent(event.getY());
            Log.i("TAG", "X:->" + withPercent + "   Y:->" + heightPercent);

            if (points.size() < 4) {
                Point point = new Point();
                point.setX(withPercent);
                point.setY(heightPercent);
                points.add(point);

                Point drPoint = new Point();
                drPoint.setX((int) event.getX());
                drPoint.setY((int) event.getY());
                drawablePoints.add(drPoint);
                postInvalidate();
            }
            if(points.size() >=4){
                //大于等于4的时候将points的数据,回调出去自动提交
                if(callBack!=null){
//                    callBack.postVerifyCode(points);
                    callBack.postVerifyCode(id,points);
                }
            }


        }

        return super.onTouchEvent(event);
    }

    private float getWithPercent(float x) {
        if (width != 0) {
            float i = (x / width) * 100;
            return i;
        }
        return 0;
    }

    private float getHeightPercent(float y) {
        if (height != 0) {
            float i = (y / height) * 100;
            return i;
        }
        return 0;
    }


}
