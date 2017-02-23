package co.com.simulator

import co.com.simulator._
import akka.actor._
import scala.collection.mutable

case class Message(from: Account, to: Account, content: String, read: Boolean)
case class EmailAddress(add: String){
  def isValid = add.split("@").size == 2
  val username = add.split("@")(0)
  val domain = add.split("@")(1)
  override def toString = add
}

class Account(address: EmailAddress) extends Actor {

  val sendedMessages = ListBuffer[Message]()
  val receivedMessages = ListBuffer[Message]()
  val myServer: Actor = context.actorSelection(address.domain)

  def writeMessage(to: Account, content: String) = 
    Message(self, to, content, false)

  def receive = {

    case SendableMessage(message) => myServer ! SendableMessage(message)

    case ReceivableMessage(message) => receivedMessages.append(message)

    case DeletableMessage(message) => {
      val index = receivedMessages.indexOf(message)
      receivedMessages.remove(index)
    }

    case ReadableMessage(message) => {
      println("******")
      println(s"From: ${m.from.address.toString}")
      println("******Message content******")
      println(m.content)
      println("******\nEND")
      m.read = true
    }

    case GetUnreads => sender ! sendedMessages.toList.filter(x => !x.read)

    case ReadAllUnreads => {
      val unreads: Future[List[Message]] = self ? GetUnreads
      unreads.foreach(messageList => {
        messageList.foreach(m => {
          self ! ReadableMessage(m)
        })
      })
    }
  }

  def writeMessage(to: Account, content: String) = Message(self, to, content, false)
  def read(m: Message) = self ! ReadableMessage(m)
  def delete(m: Message) = self ! DeletableMessage(m)
}

//protocol definition
object Account {
  case class ReceivableMessage(m: Message)
  case class DeletableMessage(m: Message)
  case class ReadableMessage(m: Message)
  case object GetUnreads
  case object ReadAllUnreads
}
