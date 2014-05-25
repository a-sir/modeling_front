package controllers

import play.api._
import play.api.mvc._
import java.util.Date

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def request(query: String) = Action {
      Ok(views.html.requested(query))
    }
  
}