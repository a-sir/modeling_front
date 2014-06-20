package messaging

/**
 * author: A.Sirenko
 *          Date: 5/25/14
 */

import akka.actor._
import com.typesafe.config.ConfigFactory

object BackendLookup {

  def confStr(port: Int) = "akka {\n" +
            "//loglevel = \"DEBUG\"\n" +
            "  actor {\n  provider = \"akka.remote.RemoteActorRefProvider\"\n }\n\n" +
            "  remote {\n" +
            "    enabled-transports = [\"akka.remote.netty.tcp\"]\n" +
            "    netty.tcp {\n" +
            "      hostname = \"127.0.0.1\"\n" +
            "      port = " + port + "\n" +
            "    }\n" +
            "  }" +
            "\n" +
            "}"

  lazy val lookupActor: ActorRef = {
    val system = ActorSystem("LocalSystem", ConfigFactory.parseString(confStr(0)))
    val a: ActorRef = system.actorOf(Props[BackendLookupActor], name = "LocalActor")
    println("Backend actor started")
    a ! "handshake"
    a
  }

}

class BackendLookupActor extends Actor {

  val remote = context.actorSelection("akka.tcp://ModelingActorSystem@127.0.0.1:2552/user/RemoteActor")

  def receive = {
    case "handshake" =>
      println("Send Ping to backend")
      remote ! "Ping"
    case msg: String =>
      println("Received " + msg)
      remote ! "Thanks for message " + msg + " from front actor"
  }
}
