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
import dev.nafusoft.magictransportchest.entities.MagicInventoryHolder
import dev.nafusoft.magictransportchest.service.StorageService
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class OpenStorageCommand(val storageService: StorageService, val settingsStore: SettingsStore) : MagicSubCommand {
    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] Only players can use this command")
        } else if (sender.hasPermission(
                settingsStore.getSetting("mtc.storage.use_storage_permission_node") ?: "mtc.storage.use"
            )
        ) {
            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] Usage: /mtc open <storage_id>")
                return true
            } else when (val storageId = args[0]) {
                in storageService.getStorageByOwner(sender.uniqueId.toString()).map { it.id } -> {
                    storageService.getStorage(storageId).let {
                        if (it == null) {
                            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] Storage not found.")
                            return true
                        }
                        sender.openInventory(MagicInventoryHolder(storageService, storageId).inventory)
                    }
                }

                "list" -> {
                    storageService.getStorageByOwner(sender.uniqueId.toString()).forEach {
                        sender.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}${it.name} (${it.id})")
                    }
                }

                else -> {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You don't have permission to use this storage.")
                }
            }
        } else {
            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You don't have permission to use this command.")
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        if (sender !is Player) {
            return emptyList()
        }
        return when (args.size) {
            1 -> {
                val storageIds = storageService.getStorageByOwner(sender.uniqueId.toString()).map { it.id }
                storageIds + "list"
            }

            else -> emptyList()
        }
    }
}
