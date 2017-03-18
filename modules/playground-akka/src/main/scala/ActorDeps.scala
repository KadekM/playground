import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration._

trait ActorDeps {

  val duration = 20.seconds
  implicit val system: ActorSystem = ActorSystem("my-test-system")
  implicit val mat: Materializer = ActorMaterializer()

  def shutdown(): Unit = {
    println("shutting down")
    Await.result(system.terminate(), duration)
    println("done")
  }

}
