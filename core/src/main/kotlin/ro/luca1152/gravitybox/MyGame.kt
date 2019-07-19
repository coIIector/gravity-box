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
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.pay.PurchaseManager
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import ktx.app.KtxGame
import ktx.inject.Context
import pl.mk5.gdx.fireapp.GdxFIRApp
import pl.mk5.gdx.fireapp.GdxFIRCrash
import pl.mk5.gdx.fireapp.promises.FuturePromise
import ro.luca1152.gravitybox.events.EventQueue
import ro.luca1152.gravitybox.screens.LoadingScreen
import ro.luca1152.gravitybox.utils.ads.AdsController
import ro.luca1152.gravitybox.utils.kotlin.*
import ro.luca1152.gravitybox.utils.leaderboards.GameShotsLeaderboardController
import ro.luca1152.gravitybox.utils.ui.label.BaseDistanceFieldLabel

/** The main class of the game. */
class MyGame : KtxGame<Screen>() {
    // Initialized in AndroidLauncher
    lateinit var purchaseManager: PurchaseManager
    lateinit var adsController: AdsController

    private val context = Context()

    override fun create() {
        initializePhysicsEngine()
        initializeDependencyInjection()
        initializeFirebase()
        addScreen(LoadingScreen(context))
        setScreen<LoadingScreen>()
    }

    private fun initializePhysicsEngine() {
        Box2D.init()
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
            bindSingleton(GameRules(context))
            bindSingleton(World(Vector2(0f, context.inject<GameRules>().GRAVITY), true))
            bindSingleton(GameCamera())
            bindSingleton(GameViewport(context))
            bindSingleton(GameStage(context))
            bindSingleton(OverlayCamera())
            bindSingleton(OverlayViewport(context))
            bindSingleton(OverlayStage(context))
            bindSingleton(UICamera())
            bindSingleton(UIViewport(context))
            bindSingleton(UIStage(context))
            bindSingleton(MenuOverlayViewport(context))
            bindSingleton(MenuOverlayStage(context))
            bindSingleton(DistanceFieldShader(BaseDistanceFieldLabel.vertexShader, BaseDistanceFieldLabel.fragmentShader))
            bindSingleton(OutlineDistanceFieldShader(BaseDistanceFieldLabel.vertexShader, BaseDistanceFieldLabel.outlineFragmentShader))
            if (context.inject<GameRules>().IS_MOBILE) {
                bindSingleton(purchaseManager)
                bindSingleton(adsController)
            }

            // Leaderboards
            bindSingleton(GameShotsLeaderboardController(context))
        }
    }

    private fun initializeFirebase() {
        if (context.inject<GameRules>().IS_MOBILE) {
            GdxFIRApp.inst().configure()
            GdxFIRCrash.inst().initialize()
            FuturePromise.setThrowFailByDefault(false)
        }
    }

    override fun dispose() {
        // Make sure Preferences are flushed
        context.inject<Preferences>().flush()

        // Disposes every screen
        super.dispose()

        context.dispose()
    }
}