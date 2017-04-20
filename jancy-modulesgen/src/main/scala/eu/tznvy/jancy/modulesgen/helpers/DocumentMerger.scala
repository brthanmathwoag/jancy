package eu.tznvy.jancy.modulesgen.helpers

import java.util.{ArrayList, LinkedHashMap}
import scala.collection.JavaConverters._

object DocumentMerger {
  def merge(
    document: LinkedHashMap[String, Object],
    fragment: LinkedHashMap[String, Object]
   ): LinkedHashMap[String, Object] = {
    val result = new LinkedHashMap[String, Object](document)
    for(key <- fragment.keySet().asScala) {

      val bothHaveKey = result.keySet().contains(key)
      val areMaps = (bothHaveKey
        && result.get(key).isInstanceOf[LinkedHashMap[_, _]]
        && fragment.get(key).isInstanceOf[LinkedHashMap[_, _]])

      val areLists = (!areMaps && bothHaveKey
        && result.get(key).isInstanceOf[ArrayList[_]]
        && fragment.get(key).isInstanceOf[ArrayList[_]])

      if (areMaps) {
        val curMap = result.get(key).asInstanceOf[LinkedHashMap[String, Object]]
        val fragmentMap = fragment.get(key).asInstanceOf[LinkedHashMap[String, Object]]
        val merged = merge(curMap, fragmentMap)
        result.put(key, merged)
      } else if (areLists) {
        val curList = result.get(key).asInstanceOf[ArrayList[Object]]
        val curListCopy = new ArrayList[Object](curList)
        val fragmentList = fragment.get(key).asInstanceOf[ArrayList[Object]]
        curListCopy.addAll(fragmentList)
        result.put(key, curListCopy)
      } else {
        result.put(key, fragment.get(key))
      }
    }
    result
  }
}
