package dev.turtle.onelib
package utils.`implicit`

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import scala.collection.immutable

trait OneCommandSender {
  implicit class OneCommandSender(s: CommandSender) {

  }
}

