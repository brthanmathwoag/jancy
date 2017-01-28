package eu.tznvy.jancy.transpiler.discovery

import java.io.File
import java.net.URLClassLoader
import java.util.zip.ZipFile

import scala.collection.JavaConverters._
import resource._
import eu.tznvy.jancy.core.PlaybookFactory

/**
  * Finds and instantiates classes implementing the PlaybookFactory interface
  * in a specified jar.
  */
object PlaybookFactoriesDiscoverer {

  def getPlaybookFactoriesInJar(file: File): Seq[PlaybookFactory] = {
    val classLoader = new URLClassLoader(Array(file.toURI.toURL))

    //TODO: can throw
    managed(new ZipFile(file.getPath))
      .map(_
        .stream
        .iterator
        .asScala
        .map(_.getName)
        .filter(_.endsWith(".class"))
        .map(_.replace(".class", "").replace('/', '.'))
        .map(classLoader.loadClass)
        .filter(_.getInterfaces.exists(_.getName == classOf[PlaybookFactory].getName))
        .map(_.newInstance.asInstanceOf[PlaybookFactory])
        .toList)
      .opt
      .get
  }
}