package controllers

import play.api._
import play.api.mvc._
import java.util.Date

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def hello = Action {
    Ok(views.html.hello((new Date).toString))
  }
  
}