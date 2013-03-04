organization := "me.lessis"

name := "hostclub"

version := "0.1.0"

description := "A host mapping manager"

licenses <<= version(v =>
      Seq("MIT" ->
          url("https://github.com/softprops/hostclub/blob/%s/LICENSE" format v)))

homepage :=
  Some(new java.net.URL("https://github.com/softprops/hostclub/"))

seq(lsSettings :_*)

resolvers += Classpaths.typesafeResolver

libraryDependencies <+= (sbtVersion)(
  "org.scala-sbt" %
   "launcher-interface" %
    _ % "provided")

publishArtifact in Test := false

publishMavenStyle := true

pomExtra := (
  <scm>
    <url>git@github.com:softprops/hostclub.git</url>
    <connection>scm:git:git@github.com:softprops/hostclub.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>http://github.com/softprops</url>
    </developer>
  </developers>)

seq(lsSettings:_*)

LsKeys.tags in LsKeys.lsync := Seq("bash", "dns", "unix", "conscript")
