package ru.latynin.ltd.common

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import ktx.json.JsonSerializer
import ktx.json.addClassTag
import ktx.json.fromJson
import ktx.json.setSerializer

class GameSaver {

    val json = Json()
    private val savedObjects: MutableMap<String, Any> = mutableMapOf()
    private val loadAllFunctions: MutableList<(GameSaver) -> Unit> = mutableListOf()

    /** When set, we know we're on Android and can save to the app's personal external file directory
     * See https://developer.android.com/training/data-storage/app-specific#external-access-files */
    var externalFilesDirForAndroid = ""

    inline fun <reified T> addSerializer(obj: JsonSerializer<T>) {
        json.setSerializer(obj)
    }

    inline fun <reified T> addClassTab(tag: String) {
        json.addClassTag<T>(tag)
    }

    fun getFile(fileName: String): FileHandle {
        val localFile = Gdx.files.local("save-data/$fileName")
        if (externalFilesDirForAndroid == "" || !Gdx.files.isExternalStorageAvailable) return localFile
        val externalFile = Gdx.files.absolute("$externalFilesDirForAndroid/save-data/$fileName")
        if (localFile.exists() && !externalFile.exists()) return localFile
        return externalFile
    }

    fun addLoadAllFunction(func: (GameSaver) -> Unit) {
        loadAllFunctions.add(func)
    }

    fun addObjectToSave(key: String, obj: Any) {
        savedObjects[key] = obj
    }

    fun saveAll() {
        savedObjects.forEach { (key, value) ->
            json.toJson(value, getFile(key))
        }
    }

    fun loadAll() {
        loadAllFunctions.forEach {
            it(this)
        }
    }

    fun save(key: String): Boolean {
        return savedObjects[key]?.let {
            json.toJson(it, getFile(key))
            true
        } ?: false
    }

    inline fun <reified T> loadObject(key: String): T? {
        val file = getFile(key)
        return if (file.exists()) json.fromJson(file) else null
    }

    inline fun <reified T> loadListObject(key: String): List<T>? {
        val file = getFile(key)
        return if (file.exists())
            (json.fromJson(file) as List<JsonValue>).map {
                json.fromJson<T>(it.toJson(JsonWriter.OutputType.json)) }
        else null
    }


}