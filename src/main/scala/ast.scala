package hostclub

trait Chunk

case class Text(text: String) extends Chunk

case class Invalid(text: String) extends Chunk

trait SectionDelimiter extends Chunk
case class Open(name: String) extends SectionDelimiter
case class Close(name: String) extends SectionDelimiter

case class Section(name: String,
                   mappings: Iterable[(String, Set[String])] = Nil)
     extends Chunk
