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
import dev.nafusoft.magictransportchest.entities.MagicStorage
import dev.nafusoft.magictransportchest.service.StorageService
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class CreateStorageCommand(val storageService: StorageService, val settingsStore: SettingsStore) : MagicSubCommand {

    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by players.")
            return true
        }

        val storageLimit = settingsStore.getSetting("mtc.storage.max_number_per_player")?.toInt() ?: 1
        storageService.getStorageByOwner(sender.uniqueId.toString()).let {
            if (it.size >= storageLimit) {
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You have reached the maximum number of storages.")
                return true
            }

            val storageId = UUID.randomUUID().toString()
            val storageName = args.getOrElse(0) { storageId }
            val storageSize = settingsStore.getSetting("mtc.storage.size")?.toInt() ?: 27

            storageService.setStorage(
                storageId,
                storageName,
                sender.uniqueId.toString(),
                MagicStorage.StorageType.VIRTUAL,
                storageSize
            )

            sender.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}[MagicTransportChest] Storage created.")
        }

        return true
    }
}
