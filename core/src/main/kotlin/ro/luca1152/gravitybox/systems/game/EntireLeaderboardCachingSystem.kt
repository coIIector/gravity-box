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

package ro.luca1152.gravitybox.systems.game

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.utils.TimeUtils
import ktx.inject.Context
import ro.luca1152.gravitybox.GameRules
import ro.luca1152.gravitybox.events.EventQueue
import ro.luca1152.gravitybox.events.WriteLeaderboardToStorageEvent
import ro.luca1152.gravitybox.utils.ads.AdsController
import ro.luca1152.gravitybox.utils.kotlin.info
import ro.luca1152.gravitybox.utils.kotlin.injectNullable
import ro.luca1152.gravitybox.utils.leaderboards.GameShotsLeaderboard
import ro.luca1152.gravitybox.utils.leaderboards.GameShotsLeaderboardController

class EntireLeaderboardCachingSystem(private val context: Context) : EntitySystem() {
    // Injected objects
    private val gameRules: GameRules = context.inject()
    private val adsController: AdsController? = context.injectNullable()
    private val gameShotsLeaderboardController: GameShotsLeaderboardController = context.inject()
    private val eventQueue: EventQueue = context.inject()

    private var isCachingGameLeaderboard = false

    override fun update(deltaTime: Float) {
        if (adsController?.isNetworkConnected() == false) return
        if (TimeUtils.timeSinceMillis(gameRules.NEXT_LEADERBOARD_CACHE_TIME) <= 0L && gameRules.CACHED_LEADERBOARD_VERSION == gameRules.GAME_LEVELS_VERSION) return
        if (isCachingGameLeaderboard) return
        cacheLeaderboard()
    }

    private fun cacheLeaderboard() {
        isCachingGameLeaderboard = true
        gameShotsLeaderboardController.readEntireGameLeaderboardDatabase {
            isCachingGameLeaderboard = false
            logInfoMessage()
            updateNextLeaderboardCacheTime()
            writeLeaderboardToStorage()
            bindLeaderboard(it)
        }
    }

    private fun logInfoMessage() {
        info("Cached the entire game leaderboard.")
    }

    private fun updateNextLeaderboardCacheTime() {
        gameRules.run {
            NEXT_LEADERBOARD_CACHE_TIME = TimeUtils.millis() + gameRules.TIME_DELAY_BETWEEN_CACHING_LEADERBOARD
            flushUpdates()
        }
    }

    private fun writeLeaderboardToStorage() {
        eventQueue.add(WriteLeaderboardToStorageEvent())
    }

    private fun bindLeaderboard(gameShotsLeaderboard: GameShotsLeaderboard) {
        if (context.contains<GameShotsLeaderboard>()) {
            context.removeProvider(GameShotsLeaderboard::class.java)
        }
        context.bindSingleton(gameShotsLeaderboard)
    }
}