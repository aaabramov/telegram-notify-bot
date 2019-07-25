resolvers += "Tranzzo Releases" at s"https://nexus.tranzzo.com/repository/tranzzo_releases"
credentials += Credentials(
  realm = "Sonatype Nexus Repository Manager",
  host = "nexus.tranzzo.com",
  userName = sys.env("TRZ_NEXUS_USER"),
  passwd = sys.env("TRZ_NEXUS_PSWD")
)

addSbtPlugin("com.tranzzo"       % "sbt-plugin"          % "2.4.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % "1.3.25")