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

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.InputMultiplexer
import ktx.app.KtxInputAdapter
import ro.luca1152.gravitybox.components.game.BulletComponent
import ro.luca1152.gravitybox.components.game.PlayerComponent
import ro.luca1152.gravitybox.components.game.body
import ro.luca1152.gravitybox.entities.game.BulletEntity
import ro.luca1152.gravitybox.utils.kotlin.getSingletonFor
import ro.luca1152.gravitybox.utils.kotlin.screenToWorldCoordinates
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ShootingSystem(private val inputMultiplexer: InputMultiplexer = Injekt.get()) : EntitySystem() {
    private lateinit var playerEntity: Entity
    private val inputAdapter = object : KtxInputAdapter {
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val worldCoordinates = screenToWorldCoordinates(screenX, screenY)
            createBullet(worldCoordinates.x, worldCoordinates.y)

            return true
        }
    }

    private fun createBullet(worldX: Float, worldY: Float) {
        val playerPosition = playerEntity.body.body.worldCenter
        val bullet = BulletEntity.createEntity(playerPosition.x, playerPosition.y)
        val velocity = playerEntity.body.body.worldCenter.cpy()
        velocity.sub(worldX, worldY)
        velocity.nor()
        velocity.scl(-BulletComponent.SPEED)
        bullet.body.body.linearVelocity = velocity
    }

    override fun addedToEngine(engine: Engine) {
        playerEntity = engine.getSingletonFor(Family.all(PlayerComponent::class.java).get())
        inputMultiplexer.addProcessor(inputAdapter)
    }

    override fun removedFromEngine(engine: Engine?) {
        inputMultiplexer.removeProcessor(inputAdapter)
    }
}