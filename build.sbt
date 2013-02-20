organization := "me.lessis"

name := "hostclub"

version := "0.1.0-SNAPSHOT"

seq(lsSettings :_*)

resolvers += Classpaths.typesafeResolver

libraryDependencies <+= (sbtVersion)(
  "org.scala-sbt" %
   "launcher-interface" %
    _ % "provided")
