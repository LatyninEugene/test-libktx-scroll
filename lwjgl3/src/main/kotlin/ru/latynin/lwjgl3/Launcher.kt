package ru.latynin.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.latynin.ltd.GameSettings
import ru.latynin.ltd.Koin
import ru.latynin.ltd.LTDGame

object Launcher : KoinComponent {
    @JvmStatic
    fun main(args: Array<String>) {
        createApplication()
    }

    private fun createApplication(): Lwjgl3Application {
        val game = LTDGame()
        Koin.start(game)
        return Lwjgl3Application(game, defaultConfiguration)
    }

    private val defaultConfiguration: Lwjgl3ApplicationConfiguration
        private get() {
            val configuration = Lwjgl3ApplicationConfiguration()
            val settings = get<GameSettings>()
            configuration.setTitle("LTD")
            configuration.setWindowedMode(settings.screenWidth, settings.screenHeight)
            configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
            return configuration
        }
}