package controllers

import akka.util.Timeout
import messaging.BackendLookup
import play.api.mvc._
import results._

import scala.util.Random

object Application extends Controller {
  
  def index = Action {
    request =>
      request.session.get("id").map { id =>
        Ok(views.html.index(id))
      }.getOrElse {
        val sessionId = Random.nextString(10)
        println("New user gets id:" + sessionId)
        Ok(views.html.index(sessionId)).withSession("id" -> sessionId)
      }
  }

  def request(query: String) = Action {
    request =>
      request.session.get("id").map { id =>
        BackendLookup.lookupActor ! "query:" + query.trim
        Ok(views.html.requested(query, id))
      }.getOrElse {
        Ok("Not authorized request. Go to mainpage.").withNewSession
      }
  }

  def result() = Action {
      Ok(views.html.list(ExpansionTerm.mockTerms))
  }
}