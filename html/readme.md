HTTP API
========
Handshake procedures. All requests to `/API/kap`. You are required to send parameters `step` and `data` in JSON format. The server will reply in the same way, with a JSON object containing `step` and `data`. It will reply with a generic "Error" as `data` if an invalid operation occurs.

### Standard scheme for sending encrypted data

All symmetric encryption is done through AES-CBC.

Data sent and recieved should be a JSON object in this format. Both `iv` and `encrypted` should be encoded through base-64.
```
{
	iv: (initialization vector)
	encrypted: (encrypted message with AES_CBC)
}
```

### Initialize handshake

Client:
+ step: 1
+ data: (name of protocol)

Server:
+ step: 1
+ data: sessionid

**`sessionid` now must be attached as a get parameter "sessionid".**

Execute chosen protocol. All choices listed:
### test (Testing protocol- INSECURE)

Client will send a secret. Server will send back the SHA-265 of secret, shortened to 16 bytes, base64-ed.
+ step: 2
+ data:
```
{
	secret: //your secret
}
```

Server:
+ step: 2
+ data: (shared session key)


### DHE (Diffie Hellman Exchange)

Client (r1) and server (r2) both compute 2 random numbers.

Client:
+ step: 2
+ data: `{ g, g^r1, p }`

Server:
+ step: 2
+ data: `{g^r2}`

Client computes `g^r1^r2`, server computes `g^r1^r2`. Both values are hashed through `SHA-256` and the first 16 bytes are used as the symmetric key.

### End handshake

Client:
+ step: (1 more then last step)
+ data: ("Confirm" AES encrypted with shared key, use the standard scheme)

Server:
+ step: (current step)
+ data: Success/Failure

### Secure session communication

Session communication. Sent to `/API/session`.

Client:
+ sessionid: (Unencrypted `sessionid`)
+ data: send the following object using the standard scheme
```
{
    authcode: (your authentication code)
    action: (same as HTTPS API: /API/action)
    parameters: `[ ]` (array of key pairs of forwarded parameters) 
}
```

Server:
+ status: Success/Failure
+ data: return data or error message, data returned is in the standard scheme

### End secure session

Client:
+ sessionid: (Unencrypted `sessionid`)
+ data: ("Terminate" encrypted with AES, sent using the standard scheme)

Server:
+ status: (Success/Failure)
