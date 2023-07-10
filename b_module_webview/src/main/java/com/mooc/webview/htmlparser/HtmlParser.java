package com.mooc.webview.htmlparser;

import android.util.Log;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

public class HtmlParser {

    public static String getText(String url) throws ParserException {
        StringBean sb = new StringBean();

        //设置不需要得到页面所包含的链接信息
        sb.setLinks(false);
        //设置将不间断空格由正规空格所替代
        sb.setReplaceNonBreakingSpaces(true);
        //设置将一序列空格由一个单一空格所代替
        sb.setCollapse(true);
        //传入要解析的URL
        sb.setURL(url);
        Parser parser = new Parser(url);
        parser.setEncoding("UTF-8");
        parser.visitAllNodesWith(sb);

        //返回解析后的网页纯文本信息
        String str = sb.getStrings();

        str = str.replaceAll("[\\s*|\t|\r|\n]", "");  // 去除所有空格，制表符
        //只保留汉字
        str = str.replaceAll("[^\u4E00-\u9FA5]", "");
//       String[] splits= str.split("n&&");
//       str=splits[0];

//        Log.i("huang", str);
        Log.i("huang", "长度：" + str.length());

        return str;
    }


}
