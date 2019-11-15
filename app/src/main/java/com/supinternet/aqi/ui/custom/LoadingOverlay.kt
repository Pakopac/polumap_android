package com.supinternet.aqi.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar

class LoadingOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    init {
        setBackgroundColor(Color.parseColor("#AAFFFFFF"))

        val progressBar = ProgressBar(context)
        addView(
            progressBar, LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (visibility != View.VISIBLE) {
            super.onInterceptTouchEvent(ev)
        } else {
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (visibility != View.VISIBLE) {
            super.onTouchEvent(event)
        } else {
            true
        }
    }
}