package hostclub

object Main {
  def main(args: Array[String]) {
    def printres = println(io.Source.fromFile(new java.io.File("test.out")).getLines().mkString("\n"))
    println("\ndefault")
    Hosts(identity)
    printres
    println("\nmap api...")
    Hosts(Transforms.map("api.api.api", "192.169.0.202"))
    printres
    println("\nclear")
    Hosts(Transforms.clear)
    printres
  }
}
