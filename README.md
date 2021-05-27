# BGC-coding-test
### Candidate: *Mirko Dimartino*
This is a Scala project and should be run via SBT.
#### Question 1
The input message is in **Resources/message.json**. The message can be parsed running the *Main* class.

The message is parsed at line 4 using the *Parser* class.
The output jsons are written as a .json file in **outputMessages/complete**. 

Output content:
   > {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"1","side":"Seller","amount":250,"price":10.26,"currency":"USD","counterpartyId":"123XYZ","nominal":2565.00}
    {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"2","side":"Buyer","amount":150,"price":7.31,"currency":"GBP","counterpartyId":"456abc","nominal":1096.50}
    {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"3","side":"Buyer","amount":100,"price":7.25,"currency":"GBP","counterpartyId":"789opv","nominal":725.00}

#### Question 2
The input message is in **Resources/messageWithMissingFields.json**. Some mandatory fields are missing from 'sideId'=3. The message can be parsed running the *Main* class.

The message is parsed at line 6 using the *Parser* class.
The output jsons are written as a .json file in **outputMessages/incomplete**.
The content of the successfully parsed messages: 
> {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"1","side":"Seller","amount":250,"price":10.26,"currency":"USD","counterpartyId":"123XYZ","nominal":2565.00}
{"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"2","side":"Buyer","amount":150,"price":7.31,"currency":"GBP","counterpartyId":"456abc","nominal":1096.50}


The exception json for the incomplete side is **erroreMessages** as a .json file.
Exception message content:
> {"sideId":"3","errorMessage":"Missing mandatory field!"}

### Question 3
The unit tests can be found in the *ParserTest* class.

### Question 4
To implement the parser as a streaming solution I would make use of the *Spark Streaming* extension of the core Spark API. 

This would allow processing real-time data from streaming platforms, by plugging the current Parser code to the existing streaming system in place (i.e., where the messages are coming from as streams).
For example, we could wire the parser onto listening from Kafka queues and parsing the messages in real time. This would give us fast recovery from failures as well as better load balancing and resource usage as out-of-the-box features.

Another (obvious) change I would make is configuring the parser so that it could run on a remote large/scalable cluster instead of running locally. For instance, the parser could run on a Amazon EMR or Databricks cluster to handle high volume of data processing.
(Note for the reviewer: I would be happy to simulate different high-volume loads but this week I have been very busy with work so my time to complete this exercise is very limited. )

### Question 5
We should be considering for example: 
1. Number of messages parsed every [time_period], for instance **n-messages/hour**.
2. Consumer max lag.  Understanding how large the queue of incoming messages grows per topic is important for real-time systems. 
3. Frequency of messages that go to the Dead Letter queue.
4. Saturation. A measure of how close to fully utilized the service’s resources are. For instance, saturation of space (i.e., s3 buckets side) or cluster size saturation (i.e., by giving a threshold of Spark tasks failures).

We could export the metrics from the code to an ad-hoc monitoring service, such as Datadog. For example, we could use the Datadog library to model such metrics, collecting them from the code and pushing them to a metrics daemon. 
Then we can manage the metrics form the platform dashboards. This way, we can easily implement an alerting system using the platform features. For instance, from the Datadog dashboard we can directly create 'alerts' based on some metrics thresholds.
In our example, we can create an alert using the frequency of message that go to the Dead Letter queue. We can define a threshold and alert when the threshold is exceeded. This alerts can be sent via emails to specific team members or group emails or even via sending notifications to Slack. (I've done this before, it's pretty easy in Datadog. Of course, we can use different Metrics platforms)