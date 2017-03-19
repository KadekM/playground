import java.io.{FileInputStream, FileOutputStream}
import java.time.LocalDateTime
import java.util.Optional

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io._
import com.romix.scala.serialization.kryo._


object KryoTest extends App {

  val kryo = new Kryo()

  //val output = new Output(new FileOutputStream("file.bin"))
  //val someObjectOut = new Lok("123", Optional.of(123))
  //kryo.writeObject(output, someObjectOut)
  //output.close()


  val input = new Input(new FileInputStream("file.bin"))
  val someObjectIn = kryo.readObject(input, classOf[Lok])
  input.close()

  println(someObjectIn)
  println(someObjectIn.i)
  println(someObjectIn.x)


}
