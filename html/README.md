API Usage Guide
========
API Usage guide. Requests take the following form `/API/action`.

### General form

On every request, the server will return a JSON Object with the following properties:
+ status: Success/Failure
+ message: On failure, return error message. On success, return requested data.

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
//Array of account objects
[ 
  //Account object has the following properties
  { 
    accountid
    bankname
    balance
    name
  }
  ...
]
```

### transfers (GET)

Returns all transfers in the following form:

```
{
  //List of requested transfers objects
  requested:
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
    },
    ...
  ],
  
  //List of recieved transfer objects
  recieved:
  [
    //Same internal structure as "requested" 
  ]

}
```

**The following deal with specific transfer actions.**

### transfer/create (POST)

Creates a transfer. Allows you to transfer to a phone number or username. Specify one destination parameter.
+ accountid: account id money is being transfered from
+ destUsername: destination user
+ destNumber: destination number
+ amount: amount of money in cents

### transfer/cancel (POST)

Cancels a transfer.
+ transferid: id of canceled transfer

### transfer/accept (POST)

Accept a transfer.
+ transferid: id of accepted transfer

**Carrier specific commands. Requests take the form `/API/carrier/action`.**

### phones (GET)

Get all phones in the following form:

```
//Array of phone objects
[
  //Individual object properties
  {
    phoneid
    cellnumber
  }
]
```

### phone/add (POST)

Add a phone.
+ number: string-9 digit phone number

### phones/delete (POST)

Delete a phone.
+ cellid: ID of cellphone wished to be deleted
