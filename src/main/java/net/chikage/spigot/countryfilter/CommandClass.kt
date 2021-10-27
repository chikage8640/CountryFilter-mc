package net.chikage.spigot.countryfilter

import com.maxmind.geoip2.DatabaseReader
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.net.InetAddress
import kotlin.io.path.Path
import kotlin.io.path.inputStream

/**
 * コマンドを取り扱う構造体
 * @author Chikage Haruse
 */
class CommandClass(
    private val plugin: CountryFilterPlugin
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        when (val commandName = command.name.lowercase()) {
            "ipinfo" -> ipInfo(sender, args.toList())
            else -> sender.sendMessage("Unknown command $commandName")
        }
        return true
    }

    private fun ipInfo(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage("The ip address is not specified.")
        } else if (!isIPv4(args[0])) {
            sender.sendMessage("The ip address is not true.")
        } else {
            val ipAddress = InetAddress.getByName(args[0])
            val geoipDb = DatabaseReader.Builder(Path(plugin.config.geoipDb).inputStream()).build()
            val ipTool = IpTools(plugin, geoipDb)
            val canConnect = ipTool.canLogin(ipAddress)
            val ipCountry = ipTool.getCountry(ipAddress)
            sender.sendMessage("[IP information]\nIP address:$ipAddress\nCountry:$ipCountry\nConnectable:$canConnect")
        }
    }

    private fun isIPv4(str: String): Boolean {
        return IP_V4_REGEX matches str
    }

    companion object {
        private val IP_V4_REGEX = "((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])([.](?!\$)|\$)){4}".toRegex()
    }
}
