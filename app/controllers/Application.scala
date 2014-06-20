package controllers

import messaging.BackendLookup
import play.api.mvc._
import results._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def request(query: String) = Action {
      BackendLookup.lookupActor ! query
      Ok(views.html.requested(query))
  }

  def result() = Action {
      Ok(views.html.list(ExpansionTerm.mockTerms))
  }
}