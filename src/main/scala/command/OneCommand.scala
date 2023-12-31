package dev.turtle.onelib

package command

import OneLib.console
import api.OneLibAPI
import message.{Debug, OneCommandSender, Placeholder, Placeholders}
import utils.Exceptions.{OneAssertionError, oneAssert}

import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand

import java.util
import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.language.postfixOps
import scala.util.Try


class OneCommand(oneCommandName: String, oneLibAPI: OneLibAPI) extends BukkitCommand(oneCommandName) {
  val arguments: mutable.Map[String, Argument] = mutable.Map.empty
  private[this] var suggestions: mutable.Map[String, mutable.Map[Integer, Suggestion]] = mutable.Map.empty
  //lazy val getArguments: mutable.Map[String, Argument] = arguments
  /**
   * @param Integer Position index
   * @param Array Available arguments which may be located on this index.
   */
  override def tabComplete(sender: CommandSender, alias: String, args: Array[String]): util.List[String] = {
    val suggestionArray: Array[String] = {
      val lastOption: Option[String] = args.map(_.trim).filterNot(_.isEmpty).lastOption
      if (lastOption.isDefined) {
        val last: String = lastOption.get.toLowerCase
        val argumentOption: Option[Argument] = arguments.get(last)
        if (argumentOption.isDefined) then
          val arg: Argument = argumentOption.get
          val suggestionOption = arg.getSuggestion(args.length)
          if suggestionOption.isDefined then
            suggestionOption.get.getSuggestions
          else
            Array.empty
        else {
          val suggestionOption = suggestions.get(commandName)
          if suggestionOption.isDefined && suggestionOption.get.contains(args.length) then
            val suggestion = suggestionOption.get
            suggestion(args.length).getSuggestions
          else
            Array.empty
        }
      } else
        if Try(suggestions(commandName)(0).getSuggestions).isSuccess then suggestions(commandName)(0).getSuggestions else arguments.keys.toArray
    }
    java.util.Arrays.asList(suggestionArray: _*)
  }
  override def execute(sender: CommandSender,  alias: String, commandArguments: Array[String]): Boolean = {
    for (arg <- commandArguments) {
      val argumentOption: Option[Argument] = arguments.get(arg)
      if (argumentOption.isDefined) {
        val arg: Argument = argumentOption.get
        if (commandArguments.length >= arg.getRequiredInputs) {
          val position: Integer = 0
          //TODO: Optional position
          if (commandArguments(position).equalsIgnoreCase(arg.getName)) {
            val allInputsValid: Boolean = arg.getInputs.zipWithIndex forall{ case(input, index) => {
              input.inputType.isAssignableFrom(classOf[String])
              || (input.inputType.isAssignableFrom(classOf[Integer]) && Try(commandArguments(index+1).toDouble).isSuccess)
              || (input.inputType.isAssignableFrom(classOf[Boolean]) && Try(commandArguments(index+1).toBoolean).isSuccess)
            }
            }
            if (allInputsValid) {
              arg.setInputValues(
                commandArguments.slice(position + 1, commandArguments.length)
                  .map(value => Option(value))
                  .padTo(arg.getInputs.length, Option.empty)
              )
              try {
                oneAssert(sender.hasPermission(arg.getRequiredPermission), arg.path("no-permissions"))
                arg.onArgument(sender)
              } catch {
                case e: OneAssertionError =>
                  val placeholders: Placeholders = Placeholders(
                    Placeholder("permission", arg.getRequiredPermission), Placeholder("sender", sender.getName)
                  )
                  sender.sendLocalizedMessage(e.formattedMessage(), placeholders)
                  if (arg.shouldInformConsole /*TODO: && config(defaultLanguage).isPathPresent(e.formattedMessage("console"))*/) then
                    console.asInstanceOf[CommandSender].sendLocalizedMessage(e.formattedMessage("console"), placeholders)
              }
            }
          }
        }
        else {
            sender.sendMessage(arg.usage)
          }
      }
    }
    true
  }
  final val commandName: String = this.oneCommandName
  def path(sauce: String): String = Array(commandName, sauce).mkString(".")
  def className: String = {
    super.getClass.getSimpleName.toLowerCase.replaceAll("\\$", "")
  }
  case class ArgumentInput(inputDisplayName: String, inputType: Class[?]=classOf[String], isRequired: Boolean=true)
  protected abstract class Argument(argumentName: String) {
    private[this] var registerArgument: Boolean = true
    private[this] var argumentInputs: Array[ArgumentInput] = Array.empty
    private[this] var argumentInputValues: Array[Option[String]] = Array.empty
    private[this] var requiredPermission: String = ""
    private[this] var informConsole: Boolean = true
    private[this] var requiredInputs: Int = 0

