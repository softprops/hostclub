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

  val Ok   = 0
  val Fail = 1

  def fail(msg: String) = {
    System.err.println(msg)
    Fail
  }

  def apply(args: Array[String]) = {
    args.toList match {
      case Nil =>
        println(Help)
        Ok
      case "map" :: host :: ip :: _ =>
        (host, aliasOr(ip)) match {
          case (h @ Parse.Host(), ip @ Parse.Ip()) =>
            Hosts(Transforms.map(h, ip))()
            Ok
          case (h, ip) =>
            fail("invalid host %s or ip %s" format(h, ip))
        }
      case "alias" :: name :: rest =>
        if (rest.isEmpty) {          
          Aliases(name).getOrElse("alias not found")
          Ok
        } else rest(0) match {
          case ip @ Parse.Ip() =>
            Aliases.alias(name, ip)
            Ok
          case invalid =>
            fail("invalid ip %s" format invalid)
        }
      case "aliases" :: _ =>
        Aliases.ls.foreach {
          case (alias, ip) =>
            println("%s-%s %s%s%s %s".format("\033[1;30m", Console.RESET, Console.CYAN, alias, Console.RESET, ip))
        }
        Ok
      case "unmap" :: host :: _ =>
        Hosts(Transforms.unmap(host))()
        Ok
      case "clear" :: _ =>
        Hosts(Transforms.clear)()
        Ok
      case "host" :: host :: _ =>
        Hosts(Transforms.host(host, { println(_) }))()
        Ok
      case "ip" :: ip :: _ =>
        aliasOr(ip) match {
          case ip @ Parse.Ip() =>
            Hosts(Transforms.ip(aliasOr(ip), { _.foreach(println) }))()
            Ok
          case invalid =>
            fail("invalid ip %s" format invalid)
        }
      case "swap" :: a :: b :: _ =>
        (aliasOr(a), aliasOr(b)) match {
          case (ipa @ Parse.Ip(), ipb @ Parse.Ip()) =>
            Hosts(Transforms.swap(ipa, ipb))()
            Ok
          case (ia, ib) =>
            fail("one or both of %s or %s are invalid ips" format(ia, ib))
        }
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
        Ok
      case "help" :: _ =>
        println(Help)
        Ok
      case "completion" :: rest =>
        rest match {
          case Nil =>
            println(Completion.script("hc"))
          case _ =>
            val compl = Completion(args)
            compl.env.w match {
              case 2 => compl.log(Completions.cmds:_*) // hc <tab>
              case n if (n > 2) =>
                val options = Completions.of(compl.env.words(1))(compl.env)
                compl.log(options: _*)
            }
        }
        Ok
      case _ =>
        println(Help)
        Ok
    }
  }
}

class Script extends AppMain {
  def run(conf: AppConfiguration) =
    new Exit(Script(conf.arguments))
}

class Exit(val code: Int) extends xsbti.Exit
