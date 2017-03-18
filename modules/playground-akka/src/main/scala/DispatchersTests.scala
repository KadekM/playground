import akka.actor.Actor.Receive
import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool
import akka.stream._
import akka.stream.scaladsl._

import scala.concurrent.Future
import scala.concurrent.duration._

object GraphDslTest extends App with ActorDeps {


}

object DispatchersStreamsTests extends App with ActorDeps {

  Source(1 to 100)
    .mapAsync(1000) { i =>
      Future {
        Thread.sleep(2500)
        println(s"processed \t${Thread.currentThread().getName} : $i")
        i
      }(system.dispatchers.lookup("threadpool-dispatcher"))
    }
    .withAttributes(ActorAttributes.dispatcher("threadpool-dispatcher"))
    .runForeach(i =>
      println(s"finish \t${Thread.currentThread().getName} : $i}"))

  Thread.sleep(5000)
  shutdown()
}

object DispatchersTests extends App with ActorDeps {

  val props = Props(new IOActor(1.second))
    .withDispatcher("forkjoin-dispatcher")
    //.withDispatcher("threadpool-dispatcher")
    .withRouter(RoundRobinPool(10))
  val actor = system.actorOf(props)

  for (i <- 1 to 100) {
    actor ! i
  }

  Thread.sleep(3000)
  shutdown()

}

class IOActor(sleepFor: FiniteDuration) extends Actor {
  override def receive: Receive = {
    case x =>
      Thread.sleep(sleepFor.toMillis)
      println(s"finished - $x")
  }
}

class CPUActor extends Actor {
  override def receive: Receive = {
    case x =>
      var sum = 1.0d
      for (exp <- 1 to 1000000) {
        sum += Math.pow(Math.PI, exp) / exp
      }
      println(s"finished - $x")
  }
}
