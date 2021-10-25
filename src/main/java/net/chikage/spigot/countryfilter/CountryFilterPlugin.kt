package net.chikage.spigot.countryfilter

import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files
import java.nio.file.Path

class CountryFilterPlugin : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        Config(this) // コンフィグの読み出し
        if(!Files.exists(Path.of(geoipDb))) { // GeoIPのデータベースがちゃんとあるか確認
            logger.info("GeoIP database file is not Found.\nPlease check file and setting.") // 無かったら警告
        }else{
            // あったらコマンドとイベントリスナーの準備
            getCommand("ipinfo")?.setExecutor(CommandClass())
            LoginEventListener(this)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}