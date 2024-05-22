package com.royputra.submissionstory.view.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.royputra.submissionstory.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError(context.getString(R.string.passMustbe), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing.
            }
        })
    }
}