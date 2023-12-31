package dev.turtle.onelib

package configuration

import OneLib.onelib
import api.{OneLibAPI, OnePlugin}
import message.Debug.*
import message.{Debug, DebugLevel}
import utils.Exceptions.ConfigValueNotFoundException

import com.typesafe.config
import com.typesafe.config.*
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.java.JavaPlugin

import java.io.*
import java.nio.file.{Files, StandardCopyOption}
import scala.collection.mutable
import scala.util.{Success, Try}

case class OneConfig(oneLibAPI: OneLibAPI) {
  private var _configName: String = oneLibAPI.plugin.get.getName
  private var _fileType: String = ".conf"
  private val _pluginFolderName: String = oneLibAPI.plugin.get.pluginFolder.get
  private var _folder: String = ""
  private var _configurationSection: String = ""
  private var _config: Config = ConfigFactory.empty
  private var _isValid: Boolean = false

  def reload: Boolean = {
    _isValid = Try(
      (_config = ConfigFactory.parseFile(getFile))
    ).isSuccess //TODO: Log errors
    oneLibAPI.oneConfigAPI.reload(this)
    _isValid
  }

  def fileName: String = _configName + _fileType

  /**
   * @Category SETTINGS
   */
  def configName(configName: String): this.type = {
    _configName = configName; this
  }

  def fileType(fileType: String): this.type = {
    _fileType = fileType; this
  }

  def folder(folder: String): this.type = {
    _folder = folder; this
  }

  def copyDefaults: this.type = {
    createFile("defaults"); this
  }

  def section(configurationSection: String*): this.type = {
    _configurationSection = configurationSection.mkString("."); this
  }

  /**
   * @Category GETTERS
   */
  def getName: String = this._configName

  def getFolder: String = this._folder

  def getFileType: String = this._fileType

  /**
   * @Category FUNCTIONS
   */
  def save(concise: Boolean=false,comments: Boolean=false, originComments: Boolean=false, formatted: Boolean=true): Boolean = {
    val file = this.getFile
    try {
      val writer = new BufferedWriter(new FileWriter(file))
      writer.write({
        this._config.root().render({
          if (concise)
            ConfigRenderOptions.concise()
          else
            ConfigRenderOptions.defaults().setComments(comments).setOriginComments(originComments).setFormatted(formatted)
        })
      })
      writer.close()
    } catch {
      case e: Exception =>
        debugMessage(s"&cError saving '${file.getName}' to path: ${e.getMessage}").level(DebugLevel.ERROR)
        return false
    }
    true
  }
  private def createFile(folder: String=""): Boolean = {
    val file: File = new File(Array("plugins", _pluginFolderName, _folder, folder, fileName).mkString(File.separator))
    (new File(file.getPath.replace(fileName,""))).mkdirs()
    val resourcesPath: String = if _folder.isEmpty then fileName else Array(_folder, fileName).mkString("/")
    val inputStream: InputStream = getClass.getClassLoader.getResourceAsStream(resourcesPath)
    if (inputStream != null) {
      try {
        Files.copy(inputStream, file.toPath, StandardCopyOption.REPLACE_EXISTING)
        return true
      } catch {
        case e: Exception =>
          getLogger.info("Error while copying default config values!")
          e.printStackTrace()
      } finally {
        try {
          inputStream.close()
        } catch {
          case e: IOException =>
            getLogger.info("Error while closing InputStream!")
            e.printStackTrace()
        }
      }
    } else {
      getLogger.info(s"Resource $resourcesPath not found!") //TODO: Implement debugger
    }

    false
  }
  def getFile: File = {
    val file: File = new File(Array("plugins", _pluginFolderName, _folder, fileName).mkString(File.separator))
    if (!file.exists())
      createFile()
    file
  }

  def isKeyPresent(key: String): Boolean = {
    Try(_config.hasPath(key)) match {
      case Success(true) => true
      case _ =>
        false
    }
  }

  def getString(key: String, default: Option[Any] = null): String = {
    if (isKeyPresent(key))
      _config.getString(key)
    else if ((default ne null) && default.isDefined)
      default.get.toString
    else
      throw ConfigValueNotFoundException(s"[${this._configName}] Value not found: $key", 150)
  }

  def getInt(key: String, default: Option[Integer] = null): Integer = Integer.parseInt(getString(key, default = default))

  def getDouble(key: String): Double = getString(key).toDouble

  def getBoolean(key: String): Boolean = getString(key).toBoolean

  def isValid: Boolean = _isValid
  /**
   * Initialization block
   */
  {
    this.reload
  }
}