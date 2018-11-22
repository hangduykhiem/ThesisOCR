package hangduykhiem.com.thesisocr.helper

import java.io.Serializable

sealed class WorkState : Serializable {

    object Other : WorkState() {
        override fun toString(): String = "Other"
    }

    data class Fail(val error: Throwable) : WorkState() {
        override fun toString(): String = "Fail"
    }

    object InProgress : WorkState() {
        override fun toString(): String = "InProgress"
    }

    object Complete : WorkState() {
        override fun toString(): String = "Complete"
    }

}
