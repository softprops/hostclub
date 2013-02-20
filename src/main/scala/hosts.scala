package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source

object Hosts {
  def apply(op: Transforms.Op)(file: String = "/etc/hosts") =
    resolve(file).map { f =>
      Parse(Read(f)).fold(println, { chunks =>
        println("chunks %s" format chunks)
        Write(Stringify((Transforms.ensureDefaultSection andThen op)(chunks)),
              new FileWriter(f))
      })
    }
  private def resolve(file: String) =
    new File(file) match {
      case e if (e.exists) => Some(e)
      case _ => None
    }
}
