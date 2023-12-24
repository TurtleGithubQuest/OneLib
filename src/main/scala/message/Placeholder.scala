package dev.turtle.onelib
package message

import message.Placeholder.{prefix, suffix}

case class Placeholder(var key: String, var value: String) {
  def forceLowerCase: this.type = {
    key = key.toLowerCase
    value = value.toLowerCase
    this
  }
  def forceUpperCase: this.type = {
    key = key.toUpperCase
    value = value.toUpperCase
    this
  }
  def replaceInString(string: String): String = {
    string.replace(prefix+this.key+suffix, this.value)
  }
  def s: Placeholders = Placeholders(this)
}
case class Placeholders(placeholders: Placeholder*) {
  val array: Array[Placeholder] = placeholders.toArray
  def isEmpty: Boolean = placeholders.isEmpty
}
object Placeholder:
  def apply(key: Any, value: Any): Placeholder = Placeholder(key.toString, value.toString)
  private val prefix = '$'
  private val suffix = ""

  implicit class PlaceholderString(string: String) {
    def translatePlaceholders(placeholders: Placeholders): String = {
      var replacedMessage: String = string
      if (!placeholders.isEmpty) {
        placeholders.array.foreach(placeholder => replacedMessage = placeholder.replaceInString(replacedMessage))
      }
      replacedMessage
    }
  }