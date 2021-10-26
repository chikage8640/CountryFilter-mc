package net.chikage.spigot.countryfilter

import com.maxmind.geoip2.DatabaseReader
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import kotlin.io.path.Path
import kotlin.io.path.inputStream


/**
 * ログインを検知してはじくイベントリスナー
 * @author Chikage Haruse
 */
class LoginEventListener(
    private val plugin: CountryFilterPlugin
) : Listener {
    private val config = plugin.config

    @EventHandler
    fun onPreLogin(e: AsyncPlayerPreLoginEvent) { // ログイン検知
        val geoipDb = DatabaseReader.Builder(Path(config.geoipDb).inputStream()).build() // こういう依存するものは使う側で用意するほうがいい
        val ipTools = IpTools(plugin, geoipDb)

        val ipAddress = e.address

        if(ipTools.canLogin(e.address)) { // ログイン可能なIPアドレスか確認
            // ログイン不能なIPアドレスだった場合、キックしてログを残す.
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, config.kickMessage)
            plugin.logger.info("${e.name} was kicked because it joined from ${ipTools.getCountry(ipAddress)}(${ipAddress.hostAddress}).")
        }
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
}
