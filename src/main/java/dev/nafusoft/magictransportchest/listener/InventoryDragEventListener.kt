/*
 * Copyright 2024 Nafu Satsuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nafusoft.magictransportchest.listener

import dev.nafusoft.magictransportchest.entities.MagicInventoryHolder
import dev.nafusoft.magictransportchest.utils.ItemFilterChecker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryDragEventListener : Listener {

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (event.inventory.holder is MagicInventoryHolder) {
            event.newItems.forEach { (_, item) ->
                if (!ItemFilterChecker.check(item)) {
                    event.isCancelled = true
                    return
                }
            }
        }
    }
}
