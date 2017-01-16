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
    // TODO sembra che se non riesce a deserializzare restituisce Bad request
    ( post & entity(as[Config]) ) { c =>
      try {
        storage.create(c)
        // TODO aggiungere location
        complete(HttpResponse(Created, List(headers.Location(s"/${c.getId}"))))
      } catch {
        case _: DuplicatedIdException => complete(HttpResponse(Conflict))
      }
    }
  } ~
  path(Segment) { id =>
    // GET /:id
    get {
      // TODO è idiomatico? NO vedi sotto
      storage.read(id) match {
        case Some(c) => complete(c)
        case None => complete(HttpResponse(NotFound))
      }
      // se vado a capo non compila!
      // storage.read(id).fold(complete(NotFound -> None))(c => complete(c))
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
        try {
          storage.update(c)
          complete(HttpResponse(NoContent))
        } catch {
          case _: EntityNotFoundException => complete(HttpResponse(NotFound))
        }
      } else {
        storage.read(id) match {
          case Some(c) => complete(HttpResponse(Conflict))
          case None => complete(HttpResponse(NotFound))
        }
      }
    }
  }
}

