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

package dev.nafusoft.magictransportchest.command

import dev.nafusoft.magictransportchest.database.SettingsStore
import dev.nafusoft.magictransportchest.entities.MagicInventory
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class ItemFilterSettingCommand(private val settingsStore: SettingsStore) : MagicSubCommand {

    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Only players can use this command")
        } else if (sender.hasPermission("mtc.command.item_filter")) {
            sender.openInventory(MagicItemFilterSettingGuiHolder(settingsStore).inventory)
        } else {
            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You don't have permission to use this command.")
        }
        return true
    }

    override fun getPermission(): String {
        return "mtc.command.item_filter"
    }

    class MagicItemFilterSettingGuiHolder(private val settingsStore: SettingsStore) : InventoryHolder {

        override fun getInventory(): Inventory {
            val inventory = settingsStore.getSetting("mtc.storage.item_filter")?.let { MagicInventory.fromString(it) }
            if (inventory == null || inventory.storageId != UI_ID) {
                return MagicInventory(UI_ID, 54).toMinecraftInventory(this)
            }
            return inventory.toMinecraftInventory(this)
        }

        fun saveInventory(inventory: Inventory) {
            settingsStore.setSetting(
                "mtc.storage.item_filter",
                MagicInventory.fromMinecraftInventory(UI_ID, inventory).toString()
            )
        }


        companion object {
            const val UI_ID = "f00963b5-67f8-495b-92fb-be9d12de8e5d"
        }
    }
}
