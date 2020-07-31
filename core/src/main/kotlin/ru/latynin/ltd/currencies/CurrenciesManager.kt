package ru.latynin.ltd.currencies

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import ktx.json.JsonSerializer
import ru.latynin.ltd.common.GameSaver

class CurrenciesManager(
        private val gameSaver: GameSaver
) {

    val cpuPower = object : BitValue(){
        override val types: List<String> = listOf("Hz", "KHz", "MHz", "GHz", "THz")
    }
    val memory = object : BitValue() {
        override val types: List<String> = listOf("B", "KB", "MB", "GB", "TB")
    }
    val details = object : QuantitativeValue() {}

    val saveKey = "currencies"
    init {
        gameSaver.addClassTab<CurrenciesManager>("currencies")
        gameSaver.addSerializer(object : JsonSerializer<CurrenciesManager> {
            override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): CurrenciesManager {
                val manager = this@CurrenciesManager
                manager.details.set(jsonValue.getInt("details"))
                manager.memory.set(jsonValue.getFloat("memory"), jsonValue.getInt("memoryType"))
                manager.cpuPower.set(jsonValue.getFloat("cpuPower"), jsonValue.getInt("cpuPowerType"))
                return manager
            }
            override fun write(json: Json, value: CurrenciesManager, type: Class<*>?) {
                json.writeObjectStart()
                json.writeValue("cpuPower", value.cpuPower.value)
                json.writeValue("cpuPowerType", value.cpuPower.currentType)
                json.writeValue("memory", value.memory.value)
                json.writeValue("memoryType", value.memory.currentType)
                json.writeValue("details", value.details.value)
                json.writeObjectEnd()
            }
        })
        gameSaver.addObjectToSave(saveKey, this)
        gameSaver.addLoadAllFunction { saver ->
            saver.loadObject<CurrenciesManager>(saveKey)
        }
    }

}