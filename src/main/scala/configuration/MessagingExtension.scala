package dev.turtle.onelib
package configuration

import message.Placeholders
import message.Messaging.MessagingString
import message.Placeholder.PlaceholderString

import scala.collection.{immutable, mutable}
import dev.turtle.onelib.configuration.OneConfig.{config, option}
import org.bukkit.ChatColor


object MessagingExtension {
  var preferredLanguages: mutable.Map[String, String] = mutable.Map.empty
  private val _defaultLanguage: String = "en_US"
  //TODO: Add option to retrieve lang each player is using.
  def defaultLanguage: String = _defaultLanguage
  def getPreferredLanguage(name: String): String = preferredLanguages.getOrElse(name, defaultLanguage)
  def getLocalizedText(language: String, path: String, placeholders: Placeholders, bukkolorize: Boolean=true): String = {
    var outputText: String = {
      try {
        option(language, folder="language").getOrElse(config(_defaultLanguage)).getString(path)
      } catch {
        case e: Throwable =>
          path
      }
    }
    if (!placeholders.isEmpty)
      outputText=outputText.translatePlaceholders(placeholders)
    if (bukkolorize)
      outputText=outputText.bukkolorize
    outputText
  }
  /**
   * @todo HEX color codes
   * @return Colorized text
  */
  def bukkolorize(text: String): String = ChatColor.translateAlternateColorCodes('&', text)
}
