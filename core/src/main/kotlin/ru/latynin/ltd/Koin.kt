package ru.latynin.ltd

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import org.koin.dsl.module
import ru.latynin.ltd.common.GameSaver
import ru.latynin.ltd.common.InventoryManager
import ru.latynin.ltd.currencies.CurrenciesManager

object Koin{

    fun start(game: LTDGame){
        org.koin.core.context.startKoin {
            modules(
                module {
                    single { game }
                    single { LTDSkin.createSkin() }
                    single { GameSettings() }
                    single { GameSaver() }
                    single { CurrenciesManager(get()) }
                    single { InventoryManager(get(), get()) }
                    single<Batch> { SpriteBatch() }
                    single { get<GameManager>().mainMenuScreen }
                    single { get<GameManager>().inventoryWindow }
                    single { GameManager(get(), get(), get()) }
                }
            )
        }
    }

}