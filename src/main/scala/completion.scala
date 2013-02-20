package hostclub

import scala.util.matching.Regex
import scala.util.Properties.{ envOrNone, envOrElse }

object Completion {
  val Script = """COMP_WORDBREAKS=${COMP_WORDBREAKS/=/}
COMP_WORDBREAKS=${COMP_WORDBREAKS/@/}
export COMP_WORDBREAKS

if type complete &>/dev/null; then
  _hc_completion () {
    local si="$IFS"
    IFS=$'\n' COMPREPLY=($(COMP_CWORD="$COMP_CWORD" \
                           COMP_LINE="$COMP_LINE" \
                           COMP_POINT="$COMP_POINT" \
                           hc completion -- "${COMP_WORDS[@]}" \
                           2>/dev/null)) || return $?
    IFS="$si"
  }
  complete -F _hc_completion hc
elif type compdef &>/dev/null; then
  _hc_completion() {
    si=$IFS
    compadd -- $(COMP_CWORD=$((CURRENT-1)) \
                 COMP_LINE=$BUFFER \
                 COMP_POINT=0 \
                 hc completion -- "${words[@]}" \
                 2>/dev/null)
    IFS=$si
  }
  compdef _hc_completion hc
elif type compctl &>/dev/null; then
  _hc_completion () {
    local cword line point words si
    read -Ac words
    read -cn cword
    let cword-=1
    read -l line
    read -ln point
    si="$IFS"
    IFS=$'\n' reply=($(COMP_CWORD="$cword" \
                       COMP_LINE="$line" \
                       COMP_POINT="$point" \
                       hc completion -- "${words[@]}" \
                       2>/dev/null)) || return $?
    IFS="$si"
  }
  compctl -K _hc_completion hc
fi"""
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
