package eu.tznvy.jancy.transpiler.rendering

import scala.collection.mutable
import scala.collection.JavaConverters._

object VarsRenderer {

  def render(vars: Map[String, Any]): String = {
    val sortedPairs = vars.toSeq.sortBy(_._1)
    val model = mutable.LinkedHashMap[String, Any](sortedPairs:_*).asJava
    YamlContext.get.dump(model)
  }
}
