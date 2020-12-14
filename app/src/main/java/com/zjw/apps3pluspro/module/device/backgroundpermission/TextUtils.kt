package com.zjw.apps3pluspro.module.device.backgroundpermission

import android.os.Build
import android.text.Html
import android.widget.TextView

object TextUtils {
    fun showText(textView: TextView, text1: String, text2: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml("$text1<b><tt>$text2</tt></b>", Html.FROM_HTML_MODE_LEGACY)
        } else {
            textView.text = Html.fromHtml("$text1<b><tt>$text2</tt></b>")
        }
    }
}