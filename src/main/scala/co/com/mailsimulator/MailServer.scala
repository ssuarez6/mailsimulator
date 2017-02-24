package co.com.simulator

import co.com.simulator._
import akka.actor._
import scala.collection.mutable.ListBuffer

case class SendMessage(m: Message)

class MailServer(domain: String) extends Actor{

  import MailServer._
  import co.com.simulator.Account._

  class RegisteredUserException extends Exception("Username already in use")
  var registeredUsers = ListBuffer[String]()

  def receive = {
    case SendMessage(message) => {
      val ea = EmailAddress(message.toAddress) // "to" means destinatary
      if(ea.domain == this.domain){
        //verify user exists
        val index = registeredUsers.indexOf(ea.username)
        if(index == -1) {
          //send error message to origin address
          val text = s"ERROR SENDING MESSAGE.\n YOUR MESSAGE:\n${message.content}\n COULD NOT BE SENT SINCE THE USER DOES NOT EXISTS.\n THANKS"
          val errorMessage = Message(message.fromAddress, message.fromAddress, text, false)
          val fromaddress = EmailAddress(message.fromAddress)
          if(fromaddress.domain == this.domain){
            val fromAccount = context.actorSelection(message.fromAddress)
            fromAccount ! ReceiveMessage(errorMessage)
          }else{
            val fromServer = system.actorSelection(s"/user/${fromaddress.domain}")
            fromServer ! SendMessage(errorMessage)
          }
        //if user exists send message
        }else {
          val toAccount = context.actorSelection(message.toAddress)
          toAccount ! ReceiveMessage(message)
        }

      }else {
        val server = system.actorSelection(s"/user/${ea.domain}")
        server ! SendMessage(message)
      }
    }

    case RegisterUser(username) => {
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
  val system = ActorSystem("MailSystem")
  case class RegisterUser(username: String)
}
