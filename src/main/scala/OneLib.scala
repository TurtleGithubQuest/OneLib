package dev.turtle.onelib

import command.OneCommand
import configuration.OneConfig
import message.Debug.{Level, debugMessage}
import message.{Debug, DebugLevel}

import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin

import scala.util.Try

object OneLib:
  var onelib: JavaPlugin = _
  val console: ConsoleCommandSender = getConsoleSender
  /**
   * 
  */
  def registerPlugin(plugin: JavaPlugin): Boolean = {
    if (Try(OneLib.onelib.isEnabled).isFailure) {
      onelib = plugin
      debugMessage(s"OneLib is running under ${plugin.getName}", debugLevel=DebugLevel(Level.INFO))
    }
    OneCommand.registerCommands
    OneConfig.reloadAll
    true
  }
end OneLib

class OneLib extends JavaPlugin {
  override def onEnable(): Unit = {
    OneLib.onelib=this
    OneLib.registerPlugin(this)
    debugMessage(s"OneLib is running in standalone mode.", debugLevel=DebugLevel(Level.INFO))
  }

  override def onDisable(): Unit = {
  }

}