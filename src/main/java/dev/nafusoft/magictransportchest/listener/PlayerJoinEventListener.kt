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

import dev.nafusoft.magictransportchest.MagicTransportChest
import dev.nafusoft.magictransportchest.database.SettingsStore
import dev.nafusoft.magictransportchest.database.StorageStore
import dev.nafusoft.magictransportchest.entities.MagicStorage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEventListener(val settingsStore: SettingsStore, val storageStore: StorageStore) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val writeLimit = settingsStore.getSetting("mtc.storage.enable_write_limit")?.toBoolean() ?: false
        val storages = storageStore.getStorages(event.player.uniqueId.toString())

        if (storages.isEmpty()) {
            storageStore.registerStorage(
                MagicStorage(
                    event.player.uniqueId.toString(),
                    null,
                    event.player.uniqueId.toString(),
                    MagicStorage.StorageType.BLOCK,
                    settingsStore.getSetting("mtc.storage.size")?.toInt() ?: 27,
                    if (writeLimit) MagicTransportChest.instance!!.pluginConfig.serverUniqueId else null
                )
            )
        } else if (writeLimit && storages.none { it.writeLimit.equals(MagicTransportChest.instance!!.pluginConfig.serverUniqueId) }) {
            storageStore.registerStorage(
                MagicStorage(
                    event.player.uniqueId.toString(),
                    null,
                    event.player.uniqueId.toString(),
                    MagicStorage.StorageType.BLOCK,
                    settingsStore.getSetting("mtc.storage.size")?.toInt() ?: 27,
                    MagicTransportChest.instance!!.pluginConfig.serverUniqueId
                )
            )
        }
    }
}
