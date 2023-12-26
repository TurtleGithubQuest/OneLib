package dev.turtle.onelib
package message

import dev.turtle.onelib.configuration.MessagingExtension
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

object Messaging {
  implicit class MessagingString(string: String) {
    def bukkolorize: String = MessagingExtension.bukkolorize(string)
  }
  implicit class MessagingCommandSender(sender: CommandSender) {
    def getLanguage: String = MessagingExtension.getPreferredLanguage(sender.getName)
    def getLocalizedText(path: String, placeholders: Placeholders, bukkolorize: Boolean=true): String = MessagingExtension.getLocalizedText(language=getLanguage, path=path, placeholders=placeholders, bukkolorize=bukkolorize)
    def sendLocalizedMessage(path: String, placeholders: Placeholders=Placeholders(), bukkolorize: Boolean=true): Boolean = {
      sender.sendMessage(
        this.getLocalizedText(path=path, placeholders=placeholders, bukkolorize=bukkolorize)
      )
      true
    }
  }
}
