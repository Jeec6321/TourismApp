package com.android.tourismapp.Ui.Elements

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class Button (var tvText: TextView, var loadingIcon: ProgressBar) {

    fun setState(state: State){
        when(state){
            State.LOADING -> {
                tvText.visibility = View.GONE
                loadingIcon.visibility = View.VISIBLE
            }

            State.ENABLE -> {
                tvText.visibility = View.VISIBLE
                loadingIcon.visibility = View.GONE
            }
        }
    }

    fun setText(text: String) {
        tvText.text = text
    }

}

enum class State{
    LOADING,
    ENABLE
}