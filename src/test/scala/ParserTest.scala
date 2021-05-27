import Utilities.getResourcePath
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.scalatest.words.ShouldVerb
import org.scalatest.{FunSuite, Matchers}

class ParserTest extends FunSuite with Matchers with ShouldVerb {

  implicit val spark = SparkSession
    .builder
    .config("spark.master", "local")
    .appName("Simple Application").getOrCreate()
  val schema = "`environment` STRING,`tradeId` STRING,`tradeStatus` STRING,`tradeDate` DATE,`sideId` STRING,`side` STRING,`amount` INT,`price` DECIMAL(21,2),`currency` STRING,`counterpartyId` STRING,`nominal` DECIMAL(32,2)"


  test("Get message dataframe") {

    val df = Parser.getMessageDataFrame("messageWithMissingFields.json")

    df.count() shouldBe 3
    df.schema.toDDL shouldBe schema
    //It has null values where fields are missing
    val incompleteRecord = df.filter("amount IS NULL AND nominal IS NULL")
    incompleteRecord.show()
    incompleteRecord.select("sideId").head.getString(0) shouldBe "3"
  }

  test("Generate complete message") {
    val df = spark.read
      .schema(StructType.fromDDL(schema))
      .option("header", "true")
      .csv(getResourcePath("outputMessages/complete/df.csv"))

    val completeMessages = Parser.getCompleteMessages(df)
    completeMessages.count() shouldBe 3
    completeMessages.select("sideId").collect().map(_.getString(0)) should equal(Array("1", "2", "3"))

  }

  test("Generate error message") {
    val df = spark.read
      .schema(StructType.fromDDL(schema))
      .option("header", "true")
      .csv(getResourcePath("outputMessages/incomplete/df.csv"))

    val completeMessages = Parser.getCompleteMessages(df)
    completeMessages.count() shouldBe 2
    completeMessages.select("sideId").collect().map(_.getString(0)) should equal(Array("1", "2"))

    val incompleteMessage = Parser.getExceptionMessages(df)
    incompleteMessage.count() shouldBe 1
    incompleteMessage.select("sideId").collect().map(_.getString(0)).head shouldBe "3"
    incompleteMessage.select("errorMessage").collect().map(_.getString(0)).head shouldBe "Missing mandatory field!"


  }

}
