package com.wolt.tacotaco.components

import android.animation.Animator
import com.wolt.tacotaco.Controller

interface TransitionAnimation {

    fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator

    fun exitViewOnTop() = false

}