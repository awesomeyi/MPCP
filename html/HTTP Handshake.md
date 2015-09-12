HTTP Handshake
========
Handshake procedures. All requests to `/API/kap`.
### Initialize handshake

Client:
+ step: 1
+ data: (name of protocol)

Server:
+ step: 1
+ data: valid/invalid

Execute chosen protocol. All choices listed:
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
+ data: ("Confirm" AES encrypted with shared key)

Server:
+ step: (current step)
+ data: (unencrypted `sessionid`)

### Secure session communication

Client:
+ sessionid: (Unencrypted `sessionid`)
+ data: JSON object encrypted with AES
```
{
    authcode: (your authentication code)
    action: (same as HTTPS API: /API/action)
    parameters: `[ ]` (array of key pairs of forwarded parameters) 
}
```

### End secure session

Client:
+ sessionid: (Unencrypted `sessionid`)
+ data: ("Terminate" encrypted with AES)

Server:
+ status: (Success/Failure)