package dev.nafusoft.magictransportchest

import dev.nafusoft.magictransportchest.MagicTransportChestConfig.ConfigLoader
import dev.nafusoft.magictransportchest.command.*
import dev.nafusoft.magictransportchest.database.*
import dev.nafusoft.magictransportchest.entities.MagicInventory
import dev.nafusoft.magictransportchest.entities.MagicInventoryHolder
import dev.nafusoft.magictransportchest.entities.MagicItemStack
import dev.nafusoft.magictransportchest.listener.*
import dev.nafusoft.magictransportchest.service.StorageService
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import redis.clients.jedis.JedisPool


class MagicTransportChest : JavaPlugin() {
    private var config: MagicTransportChestConfig? = null

    var connector: DatabaseConnector? = null
        private set
    var storageStore: StorageStore? = null
        private set
    var chestLocationStore: ChestLocationStore? = null
        private set
    var inventoryStore: InventoryStore? = null
        private set
    var settingsStore: SettingsStore? = null
        private set
    var jedisPool: JedisPool? = null
        private set

    val subCommands = mutableMapOf<String, MagicSubCommand>()

    val pluginConfig: MagicTransportChestConfig
        get() {
            if (config == null) config = ConfigLoader(instance!!).loadConfig()
            return config!!
        }

    override fun onEnable() {
        // Plugin startup logic
        init()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        shutdown()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("magictransportchest", ignoreCase = true)) {
            if (args.isEmpty()) {
                val version = description.version
                sender.sendMessage("MagicTransportChest v$version")
                return true
            }

            if (args[0].equals("reload", ignoreCase = true)) {
                if (!sender.hasPermission("mtc.reload")) {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You don't have permission to use this command.")
                    return true
                }

                // Shutdown plugin and reload
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] Reloading plugin...")
                sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}[MagicTransportChest] Shutting down plugin...")
                shutdown()

                sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}[MagicTransportChest] Reloading plugin config...")
                reloadPluginConfig()
                sender.sendMessage("${ChatColor.AQUA}${ChatColor.BOLD}[MagicTransportChest] Plugin config reloaded.")

                // Reload database connection
                sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}[MagicTransportChest] Reconnecting to database...")
                init()
                sender.sendMessage("${ChatColor.AQUA}${ChatColor.BOLD}[MagicTransportChest] Database reconnected.")

                sender.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}[MagicTransportChest] Plugin reloaded.")
            } else {
                val subCommand = subCommands[args[0]]
                if (subCommand != null) {
                    if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission()!!)) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[MagicTransportChest] You don't have permission to use this command.")
                        return true
                    }
                    return subCommand.onCommand(sender, args.copyOfRange(1, args.size))
                }
            }
        }

        return true
    }

    private fun init() {
        connector = DatabaseConnector(pluginConfig.database.toHikariConfig())
        storageStore = StorageStore(connector!!)
        chestLocationStore = ChestLocationStore(connector!!)
        inventoryStore = InventoryStore(connector!!)
        settingsStore = SettingsStore(connector!!)

        val storageService = StorageService(storageStore!!, chestLocationStore!!, inventoryStore!!, settingsStore!!)

        // Connect to Redis
        jedisPool = JedisPool(pluginConfig.redis.host, pluginConfig.redis.port)
        val jedis = jedisPool!!.resource
        jedis.select(pluginConfig.redis.database)

        getCommand("magictransportchest")?.setExecutor(this)
        subCommands["clean"] = CleanSubCommand(jedis)
        subCommands["settings"] = SettingsCommand(settingsStore!!)
        subCommands["filter"] = ItemFilterSettingCommand(settingsStore!!)
        subCommands["create"] = CreateStorageCommand(storageService, settingsStore!!)
        subCommands["delete"] = DeleteStorageCommand(storageService)
        subCommands["open"] = OpenStorageCommand(storageService, settingsStore!!)
        server.pluginManager.registerEvents(InventoryOpenEventListener(jedis), this)
        server.pluginManager.registerEvents(InventoryCloseEventListener(jedis), this)
        server.pluginManager.registerEvents(InventoryClickEventListener(), this)
        server.pluginManager.registerEvents(InventoryMoveItemEventListener(), this)
        if (pluginConfig.storage.createDefault)
            server.pluginManager.registerEvents(PlayerJoinEventListener(settingsStore!!, storageStore!!), this)

        ConfigurationSerialization.registerClass(MagicInventory::class.java)
        ConfigurationSerialization.registerClass(MagicItemStack::class.java)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (command.name.equals("magictransportchest", ignoreCase = true) && args != null && args.size == 1) {
            return subCommands.keys.filter { it.startsWith(args[0]) }.toMutableList()
        } else if (args != null && args.isNotEmpty()) {
            val subCommand = subCommands[args[0]]
            if (subCommand != null)
                return subCommand.onTabComplete(sender, args.copyOfRange(1, args.size)).toMutableList()
        }
        return null
    }

    private fun shutdown() {
        openedInventories.forEach { it.player.closeInventory() }
        openedInventories.clear()

        subCommands.clear()

        server.scheduler.cancelTasks(this)
        server.scheduler.cancelTasks(this)
        HandlerList.unregisterAll(this) // Unregister all event listeners
        this.description.commands.keys.forEach { getCommand(it)?.setExecutor(null) } // Unregister all commands

        if (jedisPool != null) jedisPool!!.close() // Close Redis connection
        if (connector != null) connector!!.close() // Close database connection

        jedisPool = null
        connector = null
    }

    private fun reloadPluginConfig() {
        config = ConfigLoader(instance!!).loadConfig()
    }

    companion object {
        var instance: MagicTransportChest? = null
            get() {
                if (field == null) field =
                    Bukkit.getServer().pluginManager.getPlugin("MagicTransportChest") as MagicTransportChest?
                return field
            }
            private set

        var openedInventories = arrayListOf<MagicInventoryHolder>()
            private set
    }
}
