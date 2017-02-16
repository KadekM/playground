import shapeless._

object Coprod extends App {

  case class A()
  case class B()
  case class C()
  case class D()

  type ISB = A :+: B :+: C :+: D :+: CNil

  val x = Coproduct[ISB](D())

  object run extends Poly1 {
    implicit def caseA = at[A](i => println("a"))
    implicit def caseB = at[B](i => println("b"))
    implicit def caseC = at[C](i => println("c"))
    implicit def caseD = at[D](i => println("d"))
  }

  x.map(run)



}
