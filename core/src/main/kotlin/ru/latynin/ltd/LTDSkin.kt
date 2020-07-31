package ru.latynin.ltd

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import ktx.scene2d.defaultStyle
import ktx.style.*

object LTDSkin {

    fun createSkin(): Skin = skin { skin ->
        add(defaultStyle, BitmapFont())
        label {
            font = skin[defaultStyle]
        }
        imageButton {
            up = Image(Texture("img/button.png")).drawable
        }
        imageButton("flip") {
            val sprite = Sprite(Texture("img/button.png"))
            sprite.flip(true, false)
            up = SpriteDrawable(sprite)
        }
        imageButton("close") {
            val sprite = Sprite(Texture("img/inventory/close-button.png"))
            up = SpriteDrawable(sprite)
        }
        textButton {
            font = skin[defaultStyle]
        }
        window {
            titleFont = skin[defaultStyle]
        }
        scrollPane {
            val sprite = Sprite(Texture("img/b1.png"))
            sprite.flip(true, false)
            vScrollKnob = SpriteDrawable(sprite)
        }
        add("box", Sprite(Texture("img/box.png")))
        add("book", Sprite(Texture("img/book.png")))
        add("cpu", Sprite(Texture("img/cpu.png")))
        add("hdd", Sprite(Texture("img/hdd.png")))
        add("nut", Sprite(Texture("img/nut.png")))
        add("screwdriver", Sprite(Texture("img/screwdriver.png")))
        add("logo", Sprite(Texture("img/logo.png")))
        add("inventory-background", Sprite(Texture("img/inventory/background.png")))
        add("open-box", Sprite(Texture("img/inventory/open-box.png")))
        add("close-box", Sprite(Texture("img/inventory/close-box.png")))
        add("craft", Sprite(Texture("img/inventory/craft.png")))



        listOf("r", "g", "b").forEach { color ->
            listOf("h", "u", "d").forEach { dir ->
                add("$dir-line-$color", Sprite(Texture("img/inventory/$dir-line-$color.png")))
            }
            add("tab-$color", Sprite(Texture("img/inventory/tab-$color.png")))
            add("item-bg-$color", Sprite(Texture("img/items/item-bg-$color.png")))
        }
    }

}