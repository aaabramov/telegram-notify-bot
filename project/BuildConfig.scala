import sbt._

object TranzzoKeys extends Dependencies

trait Dependencies {

  //@formatter:off

  // Compile
  lazy val scalaUtils     = "com.tranzzo"         %% "scala-utils"          % "1.4.11"
  lazy val telegram4s     = "com.bot4s"           %% "telegram-core"        % "4.3.0-RC1"
  lazy val telegram4sAkka = "com.bot4s"           %% "telegram-akka"        % "4.3.0-RC1"
  lazy val slick          = "com.typesafe.slick"  %% "slick"                % "3.3.2"
  lazy val slickPg        = "com.github.tminglei" %% "slick-pg"             % "0.18.0"
  lazy val slickHikari    = "com.typesafe.slick"  %% "slick-hikaricp"       % "3.3.2"
  lazy val psql           = "org.postgresql"       % "postgresql"           % "42.2.6"
  lazy val enumeratum     = "com.beachape"        %% "enumeratum"           % "1.5.13"

  lazy val logbackClassic = "ch.qos.logback"       % "logback-classic"      % "1.2.3"

  lazy val clientCore     = "com.tranzzo"         %% "client-core"          % "0.2.2"

  // Test
  lazy val scalaTest      = "org.scalatest"       %% "scalatest"            % "3.0.8"

  //@formatter:on
}
