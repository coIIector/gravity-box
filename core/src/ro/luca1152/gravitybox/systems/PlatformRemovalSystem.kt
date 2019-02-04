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

package ro.luca1152.gravitybox.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.maps.tiled.TiledMap
import ro.luca1152.gravitybox.components.PhysicsComponent
import ro.luca1152.gravitybox.components.PlatformComponent
import ro.luca1152.gravitybox.components.platform
import ro.luca1152.gravitybox.components.utils.removeAndResetEntity

class PlatformRemovalSystem(private val map: TiledMap) : IteratingSystem(Family.all(PlatformComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (entity.platform.remove) {
            // Delete the entity, including all it's components, thus removing the Box2D body too
            engine.removeAndResetEntity(entity)
        }
    }
}