package net.chikage.spigot.countryfilter

import org.bukkit.Bukkit.getLogger
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.Plugin


/**
 * ログインを検知してはじくイベントリスナー
 * @author Chikage Haruse
 */
class LoginEventListener(plugin: Plugin)  : Listener {

    @EventHandler
    fun onLogin(e: PlayerLoginEvent) { // ログイン検知
        val ipTool = IpTools(e.realAddress) // ipToolの準備
        if(!ipTool.checkIp()) { // ログイン可能なIPアドレスか確認
            // もしログイン不可能なIPアドレスだった場合、キックしてログを残す。
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessage)
            getLogger().info("${e.player.name} was kicked because it joined from ${ipTool.getCountry()}(${e.realAddress.hostAddress}).")
        }
    }

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

}