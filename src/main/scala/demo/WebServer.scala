package demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.Http
import scala.io.StdIn
import scala.concurrent.Future
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatcher
import akka.http.scaladsl.model.StatusCodes
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object WebServer {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  var hosts: List[Host] = List(Host("localhost", "127.0.0.1"))
  case class Host(hostname: String, ip: String)
  implicit val hostFormat = jsonFormat2(Host)
  def searchHost(searchStr: String): Future[List[Host]] = Future {
    hosts.filter(h => List(h.ip, h.hostname).exists(_.contains(searchStr)))
  }
  def addHost(name: String, ip: String) = Future {
    hosts = Host(name, ip) :: hosts
  }

  val routes = {
    get {
      path("host") {
        parameter("search") { search =>
          onSuccess(searchHost(search)) { list => complete(list) }
        }
      }
    } ~
      post {
        path("host" / "add") {
          parameters("name", "ip") { (name, ip) =>
            onSuccess(addHost(name, ip)) { complete(StatusCodes.Created) }
          }
        }
      }
  }

  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 8080
    println("Try starting on " + host + ":" + port + "...")
    val bindingFuture = Http().bindAndHandle(routes, host, port)
//    println("Started. Press Enter to Stop")
//    StdIn.readLine()
//    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())

  }
}
