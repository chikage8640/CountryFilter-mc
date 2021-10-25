package net.chikage.spigot.countryfilter

import com.maxmind.geoip2.GeoIp2Provider
import java.net.InetAddress


/**
 * IPアドレスとGeoIP関係のクラス
 * @author Chikage Haruse
 */
class IpTools(
    plugin: CountryFilterPlugin,
    private val geoipDb: GeoIp2Provider,
) {
    private val config = plugin.config

    /**
     * IPアドレスがフィルターに引っかかっていないか確認
     */
    fun canLogin(ipAddress: InetAddress): Boolean {
        val country = getCountry(ipAddress) // 接続元の取得

        val whitelisted = config.useWhitelist && country in config.whitelist
        val blacklisted = config.useBlacklist && country in config.blacklist

        return whitelisted && !blacklisted
    }

    /**
     * IPアドレスで国を確認
     */
    fun getCountry(ipAddress: InetAddress): String {
        val ipIntList = ipAddress.hostAddress.split(".").map { it.toInt() }

        return when {
            ipIntList[0] == 0 || ipIntList[0] == 127 -> {
                // ループバックアドレス(0.0.0.0/8, 172.0.0.0/8)
                "LOOPBACK"
            }
            ipIntList[0] == 10 || ipIntList[0] == 172 && ipIntList[1] >= 16 && ipIntList[1] <= 31 || ipIntList[0] == 192 && ((ipIntList[1] == 0 && ipIntList[2] == 0) || (ipIntList[1] == 168)) || ipIntList[0] == 198 && ipIntList[1] == 18 -> {
                // プライベートアドレス(10.0.0.0/8, 172.16.0.0/12, 192.0.0.0/24, 192.168.0.0/16, 198.18.0.0/15)
                "PRIVATE"
            }
            ipIntList[0] == 192 && ipIntList[1] == 0 && ipIntList[2] == 2 || ipIntList[0] == 198 && ipIntList[1] == 51 && ipIntList[2] == 100 || ipIntList[0] == 203 && ipIntList[1] == 0 && ipIntList[2] == 113 -> {
                // TEST-NET(192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24)
                "TESTNET"
            }
            ipIntList[0] >= 224 && ipIntList[3] != 255 || ipIntList[0] == 192 && ipIntList[1] == 88 && ipIntList[2] == 99 -> {
                // 予約アドレス(192.88.99.0/24, 224.0.0.0/4, 240.0.0.0/4)
                "INTERNET"
            }
            ipIntList[0] == 100 && ipIntList[1] >= 64 && ipIntList[1] <= 127 -> {
                // シェアードアドレス(100.64.0.0/10)
                "SHARED"
            }
            ipAddress == InetAddress.getByName("255.255.255.255") || ipIntList[0] == 169 && ipIntList[1] == 254 -> {
                // サブネット(169.254.0.0/16, 255.255.255.255/32)
                "SUBNET"
            }
            else -> {
                try {
                    val cCode = geoipDb.country(ipAddress).country.isoCode // 国の取得
                    cCode
                } catch (e: Exception) {
                    // 不明なアドレス
                    "UNKNOWN"
                }
            }
        }

    }
}
