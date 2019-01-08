package hangduykhiem.com.thesisocr.helper

import android.animation.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.components.LifecycleProvider
import com.wolt.tacotaco.components.TransitionAnimation
import hangduykhiem.com.thesisocr.R

class DialogPushAnimation(private val dialogContainerId: Int = R.id.dialogContainer) : TransitionAnimation {

    override fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator {
        val enterView = enterController?.view!!
        val dialogContainer: ViewGroup = enterView.findViewById(dialogContainerId)
        val vBackground = enterView.findViewById<View>(R.id.vBackground)
        return buildAnimator(250, OvershootInterpolator(),
            onUpdate = {
                dialogContainer.alpha = it
                dialogContainer.setScale(0.5f + 0.5f * it)
                vBackground.alpha = it
            }
        )
    }

}

class DialogPopAnimation(private val dialogContainerId: Int = R.id.dialogContainer) : TransitionAnimation {

    override fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator {
        val exitView = exitController?.view ?: return AnimatorSet()
        val contentContainer: ViewGroup = exitView.findViewById(dialogContainerId)
        return buildAnimator(250,
            onUpdate = {
                exitView.alpha = 1 - it
                contentContainer.setScale(1f - 0.3f * it)
            }
        )
    }

}

class FadeInFadeOutAnimation : TransitionAnimation {

    override fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator {
        val exitView = exitController?.view
        val enterView = enterController?.view
        return buildAnimator(250, onUpdate = {
            exitView?.alpha = 1 - it
            enterView?.alpha = it
        })
    }

}

class PushAnimation : TransitionAnimation {

    override fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator {
        return getPopPushAnimator(enterController, exitController, true)
    }

}

class PopAnimation : TransitionAnimation {

    override fun getAnimator(enterController: Controller<*, *>?, exitController: Controller<*, *>?): Animator {
        return getPopPushAnimator(enterController, exitController, false)
    }

}

private fun getPopPushAnimator(
    enterController: Controller<*, *>?,
    exitController: Controller<*, *>?,
    push: Boolean
): Animator {

    val enterView = enterController?.view
    val exitView = exitController?.view
    if (enterView == null || exitView == null) {
        return AnimatorSet()
    }

    enterView.alpha = 0f

    val x0 = (if (push) enterView.width else -enterView.width).toFloat()
    val x1 = (if (push) -exitView.width else exitView.width).toFloat()

    val animator1 = buildAnimator(200, onUpdate = {
        exitView.alpha = 1 - it
        exitView.translationX = x1 * it
    })

    val animator2 = buildAnimator(200, onUpdate = {
        enterView.alpha = it
        enterView.translationX = x0 * (1 - it)
    })

    animator2.startDelay = 100
    val set = AnimatorSet()
    set.playTogether(animator1, animator2)
    set.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            enterView.translationX = 0f
            enterView.alpha = 1f
            exitView.translationX = 0f
            exitView.alpha = 1f
        }
    })
    return set

}

fun buildAnimator(
    duration: Int, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    onUpdate: ((Float) -> (Unit)), onStart: (() -> (Unit))? = null,
    onEnd: ((cancelled: Boolean) -> (Unit))? = null, startDelay: Int = 0,
    lifecycleProvider: LifecycleProvider? = null
): ValueAnimator {

    val animator = ValueAnimator.ofFloat(0f, 1f)
        .setDuration(duration.toLong())
    animator.interpolator = interpolator
    animator.addUpdateListener {
        onUpdate.invoke(it.animatedFraction)
    }
    if (onStart != null || onEnd != null) {
        animator.addListener(object : AnimatorListenerAdapter() {
            private var cancelled = false

            override fun onAnimationStart(animation: Animator?) {
                onStart?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
                cancelled = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                onEnd?.invoke(cancelled)
            }
        })
    }
    animator.startDelay = startDelay.toLong()
    lifecycleProvider?.addListener(onDeflate = { animator.cancel() })
    return animator

}


fun View.setScale(scale: Float) {
    scaleX = scale
    scaleY = scale
}

