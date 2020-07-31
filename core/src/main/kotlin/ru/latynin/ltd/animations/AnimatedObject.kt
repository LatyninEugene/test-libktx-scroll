package ru.latynin.ltd.animations

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction

interface AnimatedObject {

    fun updateDefaultSettings()

    fun close(endAnimCall: (() -> Unit) = {})
    fun open(endAnimCall: (() -> Unit) = {})

    companion object {
        fun waitEndOfAnim(animations: List<TemporalAction>) {
            while (!animations.all { it.isComplete }) {
                Thread.sleep(10)
            }
        }
    }
}