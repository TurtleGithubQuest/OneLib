package dev.turtle.onelib

package api

import OneLib.onelib
import message.Placeholder.PlaceholderString
import message.Placeholders
import message.StylizedText.*

import scala.collection.{immutable, mutable}

class OneLanguageAPI(private val oneLibAPI: OneLibAPI) {
  def playerLanguage(playerName: String): String = OneLanguageAPI.playerLanguage(playerName, pluginName = oneLibAPI.plugin.name)

  def getLocalizedText(language: String, path: Seq[String], placeholders: Placeholders, bukkolorize: Boolean = true): String = {
    val path_as_string = path.mkString(".")
    var outputText: String = {
      try {
        val cfg = oneLibAPI.config.configName(language).folder("language")
        "NONE" //TODO: option(language, folder="language").getOrElse(config(_defaultLanguage)).getString(path)
      } catch {
        case e: Throwable =>
          path_as_string
      }
    }
    if (!placeholders.isEmpty)
      outputText = outputText.translatePlaceholders(placeholders)
    if (bukkolorize)
      outputText = outputText.bukkolorize
    outputText
  }
}

object OneLanguageAPI:
  def defaultLanguage(pluginName: String): String = onelib.oneLibAPI.config.section(pluginName, "language").getString("default")

  def playerLanguage(playerName: String, pluginName: String): String = _playerLanguage.getOrElse(playerName, defaultLanguage(pluginName))

  //TODO: Load player language settings
  private val _playerLanguage: mutable.Map[String, String] = mutable.Map.empty
