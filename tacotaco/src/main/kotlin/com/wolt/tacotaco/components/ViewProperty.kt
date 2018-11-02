package com.wolt.tacotaco.components

import kotlin.reflect.KProperty

class ViewProperty<out V>(
        private val initBlock: () -> V
) {

    private var view: V? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): V {
        if (view == null) {
            view = initBlock.invoke()
        }
        return view!!
    }

    fun unbind() {
        view = null
    }

}
