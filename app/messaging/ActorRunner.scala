package messaging

/**
 * author: A.Sirenko
 *          Date: 5/25/14
 */

import akka.actor._
import com.typesafe.config.ConfigFactory

object ActorRunner extends App {

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

  val system = ActorSystem("LocalSystem", ConfigFactory.parseString(confStr(0)))
  val localActor = system.actorOf(Props[ActorRunner], name = "LocalActor")  // the local actor
  localActor ! "START"
}

class ActorRunner extends Actor {

  val remote = context.actorSelection("akka.tcp://ModelingActorSystem@127.0.0.1:2552/user/RemoteActor")

  def receive = {
    case msg: String =>
        remote ! "Hello from the LocalActor"

        println("LocalActor received message: " + msg)
  }
}
