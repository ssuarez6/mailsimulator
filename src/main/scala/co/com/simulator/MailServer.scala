package co.com.simulator

import co.com.simulator._
import akka.actor._
import scala.collection.mutable

case class SendableMessage(m: Message)

class MailServer(domain: String) extends Actor{
  class RegisteredUserException extends Exception("Username already in use")
  val registeredUsers = List[Buffer]()

  def receive = {
    case SendableMessage(message) => {
      val toAccount: Actor = context.actorSelection(message.to.address.toString)
      toAccount ! ReceivableMessage(message)
    }
    
    case RegisterUser = become(registerMode)
  }



  def registerMode = {
    case RegisterableUser(username) => {
      val index = registeredUsers.indexOf(username)
      if(index != -1) throw new RegisteredUserException
      val address = new EmailAddress(s"$username@$domain")
      val account = Account(address)
      val user = context.actorOf(Props(account), address.toString)
      sender ! user
      unbecome()
    }
  }

  
}
//protocol definition
object MailServer {
  case class RegisterableUsername(username: String)
  case object RegisterUser
}
