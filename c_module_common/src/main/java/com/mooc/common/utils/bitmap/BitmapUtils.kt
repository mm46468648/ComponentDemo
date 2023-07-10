package com.mooc.common.utils.bitmap

import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import com.mooc.common.ktextends.dp2px
import java.io.ByteArrayOutputStream
import java.io.IOException


class BitmapUtils {

    companion object {
        private var mDesiredWidth: Int = 0
        private var mDesiredHeight: Int = 0

        /**
         * bitmap转换成数组
         */
        fun bmpToByteArray(bmp: Bitmap): ByteArray {
            val output = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, output)
            //回收bitmap
            bmp.recycle()
            val result = output.toByteArray()
            try {
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        /**
         * 从SD卡上加载图片,
         * 并对宽高进行压缩
         */
        fun compressBitmapFromFileByWidth(pathName: String, reqWidth: Int, reqHeight: Int): Bitmap {
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(pathName, options)
            options = getBestOptions(options, reqWidth, reqHeight)
            val src = BitmapFactory.decodeFile(pathName, options)
            return createScaleBitmap(src, mDesiredWidth, mDesiredHeight)
        }

        /**
         * 微信纯图分享缩略图，按比例压缩一
         */
        fun compressPicturethumbData(frame: Bitmap): ByteArray?{
            val matrix = Matrix()
            matrix.setScale(0.5f, 0.5f)
            val mSrcBitmap = Bitmap.createBitmap(frame, 0, 0, frame.width, frame.height, matrix, true)
            return compressBitmapToSizeByOption(mSrcBitmap,32)
        }

        /**
         * 对Bitmap
         * 根据质量进行循环压缩
         * 直到满足需要大小
         */
        fun compressBitmapToSizeByOption(frame: Bitmap?, limit: Int): ByteArray? {
            if (frame != null && limit > 0) {
                var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
                var options = 100
                frame.compress(Bitmap.CompressFormat.JPEG, options, baos)
                while (baos!!.toByteArray().size > limit * 1024) {
                    baos.reset()
                    options -= 5
                    if(options < 0){
                        options = 0
                    }
                    frame.compress(Bitmap.CompressFormat.JPEG, options, baos)
                }
                val bytes = baos.toByteArray()
                if (baos != null) {
                    try {
                        baos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    baos = null
                }
                return bytes
            }
            return null
        }

        /**
         * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
         *
         * @param tempBitmap
         * @param desiredWidth
         * @param desiredHeight
         * @return
         */
        private fun createScaleBitmap(
            tempBitmap: Bitmap,
            desiredWidth: Int,
            desiredHeight: Int
        ): Bitmap {
            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.width > desiredWidth || tempBitmap.height > desiredHeight)) {
                // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
                val bitmap =
                    Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true)
                tempBitmap.recycle() // 释放Bitmap的native像素数组
                return bitmap
            } else {
                return tempBitmap // 如果没有缩放，那么不回收
            }
        }

        /**
         * 计算目标宽度，目标高度，inSampleSize
         *
         * @return BitmapFactory.Options对象
         */
        private fun getBestOptions(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): BitmapFactory.Options {
            // 读取图片长宽
            val actualWidth = options.outWidth
            val actualHeight = options.outHeight
            // Then compute the dimensions we would ideally like to decode to.
            mDesiredWidth = getResizedDimension(reqWidth, reqHeight, actualWidth, actualHeight)
            mDesiredHeight = getResizedDimension(reqHeight, reqWidth, actualHeight, actualWidth)
            // 根据现在得到计算inSampleSize
            options.inSampleSize =
                calculateBestInSampleSize(actualWidth, actualHeight, mDesiredWidth, mDesiredHeight)
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false
            return options
        }

        /**
         * 最终得到重新测量的尺寸
         *
         * @param maxPrimary
         * @param maxSecondary
         * @param actualPrimary
         * @param actualSecondary
         * @return
         */
        private fun getResizedDimension(
            maxPrimary: Int,
            maxSecondary: Int,
            actualPrimary: Int,
            actualSecondary: Int
        ): Int {
            val ratio = actualSecondary.toDouble() / actualPrimary.toDouble()
            var resized = maxPrimary
            if (resized * ratio > maxSecondary) {
                resized = (maxSecondary / ratio).toInt()
            }
            return resized
        }

