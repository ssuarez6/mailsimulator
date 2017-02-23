package co.com.simulator

import co.com.simulator._
import akka.actor._
import scala.collection.mutable._
import scala.concurrent.Future
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global

case class Message(fromAddress: String, toAddress: String, content: String, var read: Boolean)
case class EmailAddress(add: String){
  def isValid = add.split("@").size == 2
  val username = add.split("@")(0)
  val domain = add.split("@")(1)
  override def toString = add
}

class Account(val address: EmailAddress) extends Actor {
  import Account._
  var sendedMessages = ListBuffer[Message]()
  var receivedMessages = ListBuffer[Message]()
  val myServer = context.parent

  def receive = {
    case WritableMessage(to,content) => {
      myServer ! SendableMessage(Message(this.address.toString, to, content, false))
    }

    case PrintAddress => println(this.address.toString)

    case ReceivableMessage(message) => receivedMessages.append(message)

    case DeletableMessage(message) => {
      val index = receivedMessages.indexOf(message)
      receivedMessages.remove(index)
    }

    case ReadableMessage(message) => {
      message.read = true
    }

    case ReadAllUnreads => readUnreads
  }

  def readUnreads = {
    receivedMessages
      .filter(x => !x.read)
      .foreach(y =>{
        println("******")
        println(s"From: ${y.fromAddress}")
        println("******Message content******")
        println(y.content)
        println("******\nEND")
        y.read = true
      })
  }
}

//protocol definition
object Account {
  case class ReceivableMessage(m: Message)
  case class DeletableMessage(m: Message)
  case class ReadableMessage(m: Message)
  case class WritableMessage(to: String, content: String)
  case object GetUnreads
  case object ReadAllUnreads
  case object PrintAddress
}
