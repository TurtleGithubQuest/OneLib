package dev.turtle.onelib

package api

import api.OneLibAPI

import org.bukkit.plugin.java.JavaPlugin

abstract class OnePlugin extends JavaPlugin:
  //var oneLibAPI: OneLibAPI
  protected var _pluginFolder = this.getName

  object pluginFolder {
    def get: String = _pluginFolder

    def set(newLocation: String): Unit = _pluginFolder = newLocation
  }

  val oneLibAPI: OneLibAPI = OneLibAPI(this)
end OnePlugin
