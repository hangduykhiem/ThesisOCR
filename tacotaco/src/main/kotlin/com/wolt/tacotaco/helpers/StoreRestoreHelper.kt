package com.wolt.tacotaco.helpers

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.Controller.LifecycleStage.TopDeflated
import com.wolt.tacotaco.TacoTaco
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model
import com.wolt.tacotaco.components.SerializableSingleton
import org.nustaq.serialization.FSTClassInstantiator
import org.nustaq.serialization.FSTConfiguration
import org.nustaq.serialization.FSTObjenesisInstantiator
import org.nustaq.serialization.serializers.FSTMapSerializer
import org.objenesis.ObjenesisStd
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

internal object StoreRestoreHelper {

    private const val SERIALIZABLE_STATE_FILE = "tacoTacoSerializableState"
    private const val KEY_PARCELABLE_STATE = "tacoTacoParcelableState"
    private const val KEY_HIERARCHY_STATE = "hierarchyState"
    private const val KEY_CLIENT_STATE = "clientState"
    private const val KEY_BACKSTACK_STATES = "backstacks"

    private val fstConfiguration = CustomFstConfig()

    fun storeRoot(
            context: Context,
            singletons: List<SerializableSingleton>,
            controller: Controller<*, *>,
            outState: Bundle
    ) {
        val serializableState = RootSerializableState(
                singletonStates = singletons.map { it.saveState() },
                controllerState = composeSerializableState(controller)
        )
        val bytes = fstConfiguration.asByteArray(serializableState)
        context.openFileOutput(SERIALIZABLE_STATE_FILE, 0).use { it.write(bytes) }
        val parcelableState = composeParcelableState(controller)
        outState.putBundle(KEY_PARCELABLE_STATE, parcelableState)
    }

    private fun composeSerializableState(controller: Controller<*, *>): SerializableState {
        with(controller) {
            val backstackStates = backstacksInternal.mapValues { (_, backstack) ->
                backstack.map { composeSerializableState(it) }
            }
            return SerializableState(
                    controllerClass = javaClass.name,
                    args = args,
                    model = model,
                    backstackStates = backstackStates
            )
        }
    }

    private fun composeParcelableState(controller: Controller<*, *>): Bundle {
        val state = Bundle()
        controller.view?.let { view ->
            if (view.parent == null) {
                controller.saveViewHierarchyState()
            }
            controller.saveViewClientState()
        }
        state.putSparseParcelableArray(KEY_HIERARCHY_STATE, controller.savedHierarchyState)
        state.putBundle(KEY_CLIENT_STATE, controller.savedClientState)

        val backstackStates = SparseArray<BundleList>(controller.backstacksInternal.size)
        controller.backstacksInternal.forEach { (id, children) ->
            val states = children.map { composeParcelableState(it) }
            backstackStates.put(id, BundleList(states))
        }
        state.putSparseParcelableArray(KEY_BACKSTACK_STATES, backstackStates)
        return state
    }

    fun restoreRoot(
            activity: Activity,
            singletons: List<SerializableSingleton>,
            savedInstanceState: Bundle
    ): Controller<*, *> {
        val bytes = activity.openFileInput(SERIALIZABLE_STATE_FILE).use { it.readBytes() }
        val serializableState = fstConfiguration.asObject(bytes) as RootSerializableState
        val parcelableState = savedInstanceState.getBundle(KEY_PARCELABLE_STATE)
        singletons.zip(serializableState.singletonStates).forEach { it.first.restoreState(it.second) }
        val controller = restoreStep1(serializableState.controllerState)
        controller.activityInternal = activity
        LifecycleConductor.changeStage(controller, TopDeflated)
        restoreStep2(controller, serializableState.controllerState, parcelableState)
        return controller
    }

    private fun restoreStep1(state: SerializableState): Controller<*, *> {
        val controller = instantiateController(state)
        controller.restored = true
        controller.setRestoredModel(state.model)
        return controller
    }

    private fun instantiateController(state: SerializableState): Controller<*, *> {
        val controllerClass = Class.forName(state.controllerClass)
        val argsConstructor = controllerClass.constructors.firstOrNull {
            it.parameterTypes.firstOrNull()?.isAssignableFrom(state.args.javaClass) ?: false
        }
        return if (argsConstructor != null) {
            argsConstructor.newInstance(state.args) as Controller<*, *>
        } else {
            controllerClass.newInstance() as Controller<*, *>
        }
    }

