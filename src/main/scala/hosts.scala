package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source

object Hosts {
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
