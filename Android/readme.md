Android Web API
========
Interacting with the server API.

### HTTP API:

Handshake procedures and session communication with the symmetric key.

Classes:
+ HTTPAPI: send JSON Post requests
+ CryptoAPI: encrypt and decrypt in AES-CBC mode
+ BaseProtocol: abstract class representing all handshake protocols
+ TestProtocol: handshake with the "test" protocol

#### Protocol use:

All protocols should of course be executed asynchroniously.
+ `Protocol.execute() -> Retval`: executes the protocol, returns a `Retval` object
+ `Protocol.getCallable() -> Callable<Retval>`: gets a `Callable` object that executes the protocol through the `.call()` function, useful for async.

The `RetVal` object:
+ `getSessionId() -> String`: returns the session id
+ `getSymkey() -> String`: gets the base 64 encoded symmetric key