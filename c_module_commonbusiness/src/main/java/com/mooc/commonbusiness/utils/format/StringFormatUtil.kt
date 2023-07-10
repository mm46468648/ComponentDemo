package com.mooc.commonbusiness.utils.format

import android.annotation.SuppressLint
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    格式化一些数据
 * @Author:         xym
 * @CreateDate:     2020/8/18 3:22 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/18 3:22 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class StringFormatUtil {

    companion object {
        /**
         * 格式化数字
         * （k，w，kw）
         */
        @JvmStatic
        fun formatNum(num: Int): String = when {
            num in 1000 until 10000 -> "${num / 1000}k"

            num in 10000 until 1 * 1000 * 10000 -> "${num / 10000}w"

            num >= 1 * 1000 * 10000 -> "${num / (1000 * 10000)}kw"

            else -> num.toString()
        }

        /**
         * 格式化粉丝和关注数字
         * 规则:10000以下直接显示准确数字，10000（包含）以上显示X.XK显示
         * （k，w，kw）
         */
        @JvmStatic
        fun formatFansNum(num: Int): String = when {
            num in 10000 until 1 * 1000 * 10000 -> "${getNoMoreThanTwoDigits((num / 1000f).toDouble())}k"

            num >= 1 * 1000 * 10000 -> "${num / (1000 * 10000)}kw"

            else -> num.toString()
        }

        fun getNoMoreThanTwoDigits(number: Double): String {
            val scale = 1;//设置尾数
            val roundingMode = BigDecimal.ROUND_DOWN;//表示四舍五入，可以选择其他的舍值方式，例如去位等等
            var b = BigDecimal(number);
            b = b.setScale(scale, roundingMode);

            return b.toString()
        }

        /*
     * 格式化音频播放次数  最大单位:万
     * */
        @JvmStatic
        fun formatPlayCount(count: Int): String? {
            var str = ""
            val n = count.toString()
            val df = DecimalFormat("#.0")
            if (n.length > 4) {
                val num = count.toDouble() / 10000
                str = df.format(num) + "万"
            } else if (n.length == 4) {
                val num = count.toDouble() / 1000
                str = df.format(num) + "千"
            } else if (n.length <= 3) {
                str = df.format(count.toLong())
            }
            return if (str == ".0") {
                "0"
            }else if(str.endsWith(".0")){
                str.replace(".0","")
            } else str
        }

        /**
         * 格式化播放数量
         */
        @JvmStatic
        fun formatPlayCount(count: Long): String {
            var str = ""
            val n = count.toString()
            val df = DecimalFormat("#.0")
            if (n.length > 4) {
                val num = count.toDouble() / 10000
                str = df.format(num) + "万"
            } else if (n.length == 4) {
                val num = count.toDouble() / 1000
                str = df.format(num) + "千"
            } else if (n.length <= 3) {
                str = df.format(count)
            }
            return if (str == ".0") {
                "0"
            }else if(str.endsWith(".0")){
                str.replace(".0","")
            } else str
        }

        /**
         * 格式化姓名
         *
         */
        @JvmStatic
        fun getStrUserName(name: String): String {
            if (name.isEmpty()) return name
            if (name.length > 5) {
                return name.substring(0, 3) + "****" + name.substring(name.length - 2)
            }
            return name
        }
        @JvmStatic
        fun timeToString(time: Long?): String? {
             if (time != null) {
                 @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy.MM.dd")
                 return dateFormat.format(time * 1000)
             }else{
                 return ""
             }

        }

        /**
         * 格式固定为  yyyy.MM.dd HH:mm
         * @param time
         * @param
         * @return
         */
        fun infoTimeToString(time: Long): String? {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
            return dateFormat.format(Date(time * 1000))
        }

        /**
         * 超过多少替换成...
         */
        fun elipsizeEnd(string: String,length:Int) : String{
            var newName: String = string
            if (string.length > length) {
                val s: String = string.substring(0, length - 1)
                newName = "$s..."
            }
            return newName
        }
    }


}