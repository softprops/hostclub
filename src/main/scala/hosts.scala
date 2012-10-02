package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source

object Hosts {
  def file = Option(new File(getClass().getResource("/hosts").getFile))
                    .filter(_.exists)
                    .headOption

  def apply(op: Transforms.Op) = file.map { f =>
    Parse(Read(f)).fold(println, { chunks =>
      Write(Stringify(op(chunks)), new FileWriter(new File("test.out")))
    })
  }
}
