import shapeless._

object Main extends App {

  case class Employee(name: String, number: Int, manager: Boolean)

  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }
  object CsvEncoder {
    def apply[A](implicit a: CsvEncoder[A]): CsvEncoder[A] = a

    implicit val booleanEncoder: CsvEncoder[Boolean] = b =>
      if (b) List("yes") else List("no")

    implicit val stringEncoder: CsvEncoder[String] = s => List(s)

    implicit val intEncoder: CsvEncoder[Int] = i => List(i.toString)

    implicit val hnilEncoder: CsvEncoder[HNil] = _ => Nil

    implicit def hlistEncoder[H, T <: HList](
        implicit hEnc: Lazy[CsvEncoder[H]],
        tEnc: CsvEncoder[T]): CsvEncoder[H :: T] = {
      case (head :: tail) => hEnc.value.encode(head) ++ tEnc.encode(tail)
    }

    implicit val cnilEncoder: CsvEncoder[CNil] = _ =>
      throw new Exception("inconceviable")

    implicit def clistEncoder[H, T <: Coproduct](
        implicit encH: Lazy[CsvEncoder[H]],
        encT: CsvEncoder[T]): CsvEncoder[H :+: T] = {
      case Inl(h) => encH.value.encode(h)
      case Inr(t) => encT.encode(t)
    }
    /*    implicit val employerEncoder: CsvEncoder[Employee] = {
      val gen = Generic[Employee]
      val enc = CsvEncoder[gen.Repr]

      x => enc.encode(gen.to(x))
    }*/

    implicit def arbEncoder[A, O](implicit genA: Generic.Aux[A, O],
                                  enc: Lazy[CsvEncoder[O]]): CsvEncoder[A] = {
      x =>
        enc.value.encode(genA.to(x))
    }

  }

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(enc.encode(_).mkString(",")).mkString("\n")

  val employees: List[Employee] = List(Employee("Bill", 1, manager = true),
                                       Employee("Peter", 2, manager = false),
                                       Employee("Milton", 3, manager = false))

  import shapeless.ops.hlist.IsHCons
  def getWrappedValue[A, R <: HList, H, HR <: HList](
      x: A)(implicit genA: Generic.Aux[A, R], ev: IsHCons.Aux[R, H, HR]) = {
    genA.to(x).head
  }

  import shapeless.syntax.singleton._
  import shapeless.labelled._

  // NPE?
  //sealed trait Cherries
  //val z = new Cherries {} ->> 123

  val numCherries = "numCherries" ->> 123

  def getFieldName[K, V](v: FieldType[K, V])(implicit w: Witness.Aux[K]): K =
    w.value

  def getValue[K, V](v: FieldType[K, V]): V = v


  val z = LabelledGeneric[Employee].to(employees.head)
  println(z)


}
