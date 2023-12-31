package dev.turtle.onelib

package api

import configuration.OneConfig

case class OneLibAPI(private val onePlugin: OnePlugin):
  private var _plugin: OnePlugin = onePlugin

  object plugin {
    def get: OnePlugin = _plugin

    def setPlugin(newPlugin: OnePlugin): Unit = _plugin = newPlugin

    def name: String = this.get.getName
  }

  val oneConfigAPI = new OneConfigAPI(this)

  def config: OneConfig = OneConfig(this)

  val language = new OneLanguageAPI(this)
  val command = new OneCommandAPI(this)
end OneLibAPI
