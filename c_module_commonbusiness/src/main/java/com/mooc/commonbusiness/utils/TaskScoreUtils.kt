package com.mooc.commonbusiness.utils

import android.text.SpannableString
import android.text.TextUtils
import com.mooc.common.dsl.spannableString

/**

 * @Author limeng
 * @Date 2023/3/24-10:01 上午
 */
class TaskScoreUtils {
    companion object {

        fun getSpaString(
            success_score: String?,
            startColor: Int,
            endColor: Int,
            startSize: Int,
            endSize: Int
        ): SpannableString {
            val scoreStr: String
            if (TextUtils.isEmpty(success_score)) {
                scoreStr = "奖励积分 0"
            } else {
                scoreStr = "奖励积分 " + success_score
            }
            val spannableString = spannableString {
                str = scoreStr
                colorSpan {
                    color = startColor
                    start = 0
                    end = 4
                }
                colorSpan {
                    color = endColor
                    start = 4
                    end = scoreStr.length
                }
                absoluteSpan {
                    size = startSize
                    start = 0
                    end = 4
                }
                absoluteSpan {
                    size = endSize
                    start = 4
                    end = scoreStr.length
                }
            }
            return spannableString
        }
    }
}