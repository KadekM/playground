import shapeless._
import shapeless.MkFieldLens

object Somewhat extends App {

  final case class Address(street: String, number: Int)
  final case class Delivery(cost: Int, address: Address)

  val d1 = Delivery(3000, Address("Highroad", 53))
  val dCopy = d1.copy(address = d1.address.copy(number = 55))

  val lensAddressStreet = lens[Address].street
  val lensAddressNumber = lens[Address].number

  val lensDeliveryCost = lens[Delivery].cost
  val lensDeliveryAddress = lens[Delivery].address

  //val d2 = lensAddressStreet.set(d1)("changed") // no good found: Delivery, required Addres
  val comp = lensAddressStreet.compose(lensDeliveryAddress)
  val d3 = comp.set(d1)("Shipyward")
  println(d3)

  val comp2 = lensDeliveryAddress >> 'street
  val d3b = comp2.set(d1)("Shipyward")
  println(d3b)

  val comp3 = lensDeliveryAddress >> 0
  val d3c = comp3.set(d1)("Shipyard")
  println(d3c)

  val chainedLens = lens[Delivery].address.street
  val d4 = chainedLens.set(d1)("Wreckage")
  println(d4)


  val lens2 = chainedLens ~ lensDeliveryCost
  val d5 = lens2.set(d1)("Hilltop", 6666)
  println(d5)
  println(lens2.get(d3))


}
