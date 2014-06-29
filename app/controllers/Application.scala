package controllers

import play.api.libs.json._
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

        val q = Json.obj(
          "sessionId" -> id,
          "query" -> query
        )
        BackendLookup.lookupActor ! "query:" + q.toString()
        BackendLookup.states += id -> Json.obj("orig_query" -> query, "state" -> "requested")
        Ok(views.html.requested(query, id, "requested"))
      }.getOrElse {
        Ok("Not authorized request. Go to mainpage.").withNewSession
      }
  }

  def queryStatus() = Action {
    request =>
      println("States: " + BackendLookup.states)
      println("Id from cookies: " + request.session.get("id"))
        request.session.get("id").map { id =>
          BackendLookup.states.get(id) match {
            case v: Some[JsValue] =>
              val origQuery = (v.get \ "orig_query").toString()
              (v.get \ "state").toString() match {
                case "failed" => Ok(views.html.failed_lemm(origQuery, id, (v.get \ "state_description").toString()))
                case _ => Ok(views.html.requested(origQuery, id, v.toString))
              }
            case None => Ok("No requests for this session.");
            case _ => Ok("States:" + BackendLookup.states)
          }
        }.getOrElse {
          Ok("Not authorized request. Go to mainpage.").withNewSession
        }
  }

  def result() = Action {
      Ok(views.html.list(ExpansionTerm.mockTerms))
  }
}