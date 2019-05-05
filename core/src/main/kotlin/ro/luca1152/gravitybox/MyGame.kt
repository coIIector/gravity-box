/*
 * This file is part of Gravity Box.
 *
 * Gravity Box is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gravity Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gravitybox

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import ktx.app.KtxGame
import ktx.inject.Context
import ro.luca1152.gravitybox.components.game.MapComponent
import ro.luca1152.gravitybox.events.EventQueue
import ro.luca1152.gravitybox.screens.LoadingScreen
import ro.luca1152.gravitybox.utils.kotlin.*

/** The main class of the game. */
class MyGame : KtxGame<Screen>() {
    companion object {
        const val LEVELS_NUMBER = 204
    }

    private val context = Context()

    override fun create() {
        Box2D.init()
        initializeDependencyInjection()
        addScreen(LoadingScreen(context))
        setScreen<LoadingScreen>()
    }

    private fun initializeDependencyInjection() {
        context.register {
            bindSingleton(this@MyGame)
            bindSingleton(SpriteBatch() as Batch)
            bindSingleton(AssetManager())
            bindSingleton(EventQueue())
            bindSingleton(InputMultiplexer())
            bindSingleton(PooledEngine())
            bindSingleton(ShapeRenderer())
            bindSingleton(Gdx.app.getPreferences("Gravity Box by Luca1152"))
            bindSingleton(World(Vector2(0f, MapComponent.GRAVITY), true))
            bindSingleton(GameCamera())
            bindSingleton(GameViewport(context))
            bindSingleton(GameStage(context))
            bindSingleton(OverlayCamera())
            bindSingleton(OverlayViewport(context))
            bindSingleton(OverlayStage(context))
            bindSingleton(UICamera())
            bindSingleton(UIViewport(context))
            bindSingleton(UIStage(context))
        }
    }

    override fun dispose() {
        super.dispose() // Disposes every screen
        context.dispose()
    }
}