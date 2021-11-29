# knock_knock

This is a simple knock-knock game played between client and server. All you have to do is start the server and run the client program and the game will proceed.

### Syntax to run the server:
First complile the code and then run the following code: 
```
java KnockKnockServer -port <port_number>
```

### Syntax to run the client:
First compile the code and then run the following code:
```
java KnockKnockClient -host <hostname> -port <port_number>
```

**Note:** `hostname` can be `localhost` or the `ip_address` of the client. Make sure that the `port_number` is the same for the server and client.
