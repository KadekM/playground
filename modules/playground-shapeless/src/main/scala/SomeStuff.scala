import shapeless._
import shapeless.tag.@@

import scala.annotation.implicitNotFound

object SomeStuff extends App {

  case class WithoutId(name: String, age: Int)
  case class WithId(id: Long, name: String, age: Long)



  def methodOnlyForId[A](x: A)(implicit ev: MkFieldLens[A, Symbol @@ Witness.`"id"`.T]) = ???

  //methodOnlyForId(WithoutId("ab", 12))
  methodOnlyForId(WithId(3L, "asd", 324))

  class Foo(x: Int) {
  }

  class Bar(a: String) {
  }


}
