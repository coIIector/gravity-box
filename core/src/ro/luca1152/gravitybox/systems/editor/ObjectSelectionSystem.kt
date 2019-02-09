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

package ro.luca1152.gravitybox.systems.editor

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import ro.luca1152.gravitybox.components.ColorType
import ro.luca1152.gravitybox.components.SelectedObjectComponent
import ro.luca1152.gravitybox.components.buttonListener
import ro.luca1152.gravitybox.components.color
import ro.luca1152.gravitybox.components.utils.tryGet
import ro.luca1152.gravitybox.utils.kotlin.GameStage
import ro.luca1152.gravitybox.utils.ui.ButtonType
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ObjectSelectionSystem(private val buttonListenerEntity: Entity,
                            private val gameStage: GameStage = Injekt.get()) : EntitySystem() {
    var selectedObject: Entity? = null

    private val inputListener = object : InputListener() {
        var touchedActor: Actor? = null

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchedActor = gameStage.hit(x, y, true)

            // If the touched actor is not a map object, return
            if (touchedActor == null || touchedActor?.userObject == null || touchedActor?.userObject !is Entity)
                return false

            // Return true so touchUp() will be called
            return true
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            val entity = (touchedActor!!.userObject) as Entity
            entity.run {
                if (tryGet(SelectedObjectComponent) != null) {
                    remove(SelectedObjectComponent::class.java)
                    color.colorType = ColorType.DARK
                } else {
                    add(engine.createComponent(SelectedObjectComponent::class.java))
                    color.colorType = ColorType.DARKER_DARK
                }
            }

        }
    }

    override fun addedToEngine(engine: Engine?) {
        gameStage.addListener(inputListener)
    }

    override fun removedFromEngine(engine: Engine?) {
        gameStage.removeListener(inputListener)
    }

    override fun update(deltaTime: Float) {
        selectedObject = findSelectedObject()
    }

    private fun findSelectedObject(): Entity? {
        val entities = engine.getEntitiesFor(Family.all(SelectedObjectComponent::class.java).get())
        return when {
            entities.size() == 0 -> null
            else -> entities.first()
        }
    }

    fun placeToolIsUsed() = buttonListenerEntity.buttonListener.toggledButton.get()?.type == ButtonType.PLACE_TOOL_BUTTON
}