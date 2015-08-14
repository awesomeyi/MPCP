API Usage
========
API Usage guide
### register (POST)
+ username: Desired username
+ password: Desired password

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message

### login (POST)
+ username: Desired username
+ password: Desired password

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message
+ authcode: Returns an authentication code on success

**From now on, ALL requests must have `authcode` attached as a URL parameter**

### logout (GET)

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message

### verify (GET)

Verifies authenticity, returns a JSON Object:
+ status: Success/Failure
+ message: Error message

### username (GET)

Returns a JSON Object:
+ status: Success/Failure
+ message: Username/Error
