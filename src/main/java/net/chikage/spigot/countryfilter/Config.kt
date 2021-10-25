package net.chikage.spigot.countryfilter

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin

/**
 * 設定をまとめて取り扱う構造体
 * @author HimaJyun
 */
class Config(plugin: Plugin) {
    private val plugin: Plugin
    private var config: FileConfiguration? = null

    /**
     * 設定をロードします
     */
    fun load() {
        // 設定ファイルを保存
        plugin.saveDefaultConfig()
        if (config != null) { // configが非null == リロードで呼び出された
            plugin.reloadConfig()
        }
        config = plugin.config

        // 各設定の変数への代入
        geoipDb = config?.getString("geoipDb") ?: geoipDb
        usableWhitelist = config?.getBoolean("useWhitelist") ?: false
        usableBlacklist = config?.getBoolean("useBlacklist") ?: false
        kickMessage = config?.getString("kickMessage") ?: kickMessage
        countryWhitelist = getStringConfigList("whitelist")
        countryBlacklist = getStringConfigList("blacklist")


    }

    /**
     * 設定のリストをMutableListにする。（ホワイトリストとブラックリストの読み出し用）
     */
    private fun getStringConfigList(configListKey: String): MutableList<String> {
        val answerList: MutableList<String> = mutableListOf() // 返答用リスト
        val configList = config?.getConfigurationSection(configListKey)?.getKeys(false) // 設定の生データ
        if(configList != null){ // 設定のnullチェック
            for (key: String in configList) { // ループで整形する
                answerList.add(config!!.getString("$configListKey.$key")!!) // 値を返答用リストに追加
            }
        }
        return answerList
    }

    init {
        this.plugin = plugin
        // ロードする
        load()
    }
}