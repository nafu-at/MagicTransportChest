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

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import redis.clients.jedis.Jedis

class CleanSubCommand(val jedis: Jedis) : MagicSubCommand {
    override fun onCommand(sender: CommandSender, args: Array<out String>): Boolean {
        jedis.keys("open:*").forEach {
            jedis.del(it)
        }
        sender.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}[MagicTransportChest] Cleaned all opened inventories.")
        return true
    }

    override fun getPermission(): String {
        return "mtc.command.clean"
    }
}
