package ru.latynin.ltd.screens

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.scene2d.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.latynin.ltd.GameManager
import ru.latynin.ltd.animations.AnimatedObject
import ru.latynin.ltd.animations.AnimatedObject.Companion.waitEndOfAnim
import ru.latynin.ltd.animations.animator
import ru.latynin.ltd.common.InventoryManager
import ru.latynin.ltd.items.Item
import ru.latynin.ltd.items.ItemType

enum class InvTab {
    INVENTORY,
    BOXES,
    CRAFT
}

class InventoryWindow(
        private val gameManager: GameManager,
        private val inventoryManager: InventoryManager
) : KoinComponent, AnimatedObject {

    private val mainSkin: Skin by inject()

    private val uLine: Image
    private val hLine: Image
    private val dLine: Image

    private val tabR: Image
    private val tabG: Image
    private val tabB: Image

    private val title: Label

    var currentTab = InvTab.INVENTORY
        private set

    private val backgroundWidth = gameManager.mainMenuScreen.rootStage.width * 0.70f
    private val backgroundHeight = backgroundWidth * 1.89f

    private val itemMap: Map<Item, KTableWidget>
    private val content: KTableWidget //Table
//    private val content: KScrollPane //Scroll
    val invTab: KTableWidget
    var needInvRecreate = true

    val window = scene2d.table { inv ->
        width = gameManager.mainMenuScreen.rootStage.width
        height = gameManager.mainMenuScreen.rootStage.height
        setOrigin(width / 2, height / 2)
        center(gameManager.mainMenuScreen.rootStage)
        val lineWidth = backgroundWidth * 0.1f
        val lineHeight = backgroundWidth * 0.85f

        //lines
        table {
            uLine = image("u-line-g") {
            }.cell(width = lineHeight, height = lineWidth,
                    padLeft = lineWidth,
                    align = Align.left)
        }.cell(colspan = 2, align = Align.left, padTop = height * 0.10f)
        row()
        table {
            hLine = image("h-line-g") {
            }.cell(width = lineWidth, height = backgroundHeight, align = Align.top)
        }

        //inventory
        table {
            zIndex = 10
            setBackground("inventory-background")

            align(Align.topLeft)
            title = label(InvTab.INVENTORY.name) {
                onClick { inventoryManager.items.random().add(1) }
            }.cell(
                    padLeft = backgroundWidth * 0.08f,
                    padTop = backgroundHeight * 0.018f,
                    padBottom = backgroundHeight * 0.022f,
                    align = Align.left)
            row()

            //content
//            content = scrollPane (skin = Scene2DSkin.defaultSkin) { //Scroll
            content = table(skin = Scene2DSkin.defaultSkin) { //Table
                align(Align.topLeft) //Table
//                fadeScrollBars = false //Scroll

                debug = true
                //Item inventory
                invTab = table(skin = mainSkin) {
                    debug = true
                    itemMap = inventoryManager.items.map { item ->
                        item to table {
                            background = when (item.type) {
                                ItemType.RED -> mainSkin.getDrawable("item-bg-r")
                                ItemType.GREEN -> mainSkin.getDrawable("item-bg-g")
                                ItemType.BLUE -> mainSkin.getDrawable("item-bg-b")
                            }
                            image(item.id).cell(
                                    width = backgroundWidth * 0.15f,
                                    height = backgroundWidth * 0.15f,
                                    pad = backgroundWidth * 0.05f,
                                    padBottom = 0f)
                            onClick {
                                item.minus(1)
                            }
                            row()
                            val label = label(item.value.toString()).cell(padBottom = backgroundWidth * 0.02f, align = Align.bottom)
                            item.addListener { newValue, oldValue ->
                                label.setText(newValue.toString())
                                if (newValue == 0 || oldValue == 0) {
                                    if(isVisible) recreateInventory()
                                    else needInvRecreate = true
                                }
                            }
                        }
                    }.toMap()
                }

                //Boxes

                //TODO

                //Craft

                //TODO

            }.cell(width = backgroundWidth * 0.85f,
//                    height = backgroundHeight*0.25f, //Scroll
                    align = Align.top,
                    padLeft = backgroundWidth * 0.02f)

            table {
                align(Align.top)
                image("open-box") {
                    onClick {
                        if (currentTab != InvTab.INVENTORY) {
                            openStorage(InvTab.INVENTORY) {
                                content.clearChildren()
//                                content.actor = invTab //Scroll
                                content.add(invTab) //Table
                            }
                        }
                    }
                }.cell(row = true)
                image("close-box") {
                    onClick {
                        if (currentTab != InvTab.BOXES) {
                            openStorage(InvTab.BOXES) {
                                content.clearChildren()
                            }
                        }
                    }
                }.cell(padTop = backgroundHeight * 0.019f, row = true)
                image("craft") {
                    onClick {
                        if (currentTab != InvTab.CRAFT) {
                            openStorage(InvTab.CRAFT) {
                                content.clearChildren()
                            }
                        }
                    }
                }.cell(padTop = backgroundHeight * 0.019f)
            }.cell(width = backgroundWidth * 0.11f, height = backgroundHeight * 0.91f, padLeft = backgroundWidth*0.01f, padTop = backgroundHeight * 0.01f)

        }.cell(width = backgroundWidth, height = backgroundHeight)

        //lines
        table {
            zIndex = 1
            align(Align.topLeft)
            tabG = image("tab-g").cell(row = true)
            tabB = image("tab-b").cell(padTop = backgroundHeight * 0.019f, row = true)
            tabR = image("tab-r").cell(padTop = backgroundHeight * 0.019f)
        }.cell(width = backgroundWidth * 0.07f, height = backgroundHeight * 0.91f, padTop = backgroundHeight * 0.09f, align = Align.topLeft)
        row()
        table {
            zIndex = 1
            dLine = image("d-line-g") {
            }.cell(width = lineHeight * 0.85f, height = lineWidth,
                    padBottom = height * 0.10f, padLeft = lineWidth,
                    align = Align.topLeft)
            imageButton("close") {
                onClick {
                    gameManager.closeInventory(openWithCallback = gameManager.mainMenuScreen)
                }
            }.cell(height = backgroundHeight * 0.07f, width = backgroundHeight * 0.07f, padRight = backgroundWidth * 0.15f)
        }.cell(colspan = 2)

        recreateInventory()
    }

    override fun updateDefaultSettings() {
        listOf(tabG, tabR, tabB).forEach {
            it.x -= it.width
            it.animator.defPosition.set(it.x, it.y)
        }
        uLine.apply {
            y -= height
            animator.defPosition.set(x, y)
        }
        hLine.apply {
            x += width
            animator.defPosition.set(x, y)
        }
        dLine.apply {
            y += height
            animator.defPosition.set(x, y)
        }
    }

    private fun KTableWidget.invItems() {
        itemMap.entries
                .filter { it.key.value > 0 }
                .forEachIndexed { index, entry ->
                    add(entry.value).apply {
                        pad(backgroundWidth * 0.015f)
                    }
                    if ((index + 1) % 3 == 0) {
                        row()
                    }
                }
    }

    private fun recreateInventory() {
        invTab.clearChildren()
        invTab.invItems()
        invTab.layout()
        needInvRecreate = false
    }

    override fun open(endAnimCall: (() -> Unit)) {
        openStorage(currentTab, endAnimCall)
    }

    fun openStorage(tab: InvTab, endCloseFun: (() -> Unit) = {}) {
        closeInvLine {
            val color = when (tab) {
                InvTab.INVENTORY -> {
                    if(needInvRecreate) {
                        recreateInventory()
                    }
                    Pair(tabG, "g")
                }
                InvTab.BOXES -> Pair(tabB, "b")
                InvTab.CRAFT -> Pair(tabR, "r")
            }
            uLine.drawable = mainSkin.getDrawable("u-line-${color.second}")
            hLine.drawable = mainSkin.getDrawable("h-line-${color.second}")
            dLine.drawable = mainSkin.getDrawable("d-line-${color.second}")
            title.setText(tab.name)
            currentTab = tab
            endCloseFun()
            openInvLine()
        }
        closeInvTab {
            openInvTab(when (tab) {
                InvTab.INVENTORY -> tabG
                InvTab.BOXES -> tabB
                InvTab.CRAFT -> tabR
            })
        }
    }

    override fun close(endAnimCall: (() -> Unit)) {
        closeInvTab()
        closeInvLine {
            endAnimCall()
        }
    }


    //Animations
    private val executor = newSingleThreadAsyncContext()

    //Inventory Lines
    private fun openInvLine(endAnimCall: (() -> Unit) = {}) {
        val animations = listOfNotNull(
                uLine.let {
                    it.animator.moveFromDefPosAnim(0f, it.height, duration = 0.2f, clearAction = false)
                },
                hLine.let {
                    it.animator.moveFromDefPosAnim(-it.width, 0f, duration = 0.2f, clearAction = false)
                },
                dLine.let {
                    it.animator.moveFromDefPosAnim(0f, -it.height, duration = 0.2f, clearAction = false)
                }
        )
        KtxAsync.launch(executor) {
            waitEndOfAnim(animations)
            endAnimCall()
        }
    }

    private fun closeInvLine(endAnimCall: (() -> Unit) = {}) {
        val animations = listOfNotNull(
                uLine.animator.moveBackAnim(duration = 0.2f, clearAction = false),
                dLine.animator.moveBackAnim(duration = 0.2f, clearAction = false),
                hLine.animator.moveBackAnim(duration = 0.2f, clearAction = false)
        )
        KtxAsync.launch(executor) {
            waitEndOfAnim(animations)
            endAnimCall()
        }
    }

    private fun closeInvTab(endAnimCall: (() -> Unit) = {}) {
        val animations = listOf(
                tabG.animator.moveBackAnim(duration = 0.2f, clearAction = false),
                tabB.animator.moveBackAnim(duration = 0.2f, clearAction = false),
                tabR.animator.moveBackAnim(duration = 0.2f, clearAction = false)
        )
        KtxAsync.launch(executor) {
            waitEndOfAnim(animations)
            endAnimCall()
        }
    }

    private fun openInvTab(tab: Actor, endAnimCall: (() -> Unit) = {}) {
        val animation = tab.animator.moveFromDefPosAnim(tab.width, 0f, duration = 0.2f, clearAction = false)
        KtxAsync.launch(executor) {
            waitEndOfAnim(listOf(animation))
            endAnimCall()
        }
    }


}