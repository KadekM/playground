import scala.concurrent.{Await, Future}

import scala.concurrent.duration._

object Foo extends App {

  import scala.concurrent.ExecutionContext.Implicits._

  def find() = Future { Some { 3 } }
  def addToLog() = Future { println("adding to log") }
  def saveToCache() = Future { println("saving to cache") }


  val f = for {
    x <- find()
    _ = addToLog()
    _ = saveToCache()
  } yield {
    println(x)
  }

  val s = Await.result(f, 10.seconds)
  println(s)

}
