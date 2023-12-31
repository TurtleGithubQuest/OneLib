package dev.turtle.onelib

package api

import configuration.OneConfig

import scala.collection.mutable

class OneConfigAPI(private val oneLibAPI: api.OneLibAPI) {
  private val _configs: mutable.Map[String, OneConfig] = mutable.Map()

  def reloadAll: Boolean = {
    for (config <- _configs)
      config._2.reload
    true
  }

  def reload(oneConfig: OneConfig): Unit = _configs.update(oneConfig.getName, oneConfig)
}