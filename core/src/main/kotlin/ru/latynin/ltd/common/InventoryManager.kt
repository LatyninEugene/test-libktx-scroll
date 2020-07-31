package ru.latynin.ltd.common

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.JsonSerializer
import ru.latynin.ltd.items.Item
import ru.latynin.ltd.items.ItemType
import java.lang.RuntimeException

class InventoryManager(
        private val gameSaver: GameSaver,
        private val skin: Skin
) {

    val items: List<Item> = listOf(
            Item("resistor", ItemType.BLUE),
            Item("diode", ItemType.BLUE),
            Item("capacitor", ItemType.GREEN),
            Item("led", ItemType.GREEN),
            Item("photodiode", ItemType.GREEN),
            Item("transistor", ItemType.RED),
            Item("phototransistor", ItemType.RED)
    )

    val savedKey = "items"
    init {
        gameSaver.addClassTab<Item>("item")
        gameSaver.addClassTab<List<Item>>("items")
        gameSaver.addSerializer(object : JsonSerializer<Item> {
            override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Item {
                return items.find { jsonValue.getString("id") == it.id }?.apply {
                    set(jsonValue.getInt("count"))
                } ?: throw RuntimeException()
            }

            override fun write(json: Json, value: Item, type: Class<*>?) {
                json.writeObjectStart()
                json.writeValue("id", value.id)
                json.writeValue("count", value.value)
                json.writeObjectEnd()
            }

        })
        gameSaver.addObjectToSave(savedKey, items)
        gameSaver.addLoadAllFunction {
            it.loadListObject<Item>(savedKey)
        }

        items.forEach {
            skin.add(it.id, Sprite(Texture("img/items/${it.id}.png")))
            skin.add(it.id+"-s", Sprite(Texture("img/items/${it.id}-s.png")))
        }
    }

}