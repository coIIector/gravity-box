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

package ro.luca1152.gravitybox.components.utils

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool.Poolable

/**
 * Returns the component if the has the [componentResolver].
 * Otherwise, it returns null.
 */
fun <T : Component> Entity.tryGet(componentResolver: ComponentResolver<T>): T? = componentResolver[this]

/** Removes the [entity] from the engine and resets each of its components. */
fun Engine.removeAndResetEntity(entity: Entity) {
    // Reset every component so you don't have to manually reset them for
    // each entity, such as calling world.destroyBody(entity.body.body).
    for (component in entity.components) {
        if (component is Poolable)
            component.reset()
        entity.remove(component::class.java)
    }

    // Call the default removeEntity() function
    this.removeEntity(entity)
}