package dev.turtle.onelib
package message

import Main.console
import message.Placeholder.PlaceholderString

import Debug.Level.*
import dev.turtle.onelib.message.Debug.{Level, getCurrentLevel}
import org.bukkit.command.ConsoleCommandSender as Console

import scala.collection.immutable


object Debug {
  private var currentDebugLevel = 1
  def getCurrentLevel: Integer = this.currentDebugLevel
  def setCurrentDebugLevel(newDebugLevel: Integer): Unit = currentDebugLevel = newDebugLevel
  /**
   * 1 - 49 = Trace
   * <p> 50 - 99 = Debug
   * <p> 100 - 149 = Info
   * <p> 150 - 199 = Warn
   * <p> 200 - 249 = Error
   * <p> 250+      = Fatal
   */
  enum Level {
    case TRACE
    case DEBUG
    case INFO
    case WARN
    case ERROR
    case FATAL
  }
  private val levelMap: immutable.Map[Debug.Level, Int] = Map(
    TRACE -> 1,
    DEBUG -> 50,
    INFO -> 100,
    WARN -> 150,
    ERROR -> 200,
    FATAL -> 250
  )

  implicit class ConsoleMessage(console: Console){
    def sendDebugMessage(messageText: String, debugLevel: DebugLevel, placeholders: Placeholders): Boolean = {
      if (debugLevel.isEnabled)
        console.sendMessage(messageText.translatePlaceholders(placeholders))
      true
    }
  }
  def debugMessage(messageText: String, debugLevel: DebugLevel, placeholders: Placeholders=Placeholders()): Boolean = console.sendDebugMessage(messageText, debugLevel, placeholders)
}

/**
 * @see [[Level]]
 */
case class DebugLevel(level: Either[Level, Integer]):
  private val debugLevel: Integer = Right(level).swap.getOrElse(150)

  //def getAsInteger: Integer = levelMap.getOrElse(level.swap.getOrElse(Level.WARN), 150)

  def isEnabled: Boolean = (Debug.getCurrentLevel <= debugLevel)
end DebugLevel

object DebugLevel {
  def apply(level: Level): DebugLevel = DebugLevel(Left(level))

  def apply(value: Integer): DebugLevel = DebugLevel(Right(value))
}