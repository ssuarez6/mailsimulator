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
  var sentMessages = ListBuffer[Message]()
  var receivedMessages = ListBuffer[Message]()
  val myServer = context.parent

  def receive = {
    case WriteMessage(to,content) => {
      val sentMessage = Message(this.address.toString, to, content, false)
      sentMessages.append(sentMessage)
      myServer ! SendMessage(sentMessage)
    }

    case PrintAddress => println(this.address.toString)

    case ReceiveMessage(message) => receivedMessages.append(message)

    case DeleteMessage(message) => {
      val index = receivedMessages.indexOf(message)
      receivedMessages.remove(index)
    }

    case ReadMessage(message) => {
      message.read = true
    }

    case ReadAllUnreads => readUnreads

    case ReadAllSent => printAllSent
  }

  def readUnreads = {
    val unread = receivedMessages.filter(x => !x.read)
    if(unread.size == 0) {
      println("You have read all your messages!")
    }else{
      unread.foreach(y =>{
        printMessage(y)
        y.read = true
      })
    }
  }

  def printAllSent = {
    if(sentMessages.size == 0){
      println("You haven't sent any messages yet!")
    }else{
      sentMessages.foreach(printMessage(_))
    }
  }

  def printMessage(m: Message): Unit = {
        println("******")
        println(s"From: ${m.fromAddress}")
        println("******")
        println(s"To: ${m.toAddress}")
        println("******Message content******")
        println(m.content)
        println("******\nEND")
  }
}

//protocol definition
object Account {
  case class ReceiveMessage(m: Message)
  case class DeleteMessage(m: Message)
  case class ReadMessage(m: Message)
  case class WriteMessage(to: String, content: String)
  case object GetUnreads
  case object ReadAllUnreads
  case object PrintAddress
  case object ReadAllSent
}
