package hostclub

object Main {
  def main(args: Array[String]) {
    def printres = println(io.Source.fromFile(new java.io.File("test.out")).getLines().mkString("\n"))
    Hosts(identity)
    printres
    Hosts(Ops.map("api.api.api", "192.169.0.202"))
    printres
  }
}
