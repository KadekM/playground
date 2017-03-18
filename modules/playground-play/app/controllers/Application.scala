package controllers

import com.google.inject.Inject
import play.api.mvc._

@Inject()
class Application extends Controller {
  def index = Action { implicit req =>
    Ok(views.html.index()).flashing("something" -> "yoo2")
  }

}
