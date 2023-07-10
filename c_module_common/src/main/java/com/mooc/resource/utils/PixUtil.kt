package com.mooc.resource.utils

import android.content.res.Resources
import android.util.TypedValue

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    尺寸类工具
 * @Author:         xym
 * @CreateDate:     2020/8/17 1:37 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/17 1:37 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class PixUtil {

    companion object{
        fun dp2px(dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
        }
    }
}