import akka.stream.{ActorAttributes, ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.stream.scaladsl.{Sink, Source}

object AttrScopeTest extends App with ActorDeps {

  val decider: Supervision.Decider = {
    case e: Exception =>
      println(s"error thrown ${e.getMessage}")
      Supervision.Resume
  }

  //implicit val matt = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))

  Source(1 to 10)
      //.map { x => if (x == 3) throw new Exception("x is 3") else x }
      .map { x => if (x == 3) throw new Exception("x is 3") else x }
      .withAttributes(ActorAttributes.supervisionStrategy(decider))
      .map { x => if (x == 5) throw new Exception("x is 5") else x }
      .to(Sink.foreach(println))
      .run()

  Thread.sleep(5000)
  shutdown()
}
