package com.kcube.cubite.com.kcube.cubite

import android.text.InputFilter
import android.text.Spanned

class AlphanumericInputFilter: InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int,
    ): String? {
        // Define a regular expression pattern to match alphanumeric characters
        val pattern = "[a-zA-Z0-9]+".toRegex()

        // Check if the entered text matches the pattern
        if (source != null && !pattern.matches(source)) {
            // If not, return an empty string to prevent the character from being entered
            return ""
        }

        // If the input is valid, allow it
        return null
    }

}