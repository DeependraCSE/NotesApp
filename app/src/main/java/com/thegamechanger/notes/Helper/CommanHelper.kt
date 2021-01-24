package com.thegamechanger.notes.Helper
import android.widget.EditText

class CommanHelper {
    companion object {
        fun isEmptyEditText(editText: EditText?): Boolean {
            val s = editText?.text.toString().trim { it <= ' ' }
            if (s.isEmpty()) {
                editText?.error = "Empty Field Not Allow"
                editText?.requestFocus()
                return true
            }
            return false
        }
    }
}