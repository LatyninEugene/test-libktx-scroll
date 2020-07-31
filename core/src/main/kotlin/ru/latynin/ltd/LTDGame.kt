package ru.latynin.ltd

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import ktx.app.KtxGame
import ktx.async.KtxAsync
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.latynin.ltd.screens.MainMenuScreen

class LTDGame : KtxGame<Screen>(), KoinComponent {
    override fun create() {
        KtxAsync.initiate()
        val settings by inject<GameSettings>()
        Gdx.graphics.setWindowedMode(settings.screenWidth, settings.screenHeight)

        addScreen(get() as MainMenuScreen)
        setScreen<MainMenuScreen>()
    }

}