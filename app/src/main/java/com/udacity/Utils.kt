package com.udacity

import android.content.res.Resources

class Utils {
    companion object {
        fun dp2px(resource: Resources, dp: Float): Float {
            var scale = resource.displayMetrics.density
            return dp * scale;
        }

        fun sp2px(resource: Resources, sp: Float): Float {
            var scale = resource.displayMetrics.scaledDensity
            return sp * scale
        }
    }

}