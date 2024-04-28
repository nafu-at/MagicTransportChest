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

package dev.nafusoft.magictransportchest.entities

import net.minecraft.nbt.MojangsonParser
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import net.minecraft.world.item.ItemStack as NmsStack

data class MagicItemStack(val itemStack: Map<String, Any>, val nbt: String?) : ConfigurationSerializable {

    constructor(serializedMap: Map<String, Any>) : this(
        serializedMap["itemStack"] as Map<String, Any>,
        serializedMap["nbt"] as String?
    )

    // TODO 2024-04-29 いずれNMSに依存しない実装をやる
    fun toMinecraftItemStack(): ItemStack {
        ItemStack.deserialize(itemStack).let {
            if (nbt != null) {
                MojangsonParser.a(nbt).let { nbt ->
                    val nmsStack = CraftItemStack.asNMSCopy(it)
                    nmsStack.c(nbt)
                    return CraftItemStack.asBukkitCopy(nmsStack)
                }
            }
            return it
        }
    }

    override fun serialize(): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["itemStack"] = itemStack
        if (nbt != null) {
            map["nbt"] = nbt
        }
        return map
    }

    companion object {
        fun fromMinecraftItemStack(itemStack: ItemStack): MagicItemStack {
            val nmsStack: NmsStack = CraftItemStack.asNMSCopy(itemStack)
            return MagicItemStack(itemStack.serialize(), nmsStack.v()?.toString())
        }
    }
}
