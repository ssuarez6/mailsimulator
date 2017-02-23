package co.com.simulator

import co.com.simulator._
import akka.actor._
import scala.collection.mutable.ListBuffer

case class SendableMessage(m: Message)

class MailServer(domain: String) extends Actor{
  
  import MailServer._
  import co.com.simulator.Account._
  
  class RegisteredUserException extends Exception("Username already in use")
  var registeredUsers = ListBuffer[String]()

  def receive = {
    case SendableMessage(message) => {
      val ea = EmailAddress(message.toAddress)
      if(ea.domain == this.domain){
        val toAccount = context.actorSelection(message.toAddress)
        toAccount ! ReceivableMessage(message)
      }else {
        val system = ActorSystem("MailSystem")
        val server = system.actorSelection(s"/user/${ea.domain}")
        println(server)
        server ! "?"
        server ! SendableMessage(message)
      }
    }
    
    case RegisterUser => context.become(registerMode)

    case "?" => println("????")
  }



  def registerMode: Receive = {
    case RegisterableUser(username) => {
      val index = registeredUsers.indexOf(username)
      if(index != -1) throw new RegisteredUserException
      val address = new EmailAddress(s"$username@$domain")
      val user = context.actorOf(Props(new Account(address)), address.toString)
      registeredUsers.append(username)
      context.unbecome()
    }
  }

  
}
//protocol definition
object MailServer {
  case class RegisterableUser(username: String)
  case object RegisterUser
}
