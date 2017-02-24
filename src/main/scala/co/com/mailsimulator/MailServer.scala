package co.com.mailsimulator

import co.com.mailsimulator._
import akka.actor._
import scala.collection.mutable.ListBuffer

case class SendMessage(m: Message)

class MailServer(domain: String) extends Actor{
  import MailServer._
  import co.com.mailsimulator.Account._
  class RegisteredUserException extends Exception("Username already in use")
  var registeredUsers = ListBuffer[String]()

  def receive = {
    case SendMessage(message) => {
      message.destinataries.foreach(destAddress => {
        val ea = EmailAddress(destAddress)
        if(ea.domain == this.domain){
          if(registeredUsers.exists(x => x == ea.username)){
            val receiver = context.actorSelection(destAddress)
            receiver ! ReceiveMessage(message)
          }else{
            sendErrorMessage(message, ea.username) //???
          }
        }else{
          val server = system.actorSelection(s"/user/${ea.domain}")
          val destinatary = message.destinataries.filter(x => x == destAddress)
          val modMessage = Message(message.fromAddress, destinatary, message.content, message.read)
          server ! SendMessage(modMessage)
        }
      })
    }

    case RegisterUser(username) => {
      val index = registeredUsers.indexOf(username)
      if(index != -1) throw new RegisteredUserException
      val address = new EmailAddress(s"$username@$domain")
      val user = context.actorOf(Props(new Account(address)).withDispatcher("account-dispatcher"), address.toString)
      registeredUsers.append(username)
    }

    case RetrieveUsers => sender ! registeredUsers
  }

  def sendErrorMessage(m: Message, username: String) = {
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
  }
}
//protocol definition
object MailServer {
  val system = ActorSystem("MailSystem")
  case class RegisterUser(username: String)
  case object RetrieveUsers
}
