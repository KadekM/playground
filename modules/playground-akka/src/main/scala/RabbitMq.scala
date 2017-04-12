import java.time.LocalDateTime

import akka.actor.Props
import com.spingo.op_rabbit._
import akka.pattern._
import akka.stream.Supervision.Decider
import akka.stream.{ActorAttributes, Attributes, Supervision}
import akka.util.Timeout
import com.rabbitmq.client.Channel

import scala.concurrent.duration._
import com.spingo.op_rabbit.Message.ConfirmResponse
import com.spingo.op_rabbit.stream.RabbitSource

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RabbitMq extends App with ActorDeps {

  val rabbitControl = system.actorOf(Props[RabbitControl])
  val queueName = "some-queue"
  implicit val timeout: Timeout = 5.seconds

  // queue into rabbitmq
  for (i <- 1 to 100) {
    (rabbitControl ! Message.queue(s"$i", queue = queueName))
    Thread.sleep(1)
  }
  Thread.sleep(2000)

  //read from rabbitmq
  val decider: Decider = {
    case e: Exception =>
      println("problem detected")
      Supervision.Resume
  }

  implicit val recoveryStrategy: RecoveryStrategy = new RecoveryStrategy {
    override def apply(v1: String, v2: Channel, v3: Throwable) = (v1, v3) match {
      case (q, e) =>
        println(s"${LocalDateTime.now.toString} on $q - $e")
        Directives.nack(requeue = true)
    }
  }

  def die(x: Int): Int = {
    if (x == 25) { Thread.sleep(1500);throw new Exception("oops") }
    else { x }
  }

  import com.spingo.op_rabbit.Directives._
  RabbitSource(rabbitControl,
               channel(qos = 50),
               consume(
                 queue(queueName,
                       durable = true,
                       exclusive = false,
                       autoDelete = false)),
               body(as[String]))
    .map(x => x.toInt)
    .mapAsyncUnordered(8) (x => Future { die(x) })
    .withAttributes(ActorAttributes.supervisionStrategy(decider))
    .acked
    .runForeach { x =>
      println(s"${LocalDateTime.now.toString}  --> $x")
    }

  Thread.sleep(15000)

  shutdown()
}
