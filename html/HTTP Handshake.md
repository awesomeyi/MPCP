HTTP Handshake
========
Handshake procedures. All requests to `/API/kap`. You are required to send parameters `step` and `data` in JSON format. The server will reply in the same way, with a JSON object containing `step` and `data`. It will reply with a generic "Error" as `data` if an invalid operation occurs.
### Initialize handshake

Client:
+ step: 1
+ data: (name of protocol)

Server:
+ step: 1
+ data: sessionid

**`sessionid` now must be attached as a get parameter "sessionid".

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

Session communication. Sent to `/API/session`.

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
