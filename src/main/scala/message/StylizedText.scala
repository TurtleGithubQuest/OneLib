package dev.turtle.onelib

package message

import org.bukkit.ChatColor

import scala.collection.{immutable, mutable}


object StylizedText {
  /**
   * @todo HEX color codes
   * @return Colorized text
   */
  def bukkolorize(text: String): String = ChatColor.translateAlternateColorCodes('&', text)

  implicit class StylizedString(string: String) {
    def bukkolorize: String = StylizedText.bukkolorize(string)
  }
}
