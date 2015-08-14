API Usage
========
API Usage guide
### register.php (POST)
+ username: Desired username
+ password: Desired password

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message

### login.php (POST)
+ username: Desired username
+ password: Desired password

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message
+ authcode: Returns an authentication code on success

**From now on, ALL requests must have `authcode` attached as a URL parameter**

### logout.php (GET)

Returns a JSON Object:
+ status: Success/Failure
+ message: Error message

### verify.php (GET)

Verifies authenticity
Returns a JSON Object:
+ status: Success/Failure