    private fun restoreStep2(
            controller: Controller<*, *>,
            serializableState: SerializableState,
            parcelableState: Bundle
    ) {
        parcelableState.classLoader = TacoTaco::class.java.classLoader
        restoreChildren(controller, serializableState, parcelableState)
        controller.savedHierarchyState = parcelableState.getSparseParcelableArray(KEY_HIERARCHY_STATE)
        controller.savedClientState = parcelableState.getBundle(KEY_CLIENT_STATE)
    }

    private fun restoreChildren(
            controller: Controller<*, *>,
            serializableState: SerializableState,
            parcelableState: Bundle
    ) {
        val backstackParcelableStates = parcelableState.getSparseParcelableArray<BundleList>(KEY_BACKSTACK_STATES)
        serializableState.backstackStates.forEach { (id, serializableStates) ->
            val parcelableStates = backstackParcelableStates.get(id)
            val backstack = serializableStates.map { restoreStep1(it) }
            controller.setBackstack(id, backstack)
            toListOfTriples(backstack, serializableStates, parcelableStates).forEach {
                restoreStep2(it.first, it.second, it.third)
            }
        }
    }

    private fun <A, B, C> toListOfTriples(l1: List<A>, l2: List<B>, l3: List<C>): List<Triple<A, B, C>> {
        return (0 until l1.size).map { Triple(l1[it], l2[it], l3[it]) }
    }

    private class SerializableState(
            val controllerClass: String,
            val args: Args,
            val model: Model?,
            val backstackStates: Map<Int, List<SerializableState>>
    ) : Serializable

    private class RootSerializableState(
            val singletonStates: List<Serializable>,
            val controllerState: SerializableState
    ) : Serializable


    private class BundleList(items: List<Bundle>) : ArrayList<Bundle>(items), Parcelable {

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR = object : Parcelable.Creator<BundleList> {
                override fun createFromParcel(source: Parcel): BundleList = BundleList(source)
                override fun newArray(size: Int): Array<BundleList?> = arrayOfNulls(size)
            }
        }

        constructor(source: Parcel) : this(source.createTypedArrayList(Bundle.CREATOR))

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeTypedList(this)
        }

    }

    /**
     * Source: [org.nustaq.serialization.FSTConfiguration.createAndroidDefaultConfiguration]
     * Added special Kotlin objects instantiator
     */
    class CustomFstConfig : FSTConfiguration(null) {

        private val objenesis = ObjenesisStd()

        init {
            initDefaultFstConfigurationInternal(this)
            try {
                val clazz = Class.forName("com.google.gson.internal.LinkedTreeMap")
                registerSerializer(clazz, FSTMapSerializer(), true)
            } catch (e: ClassNotFoundException) {
            }
            try {
                val clazz = Class.forName("com.google.gson.internal.LinkedHashTreeMap")
                registerSerializer(clazz, FSTMapSerializer(), true)
            } catch (e: ClassNotFoundException) {
            }
            isForceSerializable = false
            isShareReferences = false
            registerClass(RootSerializableState::class.java, SerializableState::class.java)
        }

        override fun getInstantiator(clazz: Class<*>): FSTClassInstantiator {
            return if (getObjectInstance(clazz) != null) {
                objectsInstantiator
            } else {
                FSTObjenesisInstantiator(objenesis, clazz)
            }
        }

        private fun getObjectInstance(clazz: Class<*>): Any? {
            val field = clazz.declaredFields.firstOrNull {
                it.name == "INSTANCE"
                        && it.type == clazz
                        && Modifier.isStatic(it.modifiers)
                        && Modifier.isFinal(it.modifiers)
                        && Modifier.isPublic(it.modifiers)
            }
            return field?.get(null)
        }

        private val objectsInstantiator = object : FSTClassInstantiator {
            override fun newInstance(
                    clazz: Class<*>,
                    cons: Constructor<*>?,
                    doesRequireInit: Boolean,
                    unsafeAsLastResort: Boolean
            ): Any {
                return getObjectInstance(clazz)!!
            }

            override fun findConstructorForExternalize(clazz: Class<*>?) = null
            override fun findConstructorForSerializable(clazz: Class<*>?) = null
        }

    }

}