    def onArgument(sender: CommandSender): Boolean
    def register(boolean: Boolean): this.type = {
      this.registerArgument = boolean
      this
    }
    /**
     * @group SET
     */
    sealed case class Suggestion(index: Integer, array: Array[String], suggestionPermission: String=requiredPermission) {
      OneCommand.this.Suggestion(s"$commandName.$argumentName".toLowerCase, index).suggestionArray(array).requiredPermission(suggestionPermission)
    }
    def requiredPermission(permission: String): this.type = {
      this.requiredPermission=permission
      this
    }
    def informConsole(boolean: Boolean): this.type = {
      this.informConsole = boolean
      this
    }
    def setInputs(argumentInput: ArgumentInput*): this.type = {
      this.argumentInputs ++= argumentInput
      argumentInput.foreach(argInput => {
        if (argInput.isRequired) this.requiredInputs += 1
      })
      this
    }
    def setInputValues(newValues: Array[Option[String]]): Boolean = {
      this.argumentInputValues = newValues
      true
    }

    /**
     * @group GET
     */
    def getName: String = argumentName
    final val className: String = this.getClass.getSimpleName
    def getCommand: OneCommand = OneCommand.this
    def path(sauce: String = ""): String = Array(commandName, argumentName, sauce).mkString(".")
    final def usage: String = {
      var usage = s"/${getCommand.commandName} "
      for (input <- argumentInputs)
        usage += input.inputDisplayName + " "
      usage
    }
    def getRequiredInputs: Integer = this.requiredInputs
    def getRequiredPermission: String = this.requiredPermission
    def shouldInformConsole: Boolean = this.informConsole
    def getInputs: Array[ArgumentInput] = argumentInputs
    def getInput(index: Integer): ArgumentInput = getInputs(index)
    def getInputValue(index: Integer): Option[String] = this.argumentInputValues(index)
    def getSuggestion(index: Integer): Option[OneCommand.this.Suggestion] = suggestions(s"$commandName.$argumentName".toLowerCase).get(index)
    if registerArgument then
      arguments.put(argumentName, this)
  }

  sealed case class Suggestion(suggestionName: String, index: Integer) {
    private[this] var requiredPermission: String = ""
    private[this] var suggestionArray: Array[String] = Array.empty

    /**
     * @group SET
     */
    def requiredPermission(permission: String): this.type = {
      this.requiredPermission = permission
      this
    }

    def suggestionArray(array: Array[_]): this.type = {
      this.suggestionArray = array.map(_.toString)
      this
    }

    /**
     * @group GET
     */
    def getRequiredPermission: String = this.requiredPermission
    def getSuggestions: Array[String] = this.suggestionArray
    /**
     * Suggestion initialization block
     */
    {
      suggestions.getOrElseUpdate(suggestionName, mutable.Map.empty).update(index, this)
    }
  }
  object Suggestion {
    def apply(index: Integer, array: Array[_], suggestionPermission: String = ""): Suggestion = Suggestion(commandName, index).suggestionArray(array).requiredPermission(suggestionPermission)
  }
  /**
   * Command initialization block
   */
  {
    oneLibAPI.command.add(this)
  }
  lazy val register: Boolean = oneLibAPI.command.register(this)
}