package hostclub

import java.io.FileWriter

object Write {
  def apply(content: String, writer: FileWriter) = {
    writer.write(content)
    writer.flush()
    writer.close()
  }
}
