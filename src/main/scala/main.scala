package hostclub

object Main {
  def main(args: Array[String]) {
    def printres = println(Read(new java.io.File("test.out")))

    def trytrans(name: String, op: Transforms.Op) {
      println("\n%s" format name)
      Hosts(op)
      printres
    }

    trytrans("clear", Transforms.clear)

    trytrans("append", Transforms.append(Section("appended", Nil)))

    trytrans("remove", Transforms.remove("api"))

    trytrans("map", Transforms.map("api.host.io", "192.169.0.202"))

    // unmap

    trytrans("ip", Transforms.ip("127.0.0.1", { println(_) }))

    trytrans("host", Transforms.host("foo.com", { println(_) }))
  }
}
