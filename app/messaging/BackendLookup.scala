package messaging

/**
 * author: A.Sirenko
 * Date: 5/25/14
 */

import akka.actor._
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{JsNull, JsValue, Json, JsObject}

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
    a
  }

  var states: Map[String, JsValue] = Map.empty

}

class BackendLookupActor extends Actor {

  val remote = context.actorSelection("akka.tcp://ModelingActorSystem@127.0.0.1:2552/user/InterfaceActor")

  def receive = {
    case msg: String =>
      if (msg.startsWith("status_update:")) {
       val o = Json.parse(msg.substring(14))
        val sessionId = (o \ "sessionId").as[String]
        val status = o \ "status"
        status match {
          case JsNull =>
            BackendLookup.states -= sessionId
            println("SessionId " + sessionId + " is not on backend")
          case _ =>
            BackendLookup.states += sessionId -> status.as[JsObject]
            println("SessionId " + sessionId + " gets status update")
        }
        println("Status of session" + sessionId + ": " + status.toString())
      } else {
        println("Resend to remote " + msg)
        remote ! msg
      }
  }
}
