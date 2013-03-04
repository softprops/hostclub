package hostclub

import scala.util.parsing.combinator.RegexParsers

object Parse extends RegexParsers {

  override def skipWhitespace = false

  def any: Parser[String] = """.|(\r?\n)+""".r

  def space: Parser[String] = """[^\S\n]+""".r

  def name: Parser[String] = """[0-9A-Za-z-_.]+""".r

  val Ip = ("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
     "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)").r

  val Host = """(?:(?:(?:(?:[a-zA-Z0-9][-a-zA-Z0-9]{0,61})?[a-zA-Z0-9])[.])*(?:[a-zA-Z][-a-zA-Z0-9]{0,61}[a-zA-Z0-9]|[a-zA-Z])[.]?)""".r

  def ip: Parser[String] = Ip

  def host: Parser[String] = Host

  private def hosts: Parser[Seq[Chunk]] =
    (section | anythingBut(section)).*

  def anythingBut[T](p: Parser[T]): Parser[Text] =
    (guard(p) ^^ { g => Text("") }
    | rep1(not(p) ~> any) ^^ {
      t => Text(t.mkString(""))
    })

  def mapping: Parser[(String, Set[String])] =
    ip ~ (rep1(space) ~> rep1sep(host, space)) ^^ {
      case ip ~ hosts => (ip -> hosts.toSet)
    }

  def mappings: Parser[List[(String, Set[String])]] =
    rep(mapping <~ opt(anythingBut(mapping)))

  def section: Parser[Chunk] =
    (sectionOpen ~ anythingBut(sectionClose)) <~ sectionClose ^^ {
      case Open(name) ~ Text(between) =>
        parseMappings(name, between.trim)
    }

  def sectionOpen: Parser[Open] =
    namedOpening | defaultOpening

  def sectionClose: Parser[Close] =
    namedClosing | defaultClosing

  def namedOpening: Parser[Open] =
    "# [hostclub \"" ~> name <~ "\"]" ^^ {
      case name => Open(name)
    }
  
  def defaultOpening: Parser[Open] =
    "# [hostclub]" ^^ {
      case _ => Open("default")
    }
 
  def namedClosing: Parser[Close] =
    "# [/hostclub \"" ~> name <~ "\"]" ^^ {
      case name => Close(name)
    }

  def defaultClosing: Parser[Close] =
    "# [/hostclub]" ^^ {
      case _ => Close("default")
    }

  def apply(in: String) =
    parseAll(hosts, in) match {
      case Parse.Success(chunks, _) => Right(chunks)
      case _ => Left("malformed hosts: %s" format in)
    }

  def parseMappings(name: String, in: String): Chunk =
    parseAll(mappings, in) match {
      case Parse.Success(mappings, _) =>
        Section(name, Map(mappings: _*))
      case _ =>
        Invalid("section source invalid: '%s'" format in)
    }
}



