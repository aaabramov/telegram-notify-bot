package com.github.aaabramov.telegram.notify

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.bot4s.telegram.clients.AkkaHttpClient
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.PostgresProfile
import slogging.{LazyLogging, LogLevel, LoggerConfig, PrintLoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * @author Andrii Abramov on 2019-07-25.
  */
object Boot extends LazyLogging with App with PostgresProfile {

  LoggerConfig.factory = PrintLoggerFactory()
  LoggerConfig.level = LogLevel.TRACE

  //  def main(args: Array[String]): Unit = {
  implicit val as: ActorSystem = ActorSystem()
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContext = as.dispatcher

  import api._

  lazy val db: Database = Database.forConfig("psql")

  val config: Config = ConfigFactory.load()

  val akkaClient = new AkkaHttpClient(config.getString("services.telegram.token"))

  new NotifyBot(
    akkaClient,
    config,
    new UserRepo(db),
    new GroupsRepo(db)
  )
    .run()
    .andThen {
      case Success(_)  =>
        println("Bot started")
      case Failure(ex) => println(s"failed to start bot: $ex")
    }
    .andThen {
      case _ => db.close()
    }

  //  }


}
