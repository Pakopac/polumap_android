package com.supinternet.aqi.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

class MapsContentGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        setPadding(
            0,
            resources.getDimensionPixelSize(
                resources.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android"
                )
            ),
            0,
            0
        )
    }

}