package dev.turtle.onelib

package message

import api.{OneLanguageAPI, OneLibAPI}
import message.StylizedText

import org.bukkit.command.{CommandSender, ConsoleCommandSender}

implicit class OneCommandSender(commandSender: CommandSender):
  protected var _oneLibAPI: OneLibAPI = _

  def test(oneLibAPI: OneLibAPI): this.type = {
    this._oneLibAPI = oneLibAPI
    this
  }

  def getLanguage: String = OneLanguageAPI.playerLanguage(commandSender.getName, pluginName = _oneLibAPI.plugin.name)

  def getLocalizedText(path: Seq[String], placeholders: Placeholders, bukkolorize: Boolean = true): String
  = OneLanguageAPI(_oneLibAPI).getLocalizedText(language = getLanguage, path = path, placeholders = placeholders, bukkolorize = bukkolorize)

  /**
   * @param path : Either String (text won't be fetched from config) or String Sequence
   */
  def sendLocalizedMessage(path: Any /*Either[String, Seq[String]]*/ , placeholders: Placeholders = Placeholders(), bukkolorize: Boolean = true): Boolean = {
    commandSender.sendMessage(
      this.getLocalizedText(
        path = //path.getOrElse(Seq(path.left.getOrElse("OneLib: sendLocalizedMessage -> Either is null."))),
        {
          path match
            case value: Seq[String] => value
            case _ => Seq(path.toString)
        },
        placeholders = placeholders,
        bukkolorize = bukkolorize,
      )
    )
    true
  }
end OneCommandSender

