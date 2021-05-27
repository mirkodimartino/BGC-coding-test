
object Main extends App {

  Parser.parseMessage("message.json", "outputMessages/complete", "errorMessages")

  Parser.parseMessage("messageWithMissingFields.json", "outputMessages/incomplete", "errorMessages")


}