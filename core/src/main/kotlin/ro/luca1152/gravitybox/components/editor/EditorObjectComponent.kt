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

package ro.luca1152.gravitybox.components.editor

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool.Poolable
import ro.luca1152.gravitybox.utils.components.ComponentResolver

class EditorObjectComponent : Component, Poolable {
    var isSelected = false
    var isResizing = false
    var isRotating = false
    var isDraggingHorizontally = false
    var isDraggingVertically = false
    val isDragging
        get() = isDraggingHorizontally || isDraggingVertically

    override fun reset() {
        isSelected = false
        isDraggingHorizontally = false
        isDraggingVertically = false
        isRotating = false
        isResizing = false
    }

    companion object : ComponentResolver<EditorObjectComponent>(EditorObjectComponent::class.java)
}

val Entity.editorObject: EditorObjectComponent
    get() = EditorObjectComponent[this]