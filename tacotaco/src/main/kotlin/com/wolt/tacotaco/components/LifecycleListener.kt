package com.wolt.tacotaco.components

interface LifecycleListener {

    fun onAttach() {}

    fun onForeground() {}

    fun onBackground() {}

    fun onDeflate() {}

    fun onDetach() {}

}
