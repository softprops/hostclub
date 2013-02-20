package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source

object Hosts {
   case class Grep(file: String) {

    def host(in: String): Seq[String] =
      resolve(file).map { f =>
        Parse(Read(f)).fold(_ => Seq.empty[String], { cs =>
          (Set.empty[String] /: cs) {
            case (set, Section(_, mappings)) =>
              val hs: Set[String] = (mappings.flatMap { case (_, hosts) => hosts.filter(_.startsWith(in)) }).toSet
              set ++ hs
            case (s, _) => s
          }.toSeq
        })
      }.getOrElse(Seq.empty[String])

    def ip(in: String): Seq[String] =
      resolve(file).map { f =>
        Parse(Read(f)).fold(_ => Seq.empty[String], { cs =>
          (Set.empty[String] /: cs) {
            case (set, Section(_, mappings)) =>
              val ips = mappings.map { case (ip, _) => ip }
              set ++ ips
            case (s, _) => s
          }.toSeq
        })
      }.getOrElse(Seq.empty[String])
  }
  def grep(file: String = "/etc/hosts") = Grep(file)
  def apply(op: Transforms.Op)(file: String = "/etc/hosts") =
    resolve(file).map { f =>
      Parse(Read(f)).fold(println, { chunks =>
        Write(Stringify((Transforms.ensureDefaultSection andThen op)(chunks)),
              new FileWriter(f))
      })
    }
  def ls(file: String = "/etc/hosts") =
    resolve(file).map { f =>
      Parse(Read(f)).fold(_ => Seq.empty[Chunk], identity)
    }
  private def resolve(file: String) =
    new File(file) match {
      case e if (e.exists) => Some(e)
      case _ => None
    }
}
