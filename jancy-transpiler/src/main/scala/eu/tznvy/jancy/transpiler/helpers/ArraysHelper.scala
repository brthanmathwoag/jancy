package eu.tznvy.jancy.transpiler.helpers

/**
  * Various array transformations and predicates
  */
object ArraysHelper {

  def flattenAPotentialArray(value: Any): Any =
    value match {
      case array: Array[Any] => flattenAnArray(array)
      case _ => value
    }

  def flattenAnArray[T](array: Array[T]): Any =
    if (array.length == 1) array(0)
    else array

  def isAnEmptyArray(value: Any): Boolean =
    value match {
      case array: Array[Any] if array.length == 0 => true
      case _ => false
    }
}
