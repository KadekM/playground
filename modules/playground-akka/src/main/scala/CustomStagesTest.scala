import java.util

import akka.NotUsed
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.scaladsl._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.immutable.{Seq, VectorBuilder}
import scala.collection.parallel.immutable

object CustomStagesTest extends App with ActorDeps {

  val xs = List(1,1,1,1,2,2,2,1,1,1,3,3,3,3,3)
  val src: Source[Int, NotUsed] = Source(xs)

  src.via(new GroupedByWithin[Int, Int](20, x => x+1))
    .runWith(Sink.foreach(println))

  shutdown()
}


/**
  * Emits bounded Seqs of `maxSize` groupedBy f
  */
class GroupedByWithin[T, K](maxSize: Int, f: T => K) extends GraphStage[FlowShape[T, collection.immutable.Seq[T]]] {

  val in = Inlet[T]("in")
  val out = Outlet[collection.immutable.Seq[T]]("out")
  override val shape = FlowShape(in, out)

  override def initialAttributes: Attributes = Attributes.name("groupedByWithin")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) with InHandler with OutHandler {
    private val buf = new VectorBuilder[T]
    private var key: K = null.asInstanceOf[K]
    private var groupClosed = false
    private var groupEmitted = false
    private var finished = false
    private var elements = 0


    override def preStart() = {
      pull(in)
    }

    private def nextElement(elem: T): Unit = {
      if (elements == 0) key = f(elem) // if first element, set it as key

      if (f(elem) != key) {
        closeGroup()
      } else {
        groupEmitted = false
        buf += elem
        elements += 1
        if (elements >= maxSize) {
          closeGroup()
        } else pull(in)
      }
    }

    private def closeGroup(): Unit = {
      groupClosed = true
      if (isAvailable(out)) emitGroup()
    }

    private def emitGroup(): Unit = {
      groupEmitted = true
      key = null.asInstanceOf[K]
      push(out, buf.result())
      buf.clear()
      if (!finished) startNewGroup()
      else completeStage()
    }

    private def startNewGroup(): Unit = {
      elements = 0
      groupClosed = false
      if (isAvailable(in)) nextElement(grab(in))
      else if (!hasBeenPulled(in)) pull(in)
    }

    override def onPush(): Unit = {
      if (!groupClosed) nextElement(grab(in)) // otherwise keep the element for next round
    }

    override def onPull(): Unit = if (groupClosed) emitGroup()

    override def onUpstreamFinish(): Unit = {
      finished = true
      if (groupEmitted) completeStage()
      else closeGroup()
    }

    setHandlers(in, out, this)
  }
}

