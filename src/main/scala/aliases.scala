package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source

object Aliases {
  private lazy val aliases = {
    ensureExists
    Map(Source.fromFile(file)
              .getLines
              .toSeq
              .map(_.split(" ")
                    .take(2) match {
                      case Array(a,b) => (a, b)
                    }):_*)
  }

  private def serialize(current: Map[String, String]) = {
    ensureExists
    val w = new FileWriter(file)
    current.foreach {
      case (ali, ip) =>
        w.write("%s %s\n".format(ali, ip))
    }
    w.flush
    w.close
  }

  def alias(alias: String, ip: String) =
    serialize(aliases + (alias -> ip))

  def apply(name: String) = aliases.get(name)

  def grep(name: String) =
    aliases.keys.filter(_.startsWith(name)).toSeq

  def ls = aliases

  private def ensureExists =
    if (!file.exists) {
      file.getParentFile.mkdirs()
      file.createNewFile
    }
  private def file = new File(configdir, "aliases")
  private def configdir = new File(System.getProperty("user.home"), ".hc")
}
