package dev.turtle.onelib

import api.OnePlugin
import command.OneCommand
import message.Debug.debugMessage
import message.DebugLevel.INFO
import message.{Debug, DebugLevel}

import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.command.ConsoleCommandSender

import scala.util.Try

object OneLib:
  var onelib: OnePlugin = _
  val console: ConsoleCommandSender = getConsoleSender
  /**
   * 
  */
  def registerPlugin(plugin: OnePlugin, onecommands: Seq[OneCommand]): Boolean = {
    if (isOneLibRunning) {
      onelib = plugin
      debugMessage(s"OneLib is running under ${plugin.getName}").level(INFO)
    } else if plugin ne this.onelib then debugMessage(s"Plugin '${plugin.getName}' successfully hooked into OneLib.").level(INFO)
    plugin.oneLibAPI.command.registerAll
    //OneConfig.reloadAll
    true
  }

  protected def isOneLibRunning: Boolean = Try(OneLib.onelib.isEnabled).isFailure
end OneLib

class OneLib extends OnePlugin {
  override def onEnable(): Unit = {
    OneLib.onelib=this
    OneLib.registerPlugin(this, Seq())
    debugMessage(s"OneLib is running in standalone mode.").level(INFO)
  }

  override def onDisable(): Unit = {
  }

}