package net.chikage.spigot.countryfilter

// publicな変数を置いておく場所
var geoipDb: String = "/usr/share/GeoIP/GeoLite2-City.mmdb"
var usableWhitelist: Boolean = false
var usableBlacklist: Boolean = false
var kickMessage: String = "This server can only be connected to from authorized countries."
var countryWhitelist: MutableList<String> = mutableListOf()
var countryBlacklist: MutableList<String> = mutableListOf()