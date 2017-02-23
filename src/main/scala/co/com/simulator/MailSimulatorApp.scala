package co.com.simulator

import co.com.simulator._
import co.com.simulator.Account._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Await

object MailSimulator extends App {
  
  implicit val timeout = Timeout(5 seconds)
  val system = ActorSystem("MailSystem")
  val googleActor = system.actorOf(Props(new MailServer("google.com")), "google.com")
  val yahooActor = system.actorOf(Props(new MailServer("yahoo.com")), "yahoo.com")
  val hotmailActor = system.actorOf(Props(new MailServer("hotmail.com")), "hotmail.com")
  val s4nActor = system.actorOf(Props(new MailServer("s4n.co")), "s4n.co")

  println(googleActor)
  import MailServer._

  s4nActor ! RegisterUser
  val user1 = RegisterableUser("user1")
  s4nActor ! user1

  s4nActor ! RegisterUser
  val user2 = RegisterableUser("user2")
  s4nActor ! user2

  val user1Actor = system.actorSelection("/user/s4n.co/user1@s4n.co")
  val user2Actor = system.actorSelection("/user/s4n.co/user2@s4n.co")

  googleActor ! RegisterUser
  val guser1 = RegisterableUser("user1")
  googleActor ! guser1
  googleActor ! RegisterUser
  val guser2 = RegisterableUser("user2")
  googleActor ! guser2

  user1Actor ! WritableMessage("user2@s4n.co", "Greetings from user1")

  user2Actor ! WritableMessage("user2@google.com", "Saludos desde user2 en s4n.co!")
  val guser2Actor = system.actorSelection("/user/google.com/user2@google.com")
  guser2Actor ! ReadAllUnreads
}
