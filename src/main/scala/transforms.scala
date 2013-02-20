package hostclub

object Transforms {
  type Op = Seq[Chunk] => Seq[Chunk]

  def ensureDefaultSection: Op = { cs =>
    val present = cs.exists {
      case s@Section(sec, mappings) if (sec == "default") => true
      case _ => false
    }
    if (present) cs else append(Section("default"))(cs)
  }

  /** clear all managed host mappings */
  def clear: Op = _.filter({
    case t: Text => true
    case _ => false
  })

  /** append a new section of host mappings */
  def append(s: Section): Op = _ :+ s

  /** remove a section by name */
  def remove(section: String): Op = _.filter({
    case s@Section(sec, mappings) if (sec == section) => false
    case _ => true
  })

  def swap(previp: String, nextip: String, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      val maps = Map(mappings.toSeq:_*)
      val updated = if (maps.contains(previp)) {
                      maps + (nextip -> maps(previp)) - previp
                    } else maps
      s.copy(mappings = updated)
    case c => c
  }

  /** map a host to given ip */
  def map(host: String, ip: String, section: String = "default"): Op = { cs =>
    unmap(host)(cs).map {
      case s@Section(sec, mappings) if (sec == section) =>
        val maps = Map(mappings.toSeq:_*)
        val updated  = if (maps.contains(ip)) maps + (ip -> (maps(ip) + host))
                     else maps + (ip -> Set(host))
        s.copy(mappings = updated)
      case c => c
    }
  }

  /** unmap host */
  def unmap(host: String, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      s.copy(mappings = (mappings map {
        case (k, v) => (k, v.filterNot(_ == host))
      }).filter {
        case (k, v) => !v.isEmpty
      })
    case c => c
  }

  /** remap a host to a given ip */
  def remap(host: String, ip: String, section: String = "default"): Op = {
    chunks => map(host, ip, section)(unmap(host, section)(chunks))
  }

  /** resolves which hosts map to a given ip */
  def ip[T](ip: String, f: Set[String] => T, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      val maps = Map(mappings.toSeq:_*)
      if (maps.isDefinedAt(ip)) f(maps(ip))
      s
    case c => c
  }
  
  /** resolves which ips map to a given host */
  def host[T](host: String, f: String => T, section: String = "default"): Op = _.map {
    case s@Section(sec, mappings) if (sec == section) =>
      mappings.foreach {
        case (ip, hosts) => if (hosts.contains(host)) f(ip)
      }
      s
    case c => c
  }
}
