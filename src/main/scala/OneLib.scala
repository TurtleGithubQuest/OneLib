package dev.turtle.onelib

import command.OneCommand
import configuration.OneConfig

import dev.turtle.onelib.configuration.OneConfig.configs
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

import scala.collection.mutable

object OneLib:
  var onelib: JavaPlugin = _
  val console: ConsoleCommandSender = getConsoleSender
  def start(plugin: JavaPlugin): Boolean = {
    onelib=plugin
    OneCommand.registerCommands
    OneConfig.reloadAll
    true
  }
end OneLib

class OneLib extends JavaPlugin {
  override def onEnable(): Unit = {
    OneLib.start(this)
    //Conf.reload()
    //getCommand("grenade").setExecutor(OneCommand)
    //    new OneConfig("en_US").folder("language").copyDefaults
    //    new OneConfig("example")
    //val commandExample: OneCommand = new CommandExample

  }

  override def onDisable(): Unit = {
  }

}