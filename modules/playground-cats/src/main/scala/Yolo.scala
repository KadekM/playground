
object Yolo extends App {

  def summm(x: Long) = x.toString
    .map(x => x.toString.toLong)
    .sum

  summm(123L)

  def isOk(x: Long): Boolean = {
    if (x % 100 == 0) {
      println(s"checking $x")
    }
    summm(x) == 100 && summm((x * 1.1).toLong) == 89
  }

  Stream.iterate(0L)(_+1L).filter(isOk).foreach(println)

}
