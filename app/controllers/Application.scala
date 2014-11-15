package controllers

import util.Html._
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
        TemporaryRedirect("/query_status")
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
              val origQuery = (v.get \ "orig_query").as[String]
              (v.get \ "state").as[String] match {
                case "submitted" =>
                  Ok(views.html.requested(origQuery, id, "submitted"))
                case "requested" =>
                  Ok(views.html.requested(origQuery, id, "requested"))
                case "failed" =>
                  Ok(views.html.failed_lemm(origQuery, id, (v.get \ "state_description").toString()))
                case "derived" =>
                  Ok(views.html.derived(
                      id, origQuery, 
                      escapeQuotes(java.net.URLDecoder.decode((v.get \ "derivation_result").as[String], "UTF-8"))
                      ))
                case s: String =>
                  println("Don't know how to interpret state: " + s)
                  Ok(views.html.index(id))
              }
            case None => Ok("No requests for this session.");
            case _ => Ok("States:" + BackendLookup.states)
          }
        }.getOrElse {
          Ok("Not authorized request. Go to mainpage.").withNewSession
        }
  }

  def formatSymbolsTable(encoded: String): List[Tuple3[Int, String, Double]] = {
    java.net.URLDecoder.decode(encoded, "UTF-8").split("\n")
      .foldLeft(List[Tuple3[Int, String, Double]]())(
        (a: List[Tuple3[Int, String, Double]], b: String) =>
          b match {
            case "" => a
            case s: String => parse(b) :: a
          }
      ).sortWith((a, b) => a._3 > b._3)
  }

  def parse(line: String): Tuple3[Int, String, Double] = {
    val tokens = line.split("\t")
    Tuple3(tokens(0).toInt, tokens(1), tokens(2).toDouble)
  }

}
