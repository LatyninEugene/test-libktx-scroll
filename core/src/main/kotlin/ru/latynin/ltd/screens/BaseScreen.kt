package ru.latynin.ltd.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.latynin.ltd.GameSettings

open class BaseScreen: KtxScreen, KoinComponent {

    val gameSettings by inject<GameSettings>()
    val batch by inject<Batch>()

    val camera: OrthographicCamera
    val rootStage: Stage
    val program: ShaderProgram
    var time = 0f

    init {
        Scene2DSkin.defaultSkin = get()
        camera = OrthographicCamera(gameSettings.screenWidth.toFloat(), gameSettings.screenHeight.toFloat())
        rootStage = Stage(ExtendViewport(gameSettings.screenWidth.toFloat(), gameSettings.screenHeight.toFloat(), camera))
        Gdx.input.inputProcessor = rootStage
        program = ShaderProgram(
                Gdx.files.internal("vertex.glsl").readString(),
                Gdx.files.internal(listOf<String>("lines.glsl", "lines1.glsl", "DataStream.glsl").random()).readString())
        ShaderProgram.pedantic = false
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.shader = program
        time += Gdx.graphics.deltaTime
        program.setUniformf("u_time", time)
        program.setUniformf("u_mouse", Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()))
        program.setUniformf("u_resolution", Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
        batch.draw(Texture("img/badlogic.png"), 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        batch.end()

        rootStage.act(delta)
        rootStage.draw()
    }

    override fun resize(width: Int, height: Int) {
        rootStage.viewport.update(width, height, true)
    }

}

fun Actor.centerX(parent: Actor){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent: Actor){ y = parent.height/2- height/2}
fun Actor.center(parent: Actor){ centerX(parent); centerY(parent)}

fun Actor.centerX(parent:Stage){ x = parent.width/2 - width/2 }
fun Actor.centerY(parent:Stage){ y = parent.height/2- height/2}
fun Actor.center(parent:Stage){ centerX(parent); centerY(parent)}