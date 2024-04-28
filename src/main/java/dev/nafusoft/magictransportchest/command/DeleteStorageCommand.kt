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

import dev.nafusoft.magictransportchest.service.StorageService
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DeleteStorageCommand(val storageService: StorageService) : MagicSubCommand {

    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by players.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("Usage: /mtc delete <storage_id>")
            return true
        }

        val storageId = args[0]
        storageService.getStorage(storageId).let {
            if (it == null) {
                sender.sendMessage("Storage not found.")
                return true
            }
            if (it.owner != sender.uniqueId.toString()) {
                sender.sendMessage("You don't have permission to delete this storage.")
                return true
            }
            storageService.deleteStorage(storageId)
            sender.sendMessage("Storage deleted.")
        }

        return true
    }


    override fun onTabComplete(sender: CommandSender, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> storageService.getStorageByOwner((sender as Player).uniqueId.toString()).map { it.id }
            else -> emptyList()
        }
    }

    override fun getPermission(): String {
        return "mtc.storage.delete"
    }
}
