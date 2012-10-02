package hostclub

object Stringify {
  val Newline = "\n"

  def apply(chunks: Seq[Chunk]) = {
    val b = new StringBuffer()

    def open(name: String) = {
      b.append("# [hostclub")
      name match {
        case "default" => b.append("]")
        case name => b.append(" \"").
                      append(name).
                      append("\"]")
      }
      b.append(Newline)
    }

    def close(name: String) = {
      b.append("""# [/hostclub""")
      name match {
        case "default" => b.append("]")
        case name => b.append(" \"").
                        append(name).
                        append("\"]")
      }
    }
    
    def mapping(m: (String, Seq[String])) = m match {
      case (ip, hosts) =>
        b.append(ip).
          append("\t").
          append(hosts.mkString(" ")).
          append(Newline)
    }

    chunks.foreach {
      case Text(txt) =>
        b.append(txt)
      case Section(name, mappings) =>
        open(name)
        mappings.foreach(mapping)
        close(name)
      case Invalid(inv) =>
        b.append(inv) // fixme: what shall we do here?      
    }

    b.toString
  }
}
