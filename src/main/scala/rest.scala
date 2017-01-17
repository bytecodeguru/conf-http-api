package rest

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import StatusCodes._
import controller._

object Rest {

  def routes = pathSingleSlash {
    // GET /
    get {
      complete(Controller.getAll)
    } ~
    // POST /
    ( post & entity(as[Config]) ) { c =>
      complete(Controller.create(c) match {
        case Left(_) => HttpResponse(Conflict)
        case Right(_) => HttpResponse(Created, List(headers.Location(s"/${c.getId}")))
      })
    }
  } ~
  path(Segment) { id =>
    // GET /:id
    get {
      complete(Controller.read(id) match {
        case None =>    HttpResponse(NotFound)
        case Some(c) => c
      })
    } ~
    // DELETE /:id
    delete {
      complete(Controller.delete(id) match {
        case Right(_) => HttpResponse(NoContent)
        case _        => HttpResponse(NotFound)
      })
    } ~
    // PUT /:id
    ( put & entity(as[Config]) ) { c =>
      complete(Controller.update(id, c) match {
        case Left(UpdateNotFound)  => HttpResponse(NotFound)
        case Left(IdConflict)      => HttpResponse(Conflict)
        case Right(_)              => HttpResponse(NoContent)
      })
    }
  }
}

