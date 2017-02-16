import java.util

import akka.NotUsed
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.scaladsl._
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}

import scala.collection.immutable.{Seq, VectorBuilder}
import scala.collection.parallel.immutable

object CustomStagesTest extends App with ActorDeps {

  val xs1 = List.empty[Int]
  val xs2 = List(1)
  val xs3 = List(1,1)
  val xs4 = List(1,2)
  val xs5 = List(1,1,1,1,2,2,2,1,1,1,3,3,3,3,3,3)
  val src: Source[Int, NotUsed] = Source(xs5)

  src.via(new GroupedByWithin[Int, Int](2, x => x))
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
    private var residuary: Option[T] = None

    override def preStart(): Unit = {
      pull(in)
    }

    private def nextElement(elem: T): Unit = {
      groupEmitted = false
      buf += elem
      elements += 1
      if (elements >= maxSize) {
        closeGroup()
      } else pull(in)
    }

    private def closeGroup(): Unit = {
      groupClosed = true
      if (isAvailable(out)) emitGroup()
    }

    private def tryAddCarryToBuf(): Unit = {
      if (elements < maxSize)
        residuary.foreach { x => buf += x; elements += 1; residuary = None }
    }

    private def emitGroup(): Unit = {
      groupEmitted = true
      key = null.asInstanceOf[K]
      tryAddCarryToBuf()
      push(out, buf.result())
      buf.clear()
      if (!finished) startNewGroup()
      else completeStage()
    }

    private def startNewGroup(): Unit = {
      elements = 0
      groupClosed = false
      if (isAvailable(in)) potNextEl()
      else if (!hasBeenPulled(in)) pull(in)
    }

    private def potNextEl(): Unit = {
      val next = grab(in)
      if (key == null) {
        key = f(next)
      }

      if (f(next) == key) {
        nextElement(next)
      } else {
        closeGroup()
        residuary = Some(next)
      }
    }


    override def onPush(): Unit = {
      if (!groupClosed) {
        potNextEl()
      }
    }

    override def onPull(): Unit = if (groupClosed) emitGroup()

    override def onUpstreamFinish(): Unit = {
      finished = true
      if (groupEmitted) {
        if (residuary.isDefined) {
          tryAddCarryToBuf()
          push(out, buf.result())
        }

        completeStage()
      }
      else closeGroup()
    }

    setHandlers(in, out, this)
  }
}

