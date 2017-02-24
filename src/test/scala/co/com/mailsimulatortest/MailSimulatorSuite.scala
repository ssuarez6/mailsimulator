package co.com.mailsimulatortest

import akka.testkit._
import akka.actor._
import co.com.mailsimulator.{Account, MailServer, Message}
import Account._
import MailServer._
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.collection.mutable.ListBuffer
import org.scalatest.MustMatchers
import org.scalatest.WordSpec
import org.scalatest.FunSuite

//class MailSimulatorSuite extends TestKit(ActorSystem("TestSystem")) with WordSpec with MustMatchers{
class MailSimulatorSuite extends FunSuite {
  
  implicit val timeout = Timeout(5 seconds)
  val server = system.actorOf(Props(new MailServer("server.co")).withDispatcher("server-dispatcher"), "server.co")
  
  /*

  "A server" should {

    "Allow to create users" in {
      server ! RegisterUser("user1")
      server ! RegisterUser("user2")
      server ! RetrieveUsers
      expectMsg{
        case list: ListBuffer[Account] => list.size == 2
      }
    }

  }

  */


  test("Un servidor debe permitir crear usuarios"){
    server ! RegisterUser("user1")
    server ! RegisterUser("user2")
    var f = (server ? RetrieveUsers).mapTo[ListBuffer[Message]]
    val res = Await.result(f, 3 seconds)
    assert(res.size == 2)
  }

  test("No se deben poder crear usuarios repetidos en un servidor"){
    server ! RegisterUser("user1")
    assertThrows[Exception]{
      val fut = server ? RegisterUser("user1")
      val res = Await.result(fut, 3 seconds)
    }
  }
}
