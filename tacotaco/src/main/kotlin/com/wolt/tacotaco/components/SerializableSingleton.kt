package com.wolt.tacotaco.components

import java.io.Serializable

interface SerializableSingleton {

    fun saveState(): Serializable

    fun restoreState(state: Serializable)

}
