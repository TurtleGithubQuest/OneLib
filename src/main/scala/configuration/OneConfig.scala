package dev.turtle.onelib
package configuration

import Main.plugin
import configuration.OneConfig.configs
import message.Debug.debugMessage
import message.Debug._
import utils.Exceptions.ConfigValueNotFoundException

import com.typesafe.config
import com.typesafe.config.*
import dev.turtle.onelib.message.{Debug, DebugLevel}
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getLogger

import java.io.*
import java.nio.file.{Files, StandardCopyOption}
import scala.collection.mutable
import scala.util.{Success, Try}

class OneConfig(configName: String) {
  var name: String = configName
  private var _fileType: String = ".conf"
  private val _pluginName: String = plugin.getName
  private var _folder: String = ""
  private var _config: Config = ConfigFactory.empty

  def reload: Boolean = {
    _config = ConfigFactory.parseFile(getFile)
    true
  }
  def fileName: String = name+_fileType
  def fileType(fileType: String): this.type = {
    _fileType=fileType
    this
  }
  def folder(folder: String): this.type = {
    _folder=folder
    this
  }
  def copyDefaults: this.type = {
    createFile("defaults")
    this
  }

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
        debugMessage(s"&cError saving '${file.getName}' to path: ${e.getMessage}", debugLevel=DebugLevel(200))
        return false
    }
    true
  }
  private def createFile(folder: String=""): Boolean = {
    val file: File = new File(Array("plugins", _pluginName, _folder, folder, fileName).mkString(File.separator))
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
    val file: File = new File(Array("plugins", _pluginName, _folder, fileName).mkString(File.separator))
    if (!file.exists())
      createFile()
    file
  }
  def isPathPresent(path: String): Boolean = {
    Try(_config.hasPath(path)) match {
      case Success(true) => true
      case _ =>
        false
    }
  }

  def getString(path: String, default: Option[Any] = null): String = {
    if (isPathPresent(path))
      _config.getString(path)
    else if ((default ne null) && default.isDefined)
      default.get.toString
    else
      throw ConfigValueNotFoundException(s"[${this.name}] Value not found: $path", 150)
  }
  def getInt(path: String, default: Option[Integer]=null): Integer = Integer.parseInt(getString(path, default=default))
  def getDouble(path: String): Double = getString(path).toDouble
  def getBoolean(path: String): Boolean = getString(path).toBoolean
  /**
   * Initialization block
   */
  {
    //reload
    configs.update(
      if _folder.isBlank then name else s"$_folder.$name",
      this
    )
  }
}

object OneConfig:
  var configs: mutable.Map[String, OneConfig] = mutable.Map()

  def option(configName: String, folder: String=""): Option[OneConfig] = {
    var path = configName
    if (configs.contains(folder))
      path=s"$folder.$path"
    configs.get(path)
  }
  def config(configName: String, folder: String=""): OneConfig = option(configName, folder).get

  def reloadAll: Boolean = {
    for(config <- configs)
      config._2.reload
    true
  }