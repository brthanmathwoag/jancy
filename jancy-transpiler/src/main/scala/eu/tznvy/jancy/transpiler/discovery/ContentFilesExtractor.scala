package eu.tznvy.jancy.transpiler.discovery

import java.io.File
import java.nio.file.Path
import java.util.zip.ZipFile

import eu.tznvy.jancy.transpiler.helpers.Filesystem

import scala.collection.JavaConverters._
import resource.managed

class ContentFilesExtractor(filesystem: Filesystem) {

  def extract(files: Seq[ContentFile], jar: File, root: Path): Seq[Path] = {

    managed(new ZipFile(jar.getPath))
      .map({ z =>
        files.flatMap({ f =>
          managed(z.getInputStream(z.getEntry(f.source)))
            .map({ in =>
              val outputPath = root.resolve(f.destination)
              filesystem.createDirectories(outputPath.getParent)
              filesystem.copy(in, outputPath)
              outputPath
            }).opt.map(Seq(_)).getOrElse(Seq())
        })
    }).opt.getOrElse(Seq())
  }
}
