import com.cra.figaro.language.{Flip, Select}
import com.cra.figaro.library.compound.If
import com.cra.figaro.algorithm.factored.VariableElimination

object HelloWorld extends App {
  val sunnyToday = Flip(0.2)

  val greetingToday = If(
    sunnyToday,
    Select(0.6 -> "Hello, world!", 0.4 -> "Howdy, universe!"),
    Select(0.2 -> "Hello, world!", 0.8 -> "Oh no, not again"))

  val sunnyTomorrow = If(sunnyToday, Flip(0.8), Flip(0.05))

  val greetingTomorrow = If(
    sunnyTomorrow,
    Select(0.6 -> "Hello, world!", 0.4 -> "Howdy, universe!"),
    Select(0.2 -> "Hello, world!", 0.8 -> "Oh no, not again"))

  def predict: Double =
    VariableElimination().probability(greetingToday, "Hello, world!")

  def infer = {
    greetingToday.observe("Hello, world!")
    VariableElimination().probability(sunnyToday, true)
  }

  println(predict)
  println(infer)
}
