package hostclub

import java.io.File

object Read {
  def apply(f: File) =
    io.Source.fromFile(f).getLines().mkString("\n")
}
