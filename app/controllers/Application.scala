package controllers

import play.api._
import play.api.mvc._
import java.util.Date
import results.ExpansionTerm

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def request(query: String) = Action {
      Ok(views.html.requested(query))
  }

  def result() = Action {
      Ok(views.html.list(ExpansionTerm.mockTerms))
  }
}