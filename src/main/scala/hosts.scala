package hostclub

import java.io.{ File, FileWriter }
import scala.io.Source


object Ops {
  type Op = Seq[Chunk] => Seq[Chunk]

  def map(host: String, ip: String, section: String = "default"): Op = _.map {
    case s@Section(section, mappings) =>
      val maps = Map(mappings.toSeq:_*)
      val updated  = if (maps.contains(ip)) maps + (ip -> (host :: maps(ip)))
                     else maps + (ip -> List(host))
      s.copy(mappings = updated)
    case c => c
  }

  def unmap(host: String, ection: String = "default"): Op = _.map {
    case s@Section(section, mappings) =>
      s.copy(mappings = mappings map {
        case (k, v) => (k, v.filterNot(_ == host))
      })
    case c => c
  }

  def remap(host: String, ip: String, section: String = "default"): Op = {
    chunks => map(host, ip, section)(unmap(host, section)(chunks))
  }

  def ip[T](ip: String, f: Seq[String] => T, section: String = "default"): Op = _.map {
    case s@Section(section, mappings) =>
      val maps = Map(mappings.toSeq:_*)
      if (maps.isDefinedAt(ip)) f(maps(ip))
      s
    case c => c
  }
  
  def host[T](host: String, f: String => T, section: String = "default"): Op = _.map {
    case s@Section(section, mappings) =>
      mappings.foreach {
        case (ip, hosts) => if (hosts.contains(host)) f(ip)
      }
      s
    case c => c
  }
}

object Hosts {
  def file = Option(new File(getClass().getResource("/hosts").getFile)).filter(_.exists).headOption

  def apply(op: Seq[Chunk] => Seq[Chunk]) = file.map { f =>
    val in = Source.fromFile(f).getLines().mkString("\n")
    Parse(in).fold(println, { chunks =>
      write(Serialize(op(chunks)), new FileWriter(new File("test.out")))
    })
  }

  def write(content: String, writer: FileWriter) = {
    writer.write(content)
    writer.flush()
    writer.close()
  }
}
