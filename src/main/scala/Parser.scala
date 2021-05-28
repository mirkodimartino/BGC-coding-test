
import Utilities.getResourcePath
import org.apache.spark.sql.functions.{explode, lit}
import org.apache.spark.sql.types.{DataTypes, DateType, IntegerType}
import org.apache.spark.sql.{DataFrame, SparkSession}

object Parser {

  implicit val spark = SparkSession
    .builder
    .config("spark.master", "local")
    .appName("Simple Application").getOrCreate()
  val resourcePath = "src/main/Resources"

  /**
   * Method that parsers the message giving an input path, output path and errorPath
   *
   * @param inputPath  the path where the input message is located
   * @param outputPath the path where the parser should save the parsed messages
   * @param errorPath  the path for the dead Letter Queue file
   */
  def parseMessage(inputPath: String, outputPath: String, errorPath: String): Unit = {
    val df = getMessageDataFrame(inputPath)
    df.show()
    val completeJsons = getCompleteMessages(df)
    writeMessages(completeJsons, outputPath)
    val errorMessages = getExceptionMessages(df)
    writeMessages(errorMessages, errorPath)
  }

  /**
   * Method that generate the dataframe for a parsed input message
   *
   * @param filePath the path for the input message
   * @param spark    the implicit spark session
   * @return the dataframe related to the input message
   */

  def getMessageDataFrame(filePath: String)(implicit spark: SparkSession): DataFrame = {
    import spark.implicits._
    spark.read.json(getResourcePath(filePath))
      .select(
        $"environment",
        $"trade.tradeId" as "tradeId",
        $"trade.tradeStatus" as "tradeStatus",
        $"trade.tradeDate" as "tradeDate",
        explode($"trade.tradeSides") as "array")
      .select(
        $"environment",
        $"tradeId",
        $"tradeStatus",
        $"tradeDate".cast(DateType),
        $"array.sideId",
        $"array.side",
        $"array.amount".cast(IntegerType),
        $"array.price".cast(DataTypes.createDecimalType(21, 2)),
        $"array.currency",
        $"array.counterpartyId"
      )
      .withColumn("nominal", $"price" * $"amount")
  }

  /**
   * Method that returns the dataframe for the complete messages
   *
   * @param dataFrame    the input dataframe
   * @param sparkSession the implicit spark session
   * @return the dataframe with all the complete/parsed messages
   */
  def getCompleteMessages(dataFrame: DataFrame)(implicit sparkSession: SparkSession): DataFrame = {
    import sparkSession.implicits._
    //We assume that the sideId is always present
    dataFrame.filter(
      $"environment".isNotNull &&
        $"tradeId".isNotNull &&
        $"tradeStatus".isNotNull &&
        $"tradeDate".isNotNull &&
        $"side".isNotNull &&
        $"amount".isNotNull &&
        $"price".isNotNull &&
        $"nominal".isNotNull &&
        $"currency".isNotNull &&
        $"counterpartyId".isNotNull
    )
  }

  /**
   * Method that returns the dataframe for the messages that end up in the Dead Letter queue
   *
   * @param dataFrame    the input dataframe
   * @param sparkSession the implicit spark session
   * @return the dataframe with the exceptions
   */
  def getExceptionMessages(dataFrame: DataFrame)(implicit sparkSession: SparkSession): DataFrame = {
    import sparkSession.implicits._
    //We assume that the sideId field is always present
    dataFrame.filter(
      $"environment".isNull ||
        $"tradeId".isNull ||
        $"tradeStatus".isNull ||
        $"tradeDate".isNull ||
        $"side".isNull ||
        $"amount".isNull ||
        $"price".isNull ||
        $"nominal".isNull ||
        $"currency".isNull ||
        $"counterpartyId".isNull
    )
      .select("sideId").withColumn("errorMessage", lit("Missing mandatory field!"))
  }

  /**
   * Method that write the dataframes as json files and prints the jsons to console
   *
   * @param dataframe  the input dataframe message
   * @param outputFile the path to the output location
   */
  def writeMessages(dataframe: DataFrame, outputFile: String): Unit = {
    printToConsolle(dataframe)
    dataframe.repartition(1).write.mode("overwrite").json(resourcePath + "/" + outputFile)
  }

  /**
   * Method that prints the df to console as json messages
   *
   * @param df the dataframe containing the records to be printed as jsons
   */
  def printToConsolle(df: DataFrame) = df.foreach(row => println(row.json))

}
