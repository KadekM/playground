import doobie.util.transactor.DriverManagerTransactor

import fs2.Task
import doobie.imports._

import akka.stream.scaladsl.{ Flow => AkkaFlow, Sink => AkkaSink, Source => AkkaSource, Keep }
import akka.{ Done, NotUsed }

import fs2.{ Pipe, Pure, Sink, Stream, pipe }

import scala.collection.immutable.Seq
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._


object DbTest extends App {

  val transactor = DriverManagerTransactor[Task](
    "org.postgresql.Driver",
    "jdbc:postgresql:superphone",
    "postgres",
    "postgres"
  )

  val amount = 1000000
  val query = sql"SELECT generate_series(1,10) AS id, md5(random()::text) AS blob;".query[(Int, String)]

  import streamz.converter._
  import scala.concurrent.ExecutionContext.Implicits.global

  AkkaSource.fromGraph(query.process.toSource)

  val numbers: Seq[Int] = 1 to 10
  def g(i: Int) = i + 10

  val fSink2: Sink[Pure, Int] = s => pipe.lift(g)(s).map(println)
  val aSink2: AkkaSink[Int, Future[Done]] = AkkaSink.fromGraph(fSink2.toSink)

  val fStream2: Stream[Pure, Int] = Stream.emits(numbers)
  val aSource2: AkkaSource[Int, NotUsed] = AkkaSource.fromGraph(fStream2.toSource)

  val fpipe2: Pipe[Pure, Int, Int] = pipe.lift[Pure, Int, Int](g)
  val aFlow2: AkkaFlow[Int, Int, NotUsed] = AkkaFlow.fromGraph(fpipe2.toFlow)





}
