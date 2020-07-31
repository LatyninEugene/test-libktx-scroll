package ru.latynin.ltd

import com.badlogic.gdx.scenes.scene2d.Stage
import ru.latynin.ltd.animations.AnimatedObject
import ru.latynin.ltd.common.GameSaver
import ru.latynin.ltd.common.InventoryManager
import ru.latynin.ltd.currencies.CurrenciesManager
import ru.latynin.ltd.screens.*

class GameManager(
        private val gameSaver: GameSaver,
        private val currenciesManager: CurrenciesManager,
        private val inventoryManager: InventoryManager
) {

    val mainMenuScreen = MainMenuScreen(this)
    val inventoryWindow = InventoryWindow(this, inventoryManager)

    fun show(screen: BaseScreen){
        gameSaver.loadAll()

        screen.rootStage.actors.add(inventoryWindow.window)
        screen.rootStage.actors.add(mainMenuScreen.window)

        screen.rootStage.draw()
        mainMenuScreen.updateDefaultSettings()
        inventoryWindow.updateDefaultSettings()

        screen.rootStage.actors.removeIndex(screen.rootStage.actors.indexOf(inventoryWindow.window))
        screen.rootStage.actors.removeIndex(screen.rootStage.actors.indexOf(mainMenuScreen.window))
    }

    fun dispose() {
        gameSaver.saveAll()
    }

    fun resize(stage: Stage) {
        mainMenuScreen.window.center(stage)
        inventoryWindow.window.center(stage)
    }

    fun openInventory(
            simpleClose: List<AnimatedObject> = emptyList(),
            closeWithCallback: AnimatedObject? = null,
            endAnimCall: (() -> Unit) = {}
    ){
        if (mainMenuScreen.rootStage.actors.indexOf(inventoryWindow.window) != -1) {
            return
        }
        simpleClose.forEach { it.close() }
        if(closeWithCallback != null){
            closeWithCallback.close {
                mainMenuScreen.rootStage.actors.add(inventoryWindow.window)
                inventoryWindow.invTab.layout()
                inventoryWindow.open {
                    endAnimCall()
                }
            }
        }else {
            mainMenuScreen.rootStage.actors.add(inventoryWindow.window)
            inventoryWindow.invTab.layout()
            inventoryWindow.open {
                endAnimCall()
            }
        }
    }

    fun closeInventory(
            simpleOpen: List<AnimatedObject> = emptyList(),
            openWithCallback: AnimatedObject? = null,
            endAnimCall: (() -> Unit) = {}
    ) {
        val index = mainMenuScreen.rootStage.actors.indexOf(inventoryWindow.window)
        if (index < 0) {
            return
        }
        simpleOpen.forEach { it.open() }
        if(openWithCallback != null){
            inventoryWindow.close {
                mainMenuScreen.rootStage.actors.removeIndex(index)
                openWithCallback.open(endAnimCall)
            }
        } else {
            inventoryWindow.close {
                mainMenuScreen.rootStage.actors.removeIndex(index)
            }
        }
    }
}