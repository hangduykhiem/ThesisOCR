package com.wolt.tacotaco.components

class LifecycleProvider {

    private val listeners = mutableListOf<LifecycleListener>()
    var foreground: Boolean = false
        private set

    fun addListener(listener: LifecycleListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: LifecycleListener) {
        listeners.remove(listener)
    }

    fun addListener(
            onAttach: (() -> Unit)? = null,
            onResume: (() -> Unit)? = null,
            onPause: (() -> Unit)? = null,
            onDeflate: (() -> Unit)? = null,
            onDetach: (() -> Unit)? = null
    ) {
        addListener(object : LifecycleListener {
            override fun onAttach() {
                onAttach?.invoke()
            }

            override fun onForeground() {
                onResume?.invoke()
            }

            override fun onBackground() {
                onPause?.invoke()
            }

            override fun onDeflate() {
                onDeflate?.invoke()
            }

            override fun onDetach() {
                onDetach?.invoke()
            }
        })
    }

    // TODO: Make all the methods below internal after removing BaseController

    fun onAttach() {
        listeners.forEach { it.onAttach() }
    }

    fun onForeground() {
        foreground = true
        listeners.forEach { it.onForeground() }
    }

    fun onBackground() {
        foreground = false
        listeners.forEach { it.onBackground() }
    }

    fun onDeflate() {
        listeners.forEach { it.onDeflate() }
    }

    fun onDetach() {
        listeners.forEach { it.onDetach() }
    }

}
