package hostclub

import xsbti.{ AppMain, AppConfiguration }

object Script {  
  object Completions {
    val cmds = "map" :: "unmap" :: "clear" :: "aliases" :: "alias" :: "host" :: "ip" :: "ls" :: "help" :: "swap" :: "completion" :: Nil
    type Complete = Completion.Env => Seq[String]
    val NoOp: Complete = { _ => Nil }
    val of: Map[String, Complete] = 
      Map("map" -> { env =>
        env.w match {
          case 3 =>
            if (env.word.isEmpty) Seq(":host")
            else Hosts.grep().host(env.word)
          case 4 =>
            if (env.word.isEmpty) Seq(":ip")
            else Hosts.grep().ip(env.word) ++ Aliases.grep(env.word)
          case _ => Seq.empty[String]
        }
      },
      "unmap" -> { env =>
        env.w match {
          case 3 =>
            if (env.word.isEmpty) Seq(":host")
            else Hosts.grep().host(env.word)
          case _ => Seq.empty[String]
        }
      },
      "clear"      -> NoOp,
      "aliases"    -> NoOp,
      "alias"      -> { env =>
        env.w match {
          case e =>
            if (env.word.isEmpty) Seq(":name")
            else Aliases.grep(env.word)
        }
      },
      "host"       -> { env =>
        env.w match {
          case 3 =>
            if (env.word.isEmpty) Seq(":host")
            else Hosts.grep().host(env.word)
          case _ => Seq.empty[String]
        }
      },
      "ip"         -> { env =>
        env.w match {
          case 3 =>
            if (env.word.isEmpty) Seq(":ip")
            else Hosts.grep().ip(env.word) ++ Aliases.grep(env.word)
          case _ => Seq.empty[String]
        }
      },
      "ls"         -> NoOp,
      "help"       -> NoOp,
      "swap" -> { env =>
        env.w match {
          case 3 =>
            if (env.word.isEmpty) Seq(":previous_ip")
            else Hosts.grep().ip(env.word) ++ Aliases.grep(env.word)
          case 4 =>
            if (env.word.isEmpty) Seq(":target_ip")
            else Hosts.grep().ip(env.word) ++ Aliases.grep(env.word)
          case _ => Seq.empty[String]
        }
      },
      "completion" -> NoOp)
  }

  val Help = """
  | Usage: 
  |
  |     %shc%s <command> %s<options>%s
  |
  | Commands:
  |
  |     %s
  |
  | Examples:
  |
  |     %shc%s map %sfoo.com 127.0.0.1%s
  |""".stripMargin.format(
    Console.CYAN, Console.RESET,
    "\033[1;30m", Console.RESET,
    Completions.cmds.mkString(", "),
    Console.CYAN, Console.RESET,
    "\033[1;30m", Console.RESET
  )

  def aliasOr(ip: String) = Aliases(ip).getOrElse(ip)

  def apply(args: Array[String]) = {
    args.toList match {
      case Nil =>
        println(Help)
      case "map" :: host :: ip :: _ =>
        Hosts(Transforms.map(host, aliasOr(ip)))()
      case "alias" :: name :: rest =>
        if (rest.isEmpty) println(Aliases(name).getOrElse("alias not found"))
        else Aliases.alias(name, rest(0))
      case "aliases" :: _ =>
        Aliases.ls.foreach {
          case (alias, ip) =>
            println("%s-%s %s%s%s %s".format("\033[1;30m", Console.RESET, Console.CYAN, alias, Console.RESET, ip))
        }
      case "unmap" :: host :: _ =>
        Hosts(Transforms.unmap(host))()
      case "clear" :: _ =>
        Hosts(Transforms.clear)()
      case "host" :: host :: _ =>
        Hosts(Transforms.host(host, { println(_) }))()
      case "ip" :: ip :: _ =>
        Hosts(Transforms.ip(aliasOr(ip), { _.foreach(println) }))()
      case "swap" :: a :: b :: _ =>
        Hosts(Transforms.swap(aliasOr(a), aliasOr(b)))()
      case "ls" :: rest =>        
        Hosts.ls().map(_.foreach {
          case Section(name, mappings)
            if (rest.isEmpty || rest.headOption.map(_ == name).getOrElse(false)) =>
            println("\033[1;30m" + "# %s".format(name) + Console.RESET)
            mappings.foreach {
              case (ip, hosts) =>
                println("%s-%s %s%13s%s %s" format("\033[1;30m", Console.RESET,
                                                   Console.CYAN, ip, Console.RESET,
                                                   hosts.mkString(", ")))
            }
          case _ => ()
        })
      case "help" :: _ =>
        println(Help)
      case "completion" :: rest =>
        rest match {
          case Nil =>
            println(Completion.Script)
          case _ =>
            val compl = Completion(args)
            compl.env.w match {
              case 2 => compl.log(Completions.cmds:_*) // hc <tab>
              case n if (n > 2) =>
                val options = Completions.of(compl.env.words(1))(compl.env)
                compl.log(options: _*)
            }
        }
      case _ => ()
    }
    1
  }
}

class Script extends AppMain {
  def run(conf: AppConfiguration) =
    new Exit(Script(conf.arguments))
}

class Exit(val code: Int) extends xsbti.Exit
