import cats._
import cats.implicits._

val bi1 = (5, "foo")
bi1.bimap(_ + 1, _ + "bar")

val bi2 = (Some(10), List(1,2,3))
bi2.bifoldLeft(0)((z, s) => z + s.getOrElse(0), (z, s) => z + s.sum)

val bi3 = (Option(3), Option(4))
bi3.bisequence

val bi4 = (Option(3), Option.empty)
bi4.bisequence

val bi5 = (Option.empty, Option(55))
bi5.bisequence

