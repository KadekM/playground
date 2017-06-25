import cats.functor._
import cats.implicits._

case class Pred[A](value: A => Boolean)

implicit val contraPred = new Contravariant[Pred] {
  override def contramap[A, B](fa: Pred[A])(f: (B) => A): Pred[B] = {
    Pred(fa.value.compose(f))
  }
}

val odd = Pred((x: Int) => x % 2 == 0)
val kindaOdd = odd.contramap((x: Float) => Math.round(x))

kindaOdd.value(5.0f)
kindaOdd.value(6.0f)
kindaOdd.value(7.0f)
kindaOdd.value(8.4f)
kindaOdd.value(8.6f)
