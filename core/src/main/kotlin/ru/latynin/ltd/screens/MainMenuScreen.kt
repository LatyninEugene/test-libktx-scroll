package ru.latynin.ltd.screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.scene2d.*
import org.koin.core.KoinComponent
import ru.latynin.ltd.GameManager
import ru.latynin.ltd.animations.AnimatedObject
import ru.latynin.ltd.animations.AnimatedObject.Companion.waitEndOfAnim
import ru.latynin.ltd.animations.animator

class MainMenuScreen(
        val gameManager: GameManager
): BaseScreen(), KoinComponent, AnimatedObject {

    val btnWidth: Float
    val btnHeight: Float
    val leftPanel: Table
    var mainPanel: Table

    val window: Table

    init {
        rootStage.actors {
            window = table { root ->
                height = rootStage.height
                btnWidth = rootStage.width*0.4f
                btnHeight = rootStage.height*0.1f

                mainPanel = table {
                    padTop(rootStage.height*0.15f)
                    leftPanel = table {
                        padTop(10f)
                        padBottom(10f)
                        padRight(5f)
                        align(Align.left)
                        addButton(label = "Inventory", image = "box", flip = true, width = btnWidth, height = btnHeight) {
                            gameManager.openInventory(closeWithCallback = this@MainMenuScreen)
                        }
                    }.cell(width = rootStage.width)
                    padBottom(rootStage.height*0.1f)
                }
            }
        }
    }

    override fun updateDefaultSettings(){
        leftPanel.animator.updateDefaultSettings()
    }

    override fun show() {
        super.show()
        gameManager.show(this)
    }

    override fun dispose() {
        gameManager.dispose()
        super.dispose()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameManager.resize(rootStage)
    }

    private val executor = newSingleThreadAsyncContext()
    override fun open(endAnimCall: (() -> Unit)) {
        leftPanel.isVisible = true
        val animations = listOfNotNull(
                leftPanel.animator.moveBackAnim(duration = 0.2f)
        )
        KtxAsync.launch(executor) {
            waitEndOfAnim(animations)
            endAnimCall()
        }
    }
    override fun close(endAnimCall: (() -> Unit)) {
        val animations = listOfNotNull(
                leftPanel.let {
                    it.animator.moveFromDefPosAnim(-it.width, 0f, duration = 0.2f)
                }
        )

        KtxAsync.launch(executor) {
            waitEndOfAnim(animations)
            leftPanel.isVisible = false
            endAnimCall()
        }
    }
}

fun KTableWidget.addButton(
        label: String,
        image: String? = null,
        flip: Boolean = false,
        width: Float = 100f,
        height: Float = 100f,
        click: () -> Unit
) {
    imageButton(if (flip) "flip" else defaultStyle) {
        horizontalGroup {
            image?.let { image(it) }
            label(label)
            onClick {
                click()
            }
        }
    }.cell(width = width, height = height)
}