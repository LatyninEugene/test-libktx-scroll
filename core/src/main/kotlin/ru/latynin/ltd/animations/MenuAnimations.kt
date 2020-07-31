package ru.latynin.ltd.animations

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import ktx.math.vec2

val animatorRepository = mutableMapOf<Actor, MenuAnimator>()

val Actor.animator: MenuAnimator
    get() = animatorRepository[this] ?: animatorRepository.let {
            MenuAnimator(this).apply {
                it[this@animator] = this
            }
        }

class MenuAnimator(
        val actor: Actor
) {

    var defScale = vec2()
    var defPosition = vec2()

    fun updateDefaultScale() {
        defScale.set(actor.scaleX, actor.scaleY)
    }
    fun updateDefaultPosition() {
        defPosition.set(vec2(actor.x, actor.y))
    }
    fun updateDefaultSettings() {
        updateDefaultPosition()
        updateDefaultScale()
    }


    fun reduceAnim(clearAction: Boolean = true, duration: Float = 0.3f) : TemporalAction{
        if (clearAction) {
            actor.actions.clear()
        }
        return ScaleToAction().apply {
            setScale(0f, 0f)
            this.duration = duration
            this@MenuAnimator.actor.addAction(this)
        }
    }

    fun scaleBackAnim(clearAction: Boolean = true, duration: Float = 0.3f) : TemporalAction {
        if (clearAction) {
            actor.actions.clear()
        }
        return ScaleToAction().apply {
            setScale(defScale.x, defScale.y)
            this.duration = duration
            this@MenuAnimator.actor.addAction(this)
        }
    }

    fun moveFromDefPosAnim(x: Float, y: Float, clearAction: Boolean = true, duration: Float = 0.3f) : TemporalAction {
        if (clearAction) {
            actor.actions.clear()
        }
        return MoveToAction().apply {
            setPosition(defPosition.x+x, defPosition.y+y)
            this.duration = duration
            this@MenuAnimator.actor.addAction(this)
        }
    }
    fun moveFromActorPosAnim(x: Float, y: Float, clearAction: Boolean = true, duration: Float = 0.3f) : TemporalAction {
        if (clearAction) {
            actor.actions.clear()
        }
        return MoveToAction().apply {
            setPosition(this@MenuAnimator.actor.x+x, this@MenuAnimator.actor.y+y)
            this.duration = duration
            this@MenuAnimator.actor.addAction(this)
        }
    }
    fun moveBackAnim(clearAction: Boolean = true, duration: Float = 0.3f) : TemporalAction {
        if (clearAction) {
            actor.actions.clear()
        }
        return MoveToAction().apply {
            setPosition(defPosition.x, defPosition.y)
            this.duration = duration
            this@MenuAnimator.actor.addAction(this)
        }
    }
}
