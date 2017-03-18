import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorAttributes, ActorMaterializer, Materializer, Supervision}
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object RetryInStream extends App with ActorDeps {

  implicit val ec: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

  println("--- start ---")
  def fail(i: Int): Future[Int] = {
    println(s"trying $i")
    if (i % 3 == 0) Future.failed(new Exception())
    else Future.successful(i)
  }

  def retry[A](times: Int)(f: => Future[A]): Future[A] = {
    f.recoverWith {
      case e : Exception if times > 0 => retry(times-1)(f)
    }
  }


  val stream = Source(1 to 10).mapAsync(3) { i => retry(3)(fail(i)) }

  val f = stream
      .withAttributes(ActorAttributes.supervisionStrategy { case e: Exception => Supervision.Resume })
    .runWith(Sink.foreach(println))


  f.onComplete(println)

  val result = Await.result(f, duration)
  println(result)

  shutdown()
}
