import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import sputter.jvm.components.RestRouter
import sputter.jvm.components.contact.ContactApiImpl
import sputter.jvm.components.contact.datastore.ContactServiceImpl
import sputter.jvm.datastores.mock.contact.ContactMockDataStore
import akka.http.scaladsl.server.Directives._
import sputterdemo.shared.TypedApi
import sputter.jvm.components.akkahttp.CorsSupport
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import sputter.jvm.components.registration.RegistrationApiImpl
import sputter.jvm.components.registration.datastore.RegistrationServiceImpl
import sputter.jvm.datastores.mock.registration.RegistrationMockDataStore
import sputter.shared.{RegistrationForm, SimpleRegistrationForm}

trait ApiImpl extends TypedApi with ContactApiImpl with RegistrationApiImpl

/**
  * Demo server using sputter components.
  *
  * Run this with `sbt "project demo_jvm" run` from the directory containing
  * `build.sbt`, or build a fat jar with `sbt "project demo_jvm" assembly`.
  */
object Server extends App with CorsSupport with ApiImpl {

  // CORS config
  override val corsAllowOrigins: List[String] = List("http://localhost:8090")

  override val corsAllowedHeaders: List[String] = List("Origin", "Authorization", "Content-Type",
    "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = false

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )

  val conf = ConfigFactory.load()

  implicit val system = ActorSystem("sputter-demo-server")
  implicit val materializer = ActorMaterializer()

  val logger = Logging(system, getClass)

  /**
    * Routes with mock logging endpoints.
    */
  val contactService = new ContactServiceImpl(new ContactMockDataStore())
  val registrationService = new RegistrationServiceImpl[Form](
    new RegistrationMockDataStore[Form]())

  val route = cors {
    get {
      // allows us to test the server is actually online with `curl http://localhost:8080/`
      pathEndOrSingleSlash {
        complete("OK")
      }
    } ~
    post {
      // generic API request handler
      path("api" / Segments) { s =>
        entity(as[String]) { e =>
          // todo: would be nice to `reject` calls that resulted in errors instead
          // of blindly `complete`-ing everything
          complete {
            RestRouter.route[TypedApi](Server)(
              autowire.Core.Request(
                s,
                upickle.default.read[Map[String, String]](e)
              )
            )
          }
        }
      }
    }
  }

  /**
    * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
    */
  sys.addShutdownHook(system.terminate())

  val serverHost = conf.getString("server.host")
  val serverPort = conf.getInt("server.port")

  Http().bindAndHandle(route, serverHost, serverPort)
  logger.info(s"Server running at $serverHost:$serverPort")
}
