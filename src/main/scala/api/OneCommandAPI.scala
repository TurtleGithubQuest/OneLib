package dev.turtle.onelib

package api

import command.OneCommand
import message.Debug.debugMessage
import message.DebugLevel.TRACE

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap

import scala.collection.mutable

class OneCommandAPI(private val oneLibAPI: OneLibAPI):
  val commands: mutable.Map[String, OneCommand] = mutable.Map.empty
  private val commandMap: CommandMap = try {
    val bukkitCommandMap = Bukkit.getServer.getClass.getDeclaredField("commandMap")
    bukkitCommandMap.setAccessible(true)
    bukkitCommandMap.get(Bukkit.getServer).asInstanceOf[CommandMap]
  } catch {
    case e: Exception =>
      e.printStackTrace()
      null
  }

  /**
   * Adds command to OneLib's memory and tells Bukkit that it is available.
   */
  def register(oneCommand: OneCommand): Boolean = {
    commandMap.register(oneCommand.getName, oneCommand)
    debugMessage(s"Loaded command '${oneCommand.getName}' containing '${oneCommand.arguments.size}' argument(s).").level(TRACE)
    true
  }

  def add(commands: OneCommand*): Boolean = {
    commands.foreach(cmd => {
      this.commands.put(cmd.commandName, cmd)
    })
    true
  }

  /**
   * Each command has to be instantiated before calling this, otherwise it won't be registered.
   */
  def registerAll: Boolean = {
    commands.foreach(oneCommand => oneCommand._2.register)
    debugMessage(s"Loaded ${commands.keys.size} commands.").level(TRACE)
    true
  }
end OneCommandAPI
