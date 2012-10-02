package hostclub

object Transforms {
  type Op = Seq[Chunk] => Seq[Chunk]

  def clear: Op = _.filter(_ match {
    case t: Text => true
    case _ => false
  })

  def append(s: Section): Op = s +: _

  def remove(section: String): Op = _.filter({
    case s@Section(sec, mappings) if (sec == section) => false
    case _ => true
  })

  def map(host: String, ip: String, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      val maps = Map(mappings.toSeq:_*)
      val updated  = if (maps.contains(ip)) maps + (ip -> (host :: maps(ip)))
                     else maps + (ip -> List(host))
      s.copy(mappings = updated)
    case c => c
  }

  def unmap(host: String, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      s.copy(mappings = mappings map {
        case (k, v) => (k, v.filterNot(_ == host))
      })
    case c => c
  }

  def remap(host: String, ip: String, section: String = "default"): Op = {
    chunks => map(host, ip, section)(unmap(host, section)(chunks))
  }

  def ip[T](ip: String, f: Seq[String] => T, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      val maps = Map(mappings.toSeq:_*)
      if (maps.isDefinedAt(ip)) f(maps(ip))
      s
    case c => c
  }
  
  def host[T](host: String, f: String => T, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      mappings.foreach {
        case (ip, hosts) => if (hosts.contains(host)) f(ip)
      }
      s
    case c => c
  }
}
