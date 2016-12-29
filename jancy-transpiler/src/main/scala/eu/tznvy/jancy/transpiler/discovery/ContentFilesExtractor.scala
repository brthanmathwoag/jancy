package eu.tznvy.jancy.transpiler.discovery

import java.io.File
import java.nio.file.{ Files, Path}
import java.util.zip.ZipFile

import scala.collection.JavaConverters._
import resource.managed

object ContentFilesExtractor {

  def extract(files: Seq[ContentFile], jar: File, root: Path): Unit = {

    managed(new ZipFile(jar.getPath))
      .map({ z =>
        files.foreach({ f =>
          managed(z.getInputStream(z.getEntry(f.source)))
            .map({ in =>
              val outputPath = root.resolve(f.destination)
              println(outputPath)
              outputPath.toFile.getParentFile.mkdirs()
              Files.copy(in, outputPath)
            }).opt.get
        })
      }).opt.get
  }
}
