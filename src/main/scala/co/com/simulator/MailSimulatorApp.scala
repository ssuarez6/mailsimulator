package co.com.simulator

import co.com.simulator._
import akka.actor._

object MailSimulator extends App {
  
  val system = ActorSystem("MailSystem")

  val googleActor = system.actorOf(Props(new MailServer("google.com")), "google.com")

  val yahooActor = system.actorOf(Props(new MailServer("yahoo.com")), "yahoo.com")

  val hotmailActor = system.actorOf(Props(new MailServer("hotmail.com")), "hotmail.com")

  val s4nActor = system.actorOf(Props(new MailServer("s4n.co")), "s4n.co")

  s4nActor ! MailServer.RegisterUser

  val user1 = MailServer.RegisterableUser("user1")
  val user2 = MailServer.RegisterableUser("user2")
  val futUser1: Future[Account] = s4nActor ? user1
  val futUser2: Future[Account] = s4nActor ? user2

  googleActor ! MailServer.RegisterUser

  val guser1 = MailServer.RegisterableUser("user1")
  val guser2 = MailServer.RegisterableUser("user2")
  val futGuser: Future[Account] = googleActor ? guser1
  val futGUser: Future[Account] = googleActor ? guser2


}
