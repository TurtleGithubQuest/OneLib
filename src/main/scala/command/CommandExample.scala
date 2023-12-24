package dev.turtle.onelib
package command

import configuration.OneConfig.config
import message.Messaging.MessagingCommandSender
import message.{Placeholder, Placeholders}

import dev.turtle.onelib.utils.Exceptions.oneAssert
import org.bukkit
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

//TODO: Tab Completion
class CommandExample extends OneCommand("example") {
  this.Suggestion(0, Array("ll"))
  /**
   *    /example launch <power> <player>
   */
  new Argument("launch") {
    override def onArgument(sender: CommandSender): Boolean = {
      // Cast is already checked for correct type
      val power = getInputValue(0).get.toInt
      val maxPower = config("example").getInt(path("power.max"))
      oneAssert(power <= maxPower, sender.getLocalizedText(path("power-limit-reached"), Placeholder("maxpower", maxPower).s))
      val target: Player = Bukkit.getPlayer(getInputValue(1).getOrElse(sender.getName))
      if (target.isOnline) {
        target.setVelocity(new bukkit.util.Vector(0, power, 0))
        sender.sendLocalizedMessage(
          path(sauce="success"),
          Placeholders(
            Placeholder("target", target.getName),
            Placeholder("power", power)
          )
        )
      }
      true
    }
    this.Suggestion(2, "1,10,15,25,50".split(","))
  }.setInputs(
     ArgumentInput("power", classOf[Integer]), ArgumentInput("player", isRequired=false))
   .requiredPermission(path("launch"))
}
