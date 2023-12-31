package dev.turtle.onelib

package message

import OneLib.console
import message.Placeholder.PlaceholderString
import message.StylizedText.bukkolorize

import org.bukkit.command.ConsoleCommandSender as Console

import scala.collection.immutable


object Debug {
  private var prefix = s"&c&l[&f${OneLib.onelib.getName}&c&l]&b"
  private var suffix = ""
  private var syntax = "%prefix%: %message%%suffix%"

  implicit class ConsoleMessage(console: Console){
    def sendDebugMessage(messageText: String, debugLevel: Integer, placeholders: Placeholders, withPrefix: Boolean = true, withSuffix: Boolean = true): Boolean = {
      if (DebugLevel.isActive(debugLevel))
        console.sendMessage(bukkolorize
          (
            syntax
              .replace("%message%",messageText.translatePlaceholders(placeholders))
              .replace("%prefix%", getPrefix(withPrefix))
              .replace("%suffix%", getSuffix(withSuffix))
          )
        )
      true
    }
  }

  /**
   * 1 - 49 = Trace
   * <p> 50 - 99 = Debug
   * <p> 100 - 149 = Info
   * <p> 150 - 199 = Warn
   * <p> 200 - 249 = Error
   * <p> 250+      = Fatal
   */
  case class debugMessage(messageText: String) {
    private var debugLevel: Integer = DebugLevel.FATAL
    private var placeholders: Placeholders = Placeholders()
    private var withPrefix = true
    private var withSuffix = true

    def level(debugLevel: Integer): this.type = {
      this.debugLevel = debugLevel
      this
    }

    def placeholders(placeholders: Placeholders): this.type = {
      this.placeholders = placeholders
      this
    }

    def withPrefix(boolean: Boolean): this.type = {
      this.withPrefix = boolean; this
    }

    def withSuffix(boolean: Boolean): this.type = {
      this.withSuffix = boolean; this
    }
    {
      console.sendDebugMessage(messageText, debugLevel, placeholders, withPrefix, withSuffix)
    }
  }
  def getPrefix(yes: Boolean=true): String = if yes then this.prefix else ""
  def getSuffix(yes: Boolean=true): String = if yes then this.suffix else ""
  def getSyntax: String = this.syntax
  def setPrefix(newVal: String): Unit = this.prefix = newVal
  def setSuffix(newVal: String): Unit = this.suffix = newVal
  def setSyntax(newVal: String): Unit = this.syntax = newVal
}

object DebugLevel {
  private var currentDebugLevel = 1

  val TRACE = 1
  val DEBUG = 50
  val INFO = 100
  val WARN = 150
  val ERROR = 200
  val FATAL = 250

  def isActive(int: Int): Boolean = (this.currentDebugLevel <= int)

  def get: Integer = this.currentDebugLevel

  def set(newDebugLevel: Integer): Unit = this.currentDebugLevel = newDebugLevel
}