        /**
         * 计算最佳尺寸
         *
         * @param actualWidth
         * @param actualHeight
         * @param desiredWidth
         * @param desiredHeight
         * @return
         */
        private fun calculateBestInSampleSize(
            actualWidth: Int,
            actualHeight: Int,
            desiredWidth: Int,
            desiredHeight: Int
        ): Int {
            val wr = actualWidth.toDouble() / desiredWidth
            val hr = actualHeight.toDouble() / desiredHeight
            val ratio = Math.min(wr, hr)
            var inSampleSize = 1.0f
            while (inSampleSize * 2 <= ratio) {
                inSampleSize *= 2f
            }

            return inSampleSize.toInt()
        }


        /**
         * 将一个未show的布局转换成bitmap
         */
        fun createUnShowBitmapFromLayout(v: View): Bitmap {
            v.measure(
                View.MeasureSpec.makeMeasureSpec(360.dp2px().toInt(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            v.layout(0, 0, v.measuredWidth, v.measuredHeight)
            val bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888)
            v.draw(Canvas(bitmap))
            return bitmap
        }

        /**
         * 通过一个已show的布局创建bitmap
         */
        fun createShowedBitmap(
            window: Window,
            targetView: View,
            getCacheResult: (bitmap: Bitmap) -> Unit
        ) {
            targetView.measure(
                View.MeasureSpec.makeMeasureSpec(360.dp2px().toInt(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            targetView.layout(0, 0, targetView.measuredWidth, targetView.measuredHeight)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //准备一个bitmap对象，用来将copy出来的区域绘制到此对象中
                val bitmap = Bitmap.createBitmap(
                    targetView.getWidth(),
                    targetView.getHeight(),
                    Bitmap.Config.ARGB_8888
                )
                //获取layout的left-top顶点位置
                val location = IntArray(2)
                targetView.getLocationInWindow(location)
                //请求转换
                PixelCopy.request(
                    window,
                    Rect(
                        location[0], location[1],
                        location[0] + targetView.getWidth(), location[1] + targetView.getHeight()
                    ),
                    bitmap, PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                        //如果成功
                        if (copyResult == PixelCopy.SUCCESS) { //方法回调
                            getCacheResult.invoke(bitmap)
                        }
                    }, Handler(Looper.getMainLooper())
                )
            } else { //开启DrawingCache
                targetView.setDrawingCacheEnabled(true)
                //构建开启DrawingCache
                targetView.buildDrawingCache()
                //获取Bitmap
                val drawingCache: Bitmap = targetView.getDrawingCache()
                //方法回调
                getCacheResult.invoke(drawingCache)
                //销毁DrawingCache
                targetView.destroyDrawingCache()
            }
        }


        /**
         * bitmap转换成圆角矩形bitmap方法
         * @param bitmap
         * @param roundPx,一般设置成14
         * @return Bitmap
         * @author caizhiming
         */
        fun convertRoundBitmap(bitmap: Bitmap, roundPx: Int): Bitmap? {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
//        final int color = 0xff424242;
            //   final int color = 0x7f0b000c;
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            paint.setAntiAlias(true)
            canvas.drawARGB(0, 0, 0, 0)
            //     paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx.toFloat(), roundPx.toFloat(), paint)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        /*** 转换图片成圆形

         *@parambitmap

         * 传入Bitmap对象

         *@return

         */

        open fun makeRoundCorner(bitmap: Bitmap): Bitmap? {
            val width = bitmap.width
            val height = bitmap.height
            var left = 0
            var top = 0
            var right = width
            var bottom = height
            var roundPx = (height / 2).toFloat()
            if (width > height) {
                left = (width - height) / 2
                top = 0
                right = left + height
                bottom = height
            } else if (height > width) {
                left = 0
                top = (height - width) / 2
                right = width
                bottom = top + width
                roundPx = (width / 2).toFloat()
            }
            val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(left, top, right, bottom)
            val rectF = RectF(rect)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        /**
         * 按比例缩放图片
         *
         * @param origin 原图
         * @param ratio  比例
         * @return 新的bitmap
         */
         fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
            if (origin == null) {
                return null
            }
            val width = origin.width
            val height = origin.height
            val matrix = Matrix()
            matrix.preScale(ratio, ratio)
            val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
            if (newBM == origin) {
                return newBM
            }
//        origin.recycle()
            return newBM
        }
    }
}