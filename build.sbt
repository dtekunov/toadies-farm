lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion    = "2.6.15"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.di",
      scalaVersion    := "2.13.4"
    )),
    name := "toadies-farm",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"         %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka"         %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka"         %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka"         %% "akka-stream"              % akkaVersion,
      "ch.qos.logback"            % "logback-classic"           % "1.2.3",
      "com.typesafe.slick"        %% "slick"                    % "3.3.3",
      "org.json4s"                %% "json4s-jackson"           % "3.7.0-RC1",
      "org.mongodb.scala"         %% "mongo-scala-driver"       % "2.9.0",

      "com.typesafe.akka"         %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka"         %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"             %% "scalatest"                % "3.1.4"         % Test
    )
  )
