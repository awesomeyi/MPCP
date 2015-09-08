API Usage
========
API Usage guide. Requests take the following form `/API/action`.

### General form

The server will return a JSON Object with the following properties:
+ status: Success/Failure
+ message: On failure, return error message, return requested data on success

### register (POST)

User registration.
+ username: Desired username
+ password: Desired password

### login (POST)

User login.
+ username: Desired username
+ password: Desired password

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message
+ authcode: Returns an authentication code on success

**From now on, ALL requests must have `authcode` attached as a URL parameter**

### logout (GET)

User logout action.

### verify (GET)

Verify validity of `authcode`. 

### username (GET)

Fetches the username of user.

**Bank specific commands. Requests take the form `/API/bank/action`.**

### accounts (GET)

Returns all user accounts in the following form: 

```
[ //Array of account objects
  { 
    accountid
    bankname
    balance
    name
  }
]
```

### transfers (GET)

Returns all transfers in the following form:

```
{
  requested: //List of requested transfers objects
  [ 
    //Individual transfer object with the following properties
    { 
      transferid
      username
      amount
      fromcheck
      tocheck
      complete
      starttime
      endtime
    }
  ],
  
  recieved: //List of recieved transfer objects
  [
    //Same internal structure as "requested" 
  ]

}
```

**The following deal with transfers**

### transfer/create (POST)

Creates a transfer.
+ accountid: account id money is being transfered from
+ destUsername: destination user
+ amount: amount of money in cents

### transfer/cancel (POST)

Cancels a transfer.
+ transferid: id of canceled transfer

### transfer/accept (POST)

Accept a transfer.
+ transferid: id of accepted transfer
