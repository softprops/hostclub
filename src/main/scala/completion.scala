package hostclub

import scala.io.Source
import scala.util.matching.Regex
import scala.util.Properties.{ envOrNone, envOrElse }

object Completion {

  def script(app: String) =
    Source.fromInputStream(getClass().getResourceAsStream("/completion"))
          .getLines()
          .mkString("\n")
          .replaceAll("""\{\{app\}\}""", app)

  case class Env(w: Int,
                 words: Seq[String],
                 word: String,
                 line: String,
                 point: Int,
                 partialLine: String,
                 partialWords: Seq[String])

  case class Completer(env: Env) {
    def log(opts: String*) = 
      opts.filter(abbr).foreach(println)
    private def abbr(opt: String) =
      if (env.word.isEmpty) true 
      else new Regex("^"+env.word.replaceAll("""[.]""", """\\."""))
                .findFirstIn(opt)
                .isDefined
  }

  def apply(args: Array[String]) = {
    val w = envOrNone("COMP_CWORD").map(_.toInt).getOrElse(0) + 1
    val words = args.toSeq.drop(2).map { a =>
      if (a.size > 0 && a.charAt(0) == '"')
        a.replaceAll("""^"|"$""", "")
      else
        a.replaceAll("""\\ """, "")
    }
    val word = words(w - 1)
    val line = envOrElse("COMP_LINE", "")
    val point = envOrNone("COMP_POINT").map(_.toInt).getOrElse(0)
    val partialLine = if (line.isEmpty) line else line.substring(0, point)
    val partialWords = words.take(w)
    Completer(Env(w, words, word, line, point, partialLine, words))
  }
}
