package main

import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import scala.io.StdIn
import rest.Rest

object Boot extends App {

  implicit val system = ActorSystem("http-router")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val server = Http().bindAndHandle(Rest.routes, "localhost", 8080)

  server.onComplete(_ => println("""
    Server online at http://localhost:8080/
    Press RETURN to stop...
  """))

  StdIn.readLine()

  server
    .flatMap(s => s.unbind())
    .onComplete(_ => system.terminate())

}
