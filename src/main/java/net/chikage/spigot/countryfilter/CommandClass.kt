package net.chikage.spigot.countryfilter

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.net.InetAddress
import java.util.regex.Pattern

/**
 * コマンドを取り扱う構造体
 * @author Chikage Haruse
 */
class CommandClass : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("ipinfo", ignoreCase = true)) {
            return if (args.isEmpty()) {
                sender.sendMessage("The ip address is not specified.")
                true
            } else if (!isIPv4(args[0])) {
                sender.sendMessage("The ip address is not true.")
                true
            } else {
                val ipAddress = args[0]
                val ipTool = IpTools(InetAddress.getByName(ipAddress))
                val canConnect = ipTool.checkIp()
                val ipCountry = ipTool.getCountry()
                sender.sendMessage("[IP information]\nIP address:$ipAddress\nCountry:$ipCountry\nConnectable:$canConnect")
                true
            }
        }
        return false
    }

    private fun isIPv4(str: String?): Boolean {
        return Pattern.matches("((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])([.](?!$)|$)){4}", str)
    }
}