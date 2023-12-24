package dev.turtle.onelib

import command.{CommandExample, OneCommand}
import configuration.OneConfig

import dev.turtle.onelib.configuration.OneConfig.configs
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable

object Main:
  var plugin: JavaPlugin = _
  val console: ConsoleCommandSender = getConsoleSender
end Main

class Main extends JavaPlugin {
  override def onEnable(): Unit = {
    Main.plugin = this
    //Conf.reload()
    //getCommand("grenade").setExecutor(OneCommand)
    val commandExample: OneCommand = new CommandExample
    OneCommand.registerCommands
    new OneConfig("en_US").folder("language").copyDefaults
    new OneConfig("example")
    OneConfig.reloadAll
  }

  override def onDisable(): Unit = {
  }

}