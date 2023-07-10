package com.mooc.resource.widget.tagtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import com.mooc.common.R;;

import java.util.ArrayList;
import java.util.List;

/**
 * 带标签的textview
 * 利用富文本实现
 */
public class TagTextView extends AppCompatTextView {

    public static int TAGS_INDEX_AT_START = 0;
    public static int TAGS_INDEX_AT_END = 1;

    private int tagsBorderRes = R.drawable.shape_radius2_stoke1primary_solidf2fdf8;
    private Context mContext;

    private Drawable tagsBgDrawable = null;
    private int tagTextSize = 10;       //  标签的字体大小
    private int tagTextColor = this.getResources().getColor(R.color.colorPrimary);    //   标签的字体颜色

    private StringBuffer content_buffer;
    private TextView tv_tag;
    private int borderPadding;  //border两边的间距
    private int tagsIndex = 0;  //  默认标签在开始的位置

    public TagTextView(Context context) {
        this(context,null);
    }

    public TagTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TagTextView);
        tagsBorderRes = array.getInt(R.styleable.TagTextView_ttv_border_res, R.drawable.shape_radius2_stoke1primary_solidf2fdf8);
        tagTextColor = array.getInt(R.styleable.TagTextView_ttv_text_color, tagTextColor);
        borderPadding = (int) array.getDimension(R.styleable.TagTextView_ttv_border_padding,dp2px(context,4));

        array.recycle();
        mContext = context;
    }

    /**
     * 设置标签的背景样式
     *
     * @param tagTextSize 你需要替换的tag文字字体大小
     */
    public void setTagTextSize(int tagTextSize) {
        this.tagTextSize = tagTextSize;
    }

    /**
     * 设置标签的背景样式
     *
     * @param tagTextColor 你需要替换的tag的文字颜色
     */
    public void setTagTextColor(int tagTextColor) {
        this.tagTextColor = tagTextColor;
    }

    /**
     * 设置标签的背景样式
     *
     * @param tagsBorderRes 你需要替换的tag背景样式
     */
    public void setTagsBorderRes(int tagsBorderRes) {
        this.tagsBorderRes = tagsBorderRes;
    }

    /**
     * 设置标签是在头部还是尾部
     *
     * @param tagsIndex 头部还是尾部显示tag
     */
    public void setTagsIndex(int tagsIndex) {
        this.tagsIndex = tagsIndex;
    }

    /**
     * 设置标签和文字内容(单个)
     *
     * @param tag     标签内容
     * @param content 标签文字
     */
    public void setSingleTagAndContent(@NonNull String tag, String content) {
        List<String> tagList = new ArrayList<>();
        tagList.add(tag);
        setMultiTagAndContent(tagList, content);
    }

    /**
     * 设置标签和文字内容(多个)
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    public void setMultiTagAndContent(@NonNull List<String> tags, String content) {
        if (tagsIndex == TAGS_INDEX_AT_START) {
            setTagStart(tags, content);
        } else {
            setTagEnd(tags, content);
        }
    }

    /**
     * 标签显示在头部位置
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    public void setTagStart(List<String> tags, String content) {
        int endIndex = 0;
        int startIndex = 1;
        content_buffer = new StringBuffer();
        for (String item : tags) {
            content_buffer.append(item);
        }
        content_buffer.append(content);
        SpannableString spannableString = new SpannableString(content_buffer);
        for (int i = 0; i < tags.size(); i++) {
            String item = tags.get(i);
            endIndex += item.length();
            //  设置标签的布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.common_view_tagtext, null);
            tv_tag = view.findViewById(R.id.tv_tags);
            tv_tag.setPadding(borderPadding,0,borderPadding,0);
            tv_tag.setText(item);
            tv_tag.setTextSize(tagTextSize);
            tv_tag.setTextColor(tagTextColor);
            //  设置背景样式
            tv_tag.setBackgroundResource(tagsBorderRes);

            if(tagsBgDrawable!=null){
                tv_tag.setBackground(tagsBgDrawable);
            }
            Bitmap bitmap = convertViewToBitmap(view);
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, tv_tag.getWidth(), tv_tag.getHeight());

            CenterImageSpan span = new CenterImageSpan(drawable);
            spannableString.setSpan(span, startIndex - 1, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            startIndex += item.length();
        }
        setText(spannableString);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 设置图片
     *
     * @param resID   资源ID
     * @param content 文字内容
     */
    public void setTagImageStart(Context context, int resID, String content, int width, int height) {
        content_buffer = new StringBuffer("**" + content);  //  两个字符占位
        SpannableString spannableString = new SpannableString(content_buffer);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resID);
        Drawable drawable = new BitmapDrawable(bitmap);
        drawable.setBounds(0, 0, dp2px(context, width), dp2px(context, height));
        CenterImageSpan span = new CenterImageSpan(drawable);
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setText(spannableString);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 标签显示在头部位置
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    public void setTagEnd(List<String> tags, String content) {
        content_buffer = new StringBuffer(content);
        for (String item : tags) {
            content_buffer.append(item);
        }
        SpannableString spannableString = new SpannableString(content_buffer);
        for (int i = 0; i < tags.size(); i++) {
            String item = tags.get(i);
            //  设置标签的布局
            View view = LayoutInflater.from(mContext).inflate(R.layout.common_view_tagtext, null);
            tv_tag = view.findViewById(R.id.tv_tags);
            tv_tag.setText(item);
            tv_tag.setTextSize(tagTextSize);
            tv_tag.setTextColor(tagTextColor);
            //  设置背景样式
            tv_tag.setBackgroundResource(tagsBorderRes);

            if(tagsBgDrawable!=null){
                tv_tag.setBackground(tagsBgDrawable);
            }
            Bitmap bitmap = convertViewToBitmap(view);
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, tv_tag.getWidth(), tv_tag.getHeight());

            CenterImageSpan span = new CenterImageSpan(drawable);
            spannableString.setSpan(span, content_buffer.length() - item.length(), content_buffer.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        setText(spannableString);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 指定位置设置标签
     *
     * @param start   开始位置从0开始
     * @param end     结束位置长度-1
     * @param content 文字内容
     */
    public void setTagAnyway(int start, int end, String content) {
        SpannableString spannableString = new SpannableString(content);
        //  设置标签的布局
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_view_tagtext, null);
        String item = content.substring(start, end);
        tv_tag = view.findViewById(R.id.tv_tags);
        tv_tag.setText(item);
        tv_tag.setTextSize(tagTextSize);
        tv_tag.setTextColor(tagTextColor);
        //  设置背景样式
        tv_tag.setBackgroundResource(tagsBorderRes);

        Bitmap bitmap = convertViewToBitmap(view);
        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        drawable.setBounds(0, 0, tv_tag.getWidth(), tv_tag.getHeight());

        CenterImageSpan span = new CenterImageSpan(drawable);
        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        setText(spannableString);
        setGravity(Gravity.CENTER_VERTICAL);
    }


    public void setTagsBgDrawable(Drawable tagsBgDrawable) {
        this.tagsBgDrawable = tagsBgDrawable;
    }

    /**
     * Android中dp和pix互相转化
     *
     * @param dpValue dp值
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static Bitmap convertViewToBitmap(View view) {
        //        view.isDrawingCacheEnabled = true
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }
}


