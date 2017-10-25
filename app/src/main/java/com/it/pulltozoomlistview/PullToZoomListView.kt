package com.it.pulltozoomlistview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.ListView

/**
 * Created by chendeqiang on 2017/10/25 09:42
 */
open class PullToZoomListView(context: Context, attrs: AttributeSet) : ListView(context, attrs) {
    private var mImageView: ImageView? = null
    //初始高度
    private var mImageViewHeight = -1
    //最大拉伸高度
    private val mDrawableMaxHeight = -1

    /**
     * 设置拉伸的图片
     * @param imageView
     */
    fun setPullToZoomListView(imageView: ImageView) {
        this.mImageView = imageView
        // 设置伸缩类型 -- 居中填充
        this.mImageView!!.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    /**
     * 初始化图片加载进来最初的高度

     */
    fun setViewBounds() {
        if (mImageViewHeight == -1) {
            mImageViewHeight = mImageView!!.height
            if (mImageViewHeight < 0) {
                mImageViewHeight = context.resources.getDimensionPixelOffset(R.dimen.size_default)
            }
        }
    }

    /**
     * 滑动过头的时候回调
     */
    override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {
        // 控制ImageView的高度不断增加
        val isCollapse = resizeOverScrollBy(deltaY)
        // return true:下拉到边界的某个地方的时候不再往下拉
        return if (isCollapse)
            true
        else
            super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent)
    }

    /**
     * 监听ListView滑动
     */

    private fun resizeOverScrollBy(deltaY: Int): Boolean {
        // 下拉的过程当中，不断地控制ImageView的高度
        /**
         * deltaY:是在超出滑动的时候每毫秒滑动的距离 -- 增量 (-往下拉过渡，+往上拉过渡) 大小：根据用户滑动的速度决定 一般滑动的速度
         * -50~50
         */
        if (deltaY < 0) {
            // 下拉过渡，不断增加ImageView的高度，deltay是负数，增加高度就是减去
            mImageView!!.layoutParams.height = mImageView!!.height - deltaY
            // View重新调整宽高
            mImageView!!.requestLayout() // onMeasure-->onLayout
        } else {
            // 上拉过渡，不断减小ImageView的高度，deltay是正数，减小高度还是减去
            if (mImageView!!.height > mImageViewHeight) {
                mImageView!!.layoutParams.height = Math.max(mImageView!!.height - deltaY, mImageViewHeight)
                // View重新调整宽高
                mImageView!!.requestLayout() // onMeasure-->onLayout
            }
        }
        return false
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        // 获得ImageView的父控件
        val header = mImageView!!.parent as View
        if (header.top < 0 && mImageView!!.height > mImageViewHeight) {
            // 减小ImageView的高度 -- 不能超过图片最原始的高度
            mImageView!!.layoutParams.height = Math.max(mImageView!!.height + header.top, mImageViewHeight)
            // ImageView所在的容器的高度也要变化：getTop--->0
            header.layout(header.left, 0, header.right, header.height)
            mImageView!!.requestLayout()
        }
    }

    /**
     * 监听触摸 -- 松开手
     */
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // 判断
        if (ev!!.action == MotionEvent.ACTION_UP) {
            // 开启回弹的动画
            val animation = ResetAnimation(mImageView!!, mImageViewHeight)
            animation.duration = 300
            mImageView!!.startAnimation(animation)
        }
        return super.onTouchEvent(ev)
    }

    inner class ResetAnimation(private val mView: ImageView, private val targetHeight: Int) : Animation() {
        // 动画执行前的高度
        private val originalHeight: Int
        // 高度差
        private val extraHeight: Int
        init {
            this.originalHeight = mView.height
            extraHeight = originalHeight - targetHeight
        }

        /**
         * 动画不断地执行的时候会回调该方法
         * interpolatedTime：范围是0
         * 0ms-------------->300ms
         * 当前的图片高度--->动画执行之前的高度-高度差      *interpolatedTime
         * extraHeight------>0
         */
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            mView.layoutParams.height = (originalHeight - extraHeight * interpolatedTime).toInt()
            mView.requestLayout()
            super.applyTransformation(interpolatedTime, t)
        }
    }
}