# BGC-coding-test
### Candidate: *Mirko Dimartino*

#### Question 1
The input message is in *Resources/message.json*. The message can be parsed running the *Main* class.

The message is parsed at line 4 using the *Parser* class.
The output jsons are written as a .json file in *outputMessages/complete*. 

Output content:
   > {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"1","side":"Seller","amount":250,"price":10.26,"currency":"USD","counterpartyId":"123XYZ","nominal":2565.00}
    {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"2","side":"Buyer","amount":150,"price":7.31,"currency":"GBP","counterpartyId":"456abc","nominal":1096.50}
    {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"3","side":"Buyer","amount":100,"price":7.25,"currency":"GBP","counterpartyId":"789opv","nominal":725.00}

#### Question 2
The input message is in *Resources/messageWithMissingFields.json*. Some mandatory fields are missing from 'side' 3. The message can be parsed running the *Main* class.

The message is parsed at line 4 using the *Parser* class.
The output jsons are written as a .json file in *outputMessages/complete*.
Output content:
> {"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"1","side":"Seller","amount":250,"price":10.26,"currency":"USD","counterpartyId":"123XYZ","nominal":2565.00}
{"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"2","side":"Buyer","amount":150,"price":7.31,"currency":"GBP","counterpartyId":"456abc","nominal":1096.50}
{"environment":"BETA","tradeId":"51849291","tradeStatus":"NEW","tradeDate":"2021-05-17","sideId":"3","side":"Buyer","amount":100,"price":7.25,"currency":"GBP","counterpartyId":"789opv","nominal":725.00}
