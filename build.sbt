import TranzzoKeys._

val baseSettings: BaseSettings = BaseSettings(
  version = Version.mk("0.1.1", persistTo = "version"),
  scalaVer = `2.12.8`,
  organization = Tranzzo
)

lazy val `telegram-bot-notify` = create single module named "telegram-bot-notify" withConfig(
  compileLibs = Seq(
    telegram4s,
    telegram4sAkka,
    scalaUtils,
    logbackClassic,
    slick,
    slickHikari,
    slickPg
  ),
  testLibs = Seq(
  ),
  additionalSettings = Seq(
    scalacOptions ++= Seq("-Ypartial-unification"),
    dockerBaseImage := "openjdk:8-jdk",
    dockerRepository := Some("eu.gcr.io"),
    dockerUsername := Some("requeue"),
    dockerExposedPorts := Seq(9000)
  ),
  plugins = Seq(JavaAppPackaging)
) from baseSettings