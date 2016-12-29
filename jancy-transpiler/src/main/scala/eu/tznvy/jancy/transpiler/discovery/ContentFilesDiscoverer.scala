package eu.tznvy.jancy.transpiler.discovery

import java.io.File
import java.util.zip.ZipFile
import resource.managed
import scala.collection.JavaConverters._

object ContentFilesDiscoverer {

  def discover(file: File, configurationName: String): Seq[ContentFile] = {
    managed(new ZipFile(file.getPath))
      .map(_
        .stream
        .iterator
        .asScala
        .filter(!_.isDirectory)
        .map(_.getName)
        .filter(_.startsWith(configurationName + "/"))
        .map({ source =>
          val destination = source.substring(configurationName.length + 1)
          ContentFile(source, destination)
        })
        .toList)
      .opt
      .get
  }
}
