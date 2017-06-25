import cats.kernel._
import cats.implicits._

case class Foo(value: Int)

implicit val semiFoo = new Semigroup[Foo] {
  override def combine(x: Foo, y: Foo): Foo = Foo(x.value+y.value)
}

(Foo(3) |+| Foo(2)) |+| Foo(8)
Foo(3) |+| (Foo(2) |+| Foo(8))

case class FooBand(value: Int)

implicit val fooBand = new Band[FooBand] {
  override def combine(x: FooBand, y: FooBand): FooBand = FooBand(Math.max(x.value, y.value))
}

(FooBand(3) |+| FooBand(2)) |+| FooBand(8)
FooBand(3) |+| (FooBand(2) |+| FooBand(8))

FooBand(3) |+| FooBand(3)

//

case class FooFunc(value: Int => Int)
implicit val fooFuncMonoid  = new Monoid[FooFunc] {
  override def empty: FooFunc = FooFunc(x => x)
  override def combine(x: FooFunc, y: FooFunc): FooFunc =
    FooFunc(x.value.andThen(y.value))
}

val f1 = (FooFunc(x => x - 1) |+| FooFunc(x => x * 3)) |+| FooFunc(x => x + 8)
val f2 = FooFunc(x => x - 1) |+| (FooFunc(x => x * 3) |+| FooFunc(x => x + 8))
f1.value(-5)
f2.value(-5)

//





