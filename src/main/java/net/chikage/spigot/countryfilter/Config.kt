package net.chikage.spigot.countryfilter

import org.bukkit.configuration.file.YamlConfiguration
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.bufferedReader

/**
 * 設定をまとめて取り扱う構造体
 * @author HimaJyun
 */
class Config (
    private val logger: Logger,
    private val path: Path,
) {
    private val config = YamlConfiguration().apply {
        load(path.bufferedReader())
    }

    init {
        reload()
    }

    /**
     * 設定をロードします
     */
    fun reload() {
        config.load(path.bufferedReader())
    }

    val geoipDb: String
        get() = config.getString("geoipDb") ?: fallback("geoipDb", DEFAULT_GEOIP_DB)

    val useWhitelist: Boolean
        get() = config.get("useWhitelist") as? Boolean? ?: fallback("useWhitelist", false)

    val useBlacklist: Boolean
        get() = config.get("useBlacklist") as? Boolean? ?: fallback("useBlacklist", false)

    val kickMessage: String
        get() = config.getString("kickMessage") ?: fallback("kickMessage", DEFAULT_KICK_MESSAGE)

    val whitelist: List<String>
        get() = config.getStringList("whitelist")

    val blacklist: List<String>
        get() = config.getStringList("blacklist")

    private fun <T> fallback(propertyName: String, fallback: T): T {
        logger.warning("property '$propertyName' is not set, fallback to $fallback")
        return fallback
    }

    companion object {
        /**
         * geoipDbのデフォルト位置
         */
        private const val DEFAULT_GEOIP_DB = "/usr/share/GeoIP/GeoLite2-City.mmdb"

        private const val DEFAULT_KICK_MESSAGE = "This server can only be connected to from authorized countries."
    }
}
