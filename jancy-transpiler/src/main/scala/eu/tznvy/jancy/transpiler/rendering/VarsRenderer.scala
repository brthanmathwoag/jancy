package eu.tznvy.jancy.transpiler.rendering

import scala.collection.mutable
import scala.collection.JavaConverters._

/**
  * Creates a YAML representation of variable collections
  */
object VarsRenderer extends Renderer[(String, Any)] {

  override def renderAll(vars: Array[(String, Any)]): String = {
    val sortedPairs = vars.sortBy(_._1)
    val model = mutable.LinkedHashMap[String, Any](sortedPairs:_*).asJava
    YamlContext.get.dump(model)
  }

  override def render(vars: (String, Any)): String =
    renderAll(Array(vars))
}
