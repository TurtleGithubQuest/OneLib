package dev.turtle.onelib
package utils

object Exceptions {
  case class ConfigValueNotFoundException(message: String, debugLevel: Integer) extends Exception(message)
  case class ConfigPathNotFoundException(message: String, debugLevel: Integer) extends Exception(message)
  case class ConfigContainerSlotNotValidException(message: String, debugLevel: Integer) extends Exception(message)
  case class ConfigNotFoundException(message: String, debugLevel: Integer) extends Exception(message)

  case class OneAssertionError(message: String) extends AssertionError {
    def formattedMessage(prefix: String=""): String = if prefix.isEmpty then message else prefix+"."+message
  }

  def oneAssert(condition: Boolean, message: String): Unit = {
    if (!condition) throw new OneAssertionError(message)
  }
}