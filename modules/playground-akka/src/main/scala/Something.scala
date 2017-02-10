import akka.NotUsed
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

object Something extends App with ActorDeps {

  val src: Source[Int, NotUsed] = Source(1 to 100)
  val sink = Flow[Int].map(x => x + 1).toMat(Sink.foreach(println))(Keep.right)

}
