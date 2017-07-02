import dogs.Streaming

import scala.concurrent._
import scala.concurrent.duration._


object Yolo extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  def summm(x: Long) = x.toString
    .map(x => x.toString.toLong)
    .sum

  summm(123L)

  def isOk(x: Long): Boolean = {
    val sumx = summm(x) == 100
    if (sumx) {
      println(s" ${Thread.currentThread} checking $x")
      summm((x * 1.1).toLong) == 89
    } else false
  }

  val cores = 8L
  val step = Math.ceil(Long.MaxValue / cores).toLong

  val fs = for(i <- 0L until cores) yield
    Future {
      Streaming.infinite(i * step)(_ + 10L).takeWhile(x => x <= (i+1)*step).find(isOk)
    }


  val finished = Future.sequence(fs)
  finished.foreach(x => println(x.mkString))

  Await.result(finished, 15.minutes)

}
