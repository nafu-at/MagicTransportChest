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
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class SettingsCommand(private val settingsStore: SettingsStore) : MagicSubCommand {
    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            // Send current settings
            settingsStore.getSettings().forEach {
                sender.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}${it.key}: ${ChatColor.AQUA}${ChatColor.BOLD}${it.value}")
            }
        } else when (args[0]) {
            "get" -> { // Get a specific setting
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Usage: /mtc settings get <setting_key>")
                    return true
                }
                val settingKey = args[1]
                val settingValue = settingsStore.getSetting(settingKey)
                if (settingValue == null) {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] Setting $settingKey not found")
                } else {
                    sender.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}[MagicTransportChest] $settingKey: ${ChatColor.AQUA}${ChatColor.BOLD}$settingValue")
                }
            }

            "set" -> { // Set a specific setting
                if (args.size < 3) {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Usage: /mtc settings set <setting_key> <setting_value>")
                    return true
                }
                val settingKey = args[1]
                val settingValue = args[2]
                settingsStore.setSetting(settingKey, settingValue)
                sender.sendMessage("${ChatColor.GOLD}${ChatColor.BOLD}[MagicTransportChest] $settingKey: ${ChatColor.AQUA}${ChatColor.BOLD}$settingValue")
            }

            else -> {
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest]Unknown subcommand")
                return false
            }
        }

        return true
    }

    override fun getPermission(): String {
        return "mtc.command.settings"
    }
}
