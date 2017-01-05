package eu.tznvy.jancy.transpiler.rendering

trait Renderer[T] {
  def render(t: T): String

  def renderAll(ts: Array[T]): String
}
