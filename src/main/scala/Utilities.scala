

object Utilities {

  def getResourcePath(fileName: String) = {
    import java.nio.file.Paths
    val res = getClass.getClassLoader.getResource(fileName)
    val file = Paths.get(res.toURI).toFile
    file.getAbsolutePath
  }

}
