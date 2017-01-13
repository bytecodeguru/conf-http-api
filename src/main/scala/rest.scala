package rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import de.heikoseeberger.akkahttpcirce.CirceSupport._
import io.circe.generic.auto._
import StatusCodes._
import model._

object Boot extends App {

  implicit val system = ActorSystem("http-router")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  case class Config(id: String, name: String, value: String) extends Configuration {
    override def getId: String = id
  }

  // TODO togliere String
  val storage = new InMemoryCrudRepository[String, Config]()
  storage.create(Config("id1", "Giuseppe", "100"))

  val route = pathSingleSlash {
    get {
      complete(storage.getAll)
    } ~
    ( post & entity(as[Config]) ) { c =>
      try {
        storage.create(c)
        // TODO aggiungere location
        complete(Created)
      } catch {
        case _: EntityConflictException => complete(Conflict)
      }
    }
  } ~
  path(Segment) { id =>
    get {
      // TODO è idiomatico?
      storage.read(id) match {
        case Some(c) => complete(c)
        case None => complete(NotFound)
      }
    } ~
    delete {
      storage.read(id) match {
        case Some(c) => {
          storage.delete(id)
          complete(NoContent)
        }
        case None => complete(NotFound)
      }
    } ~
    ( put & entity(as[Config]) ) { c =>
      if (c.id == id) {
        try {
          storage.update(c)
          complete(NoContent)
        } catch {
          case _: EntityNotFoundException => complete(NotFound)
        }
      } else {
        storage.read(id) match {
          case Some(c) => complete(Conflict)
          case None => complete(NotFound)
        }
      }
    }
  }

  val server = Http().bindAndHandle(route, "localhost", 8080)

  server.onComplete(_ => println("Server online at http://localhost:8080/\nPress RETURN to stop..."))

  StdIn.readLine()

  server
    .flatMap(s => s.unbind())
    .onComplete(_ => system.terminate())

}
