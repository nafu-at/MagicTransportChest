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

package dev.nafusoft.magictransportchest.utils

import dev.nafusoft.magictransportchest.MagicTransportChest
import dev.nafusoft.magictransportchest.command.ItemFilterSettingCommand
import org.bukkit.inventory.ItemStack

class ItemFilterChecker {

    companion object {
        private val settingsStore = MagicTransportChest.instance?.settingsStore
        private val magicItemFilterSettingGuiHolder =
            settingsStore?.let { ItemFilterSettingCommand.MagicItemFilterSettingGuiHolder(it) }

        fun check(itemStack: ItemStack): Boolean {
            if (settingsStore?.getSetting("mtc.storage.enable_item_filter")?.toBoolean() != true) return true

            val blacklist =
                (settingsStore.getSetting("mtc.storage.filter_type")?.uppercase() ?: "WHITELIST") == "BLACKLIST"
            magicItemFilterSettingGuiHolder?.inventory?.contents?.find { it.isSimilar(itemStack) }?.let {
                return !blacklist
            }
            return blacklist
        }
    }
}
