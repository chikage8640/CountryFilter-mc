package net.chikage.spigot.countryfilter

import com.maxmind.geoip2.DatabaseReader
import java.io.File
import java.net.InetAddress


/**
 * IPアドレスとGeoIP関係のクラス
 * @author Chikage Haruse
 */
class IpTools(val ipAddress: InetAddress) {
    val geoipDatabaseFile = File(geoipDb)
    val reader: DatabaseReader = DatabaseReader.Builder(geoipDatabaseFile).build()
    val ipIntList= ipAddress.hostAddress.split(".").map { it.toInt() }

    /**
     * IPアドレスがフィルターに引っかかっていないか確認
     */
    fun checkIp(): Boolean {
        val country = getCountry() // 接続元の取得
        var response = true // 返答の準備

        if(usableWhitelist) { // ホワイトリストのチェック
            response = countryWhitelist.contains(country)
        }

        if(usableBlacklist && response)  { // ブラックリストのチェック
            response = !countryBlacklist.contains(country)
        }

        return response
    }

    /**
     * IPアドレスで国を確認
     */
    fun getCountry(): String? {
        return if(ipIntList[0] == 0 || ipIntList[0] == 127) {
            // ループバックアドレス(0.0.0.0/8, 172.0.0.0/8)
            "LOOPBACK"
        } else if((ipIntList[0] == 10) || (ipIntList[0] == 172 && ipIntList[1] >= 16 && ipIntList[1] <= 31) || (ipIntList[0] == 192 && ((ipIntList[1] == 0 && ipIntList[2] == 0) || (ipIntList[1] == 168))) || (ipIntList[0] == 198 && ipIntList[1] == 18)) {
            // プライベートアドレス(10.0.0.0/8, 172.16.0.0/12, 192.0.0.0/24, 192.168.0.0/16, 198.18.0.0/15)
            "PRIVATE"
        } else if((ipIntList[0] == 192 && ipIntList[1] == 0 && ipIntList[2] == 2) || (ipIntList[0] == 198 && ipIntList[1] == 51 && ipIntList[2] == 100) || (ipIntList[0] == 203 && ipIntList[1] == 0 && ipIntList[2] == 113)) {
            // TEST-NET(192.0.2.0/24, 198.51.100.0/24, 203.0.113.0/24)
            "TESTNET"
        } else if((ipIntList[0] >= 224 && ipIntList[3] != 255) || (ipIntList[0] == 192 && ipIntList[1] == 88 && ipIntList[2] == 99)) {
            // 予約アドレス(192.88.99.0/24, 224.0.0.0/4, 240.0.0.0/4)
            "INTERNET"
        }else if(ipIntList[0] == 100 && ipIntList[1] >= 64 && ipIntList[1] <= 127) {
            // シェアードアドレス(100.64.0.0/10)
            "SHARED"
        }else if(ipAddress == InetAddress.getByName("255.255.255.255") || (ipIntList[0] == 169 && ipIntList[1] == 254)) {
            // サブネット(169.254.0.0/16, 255.255.255.255/32)
            "SUBNET"
        } else{
            try {
                val cCode = reader.country(ipAddress).country.isoCode // 国の取得
                cCode
            } catch (e: Exception) {
                // 不明なアドレス
                "UNKNOWN"
            }

        }

    }
}