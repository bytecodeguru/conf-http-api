package rest

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import StatusCodes._
import storage._

case class Config(id: String, name: String, value: String) extends Entity[String] {
  override def getId: String = id
}

object Rest {
  // TODO togliere String
  val storage = new InMemoryStorage[String, Config]()

  def routes = pathSingleSlash {
    // GET /
    get {
      complete(storage.getAll)
    } ~
    // POST /
    ( post & entity(as[Config]) ) { c =>
      complete(storage.create(c) match {
        case Left(_) => HttpResponse(Conflict)
        case Right(_) => HttpResponse(Created, List(headers.Location(s"/${c.getId}")))
      })
    }
  } ~
  path(Segment) { id =>
    // GET /:id
    get {
      complete(storage.read(id) match {
        case None =>    HttpResponse(NotFound)
        case Some(c) => c
      })
    } ~
    // DELETE /:id
    delete {
      storage.read(id) match {
        case Some(c) => {
          storage.delete(id)
          complete(HttpResponse(NoContent))
        }
        case None => complete(HttpResponse(NotFound))
      }
    } ~
    // PUT /:id
    ( put & entity(as[Config]) ) { c =>
      if (c.id == id) {
        complete(storage.update(c) match {
          case Left(_) => HttpResponse(NotFound)
          case Right(_) => HttpResponse(NoContent)
        })
      } else {
        storage.read(id) match {
          case Some(c) => complete(HttpResponse(Conflict))
          case None => complete(HttpResponse(NotFound))
        }
      }
    }
  }
}

