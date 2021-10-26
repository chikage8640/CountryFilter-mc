package net.chikage.spigot.countryfilter

import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.notExists

class CountryFilterPlugin : JavaPlugin() {
    val config: Config = Config(
        logger,
        dataFolder.toPath() / "config.yml",
    )

    override fun onEnable() {
        // Plugin startup logic
        if(Path(config.geoipDb).notExists()) { // GeoIPのデータベースがちゃんとあるか確認
            logger.info("GeoIP database file is not Found.\nPlease check file and setting.") // 無かったら警告
        }else{
            // あったらコマンドとイベントリスナーの準備
            getCommand("ipinfo")?.setExecutor(CommandClass(this))
            LoginEventListener(this)
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
