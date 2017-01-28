package eu.tznvy.jancy.transpiler.discovery

import java.io.File
import java.net.URLClassLoader
import java.util.zip.ZipFile
import java.io.Closeable
import scala.collection.JavaConverters._

/**
  * Provides an interator for classes in a particular jar.
  */
class JarClassSource(file: File) extends Closeable {

  lazy val zipFile = new ZipFile(file.getPath)

  override def close(): Unit = {
    zipFile.close()
  }

  def iterate: Iterator[Class[_]] = {
    val classLoader = new URLClassLoader(Array(file.toURI.toURL))

    zipFile
      .stream
      .iterator
      .asScala
      .map(_.getName)
      .filter(_.endsWith(".class"))
      .map(_.replace(".class", "").replace('/', '.'))
      .map(classLoader.loadClass)
  }
}
