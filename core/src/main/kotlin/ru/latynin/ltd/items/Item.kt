package ru.latynin.ltd.items

import ru.latynin.ltd.currencies.QuantitativeValue

enum class ItemType{
    RED,
    GREEN,
    BLUE
}

class Item(
        val id: String,
        val type: ItemType
): QuantitativeValue() {

    constructor(id: String, type: ItemType, value: Int) : this(id, type) {
        this.value = value
    }

}