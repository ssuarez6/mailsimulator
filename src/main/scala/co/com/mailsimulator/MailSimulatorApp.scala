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
import MailServer._

object MailSimulator extends App {
  
  implicit val timeout = Timeout(5 seconds)
  // init server actors
  val googleActor = system.actorOf(Props(new MailServer("google.com")).withDispatcher("server-dispatcher"), "google.com")
  val yahooActor = system.actorOf(Props(new MailServer("yahoo.com")).withDispatcher("server-dispatcher"), "yahoo.com")
  val hotmailActor = system.actorOf(Props(new MailServer("hotmail.com")).withDispatcher("server-dispatcher"), "hotmail.com")
  val s4nActor = system.actorOf(Props(new MailServer("s4n.co")).withDispatcher("server-dispatcher"), "s4n.co")

  //register user to the servers
  val user1 = RegisterUser("user1")
  s4nActor ! user1
  val user2 = RegisterUser("user2")
  s4nActor ! user2
  val user1Actor = system.actorSelection("/user/s4n.co/user1@s4n.co")
  val user2Actor = system.actorSelection("/user/s4n.co/user2@s4n.co")
  
  val guser1 = RegisterUser("user1")
  googleActor ! guser1
  val guser2 = RegisterUser("user2")
  googleActor ! guser2
  val guser2Actor = system.actorSelection("/user/google.com/user2@google.com")

  //sending mails
  println("Sending message from user1@s4n.co to user2@s4n.co")
  user1Actor ! WriteMessage("user2@s4n.co", "Greetings from user1")
  println("Sent!")
  println("Sending message from user2@s4n.co to user2@google.com")
  user2Actor ! WriteMessage("user2@google.com", "Saludos desde user2 en s4n.co!")
  println("Sent!")
  Thread.sleep(500)
  println("*************")
  println("Reading unread messages of user2@google.com ... *twice*")
  guser2Actor ! ReadAllUnreads
  guser2Actor ! ReadAllUnreads
  Thread.sleep(500)
  println("*************")
  println("Reading sent messages of user2@s4n.co")
  user2Actor ! ReadAllSent
  
  system.terminate
}
