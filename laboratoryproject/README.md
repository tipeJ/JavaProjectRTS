# TCP/UDP communication demo project for RTS

## Running

### UDPClient
On command line, run "java UDPclient.java <URL> <PORT>" with the proper URL and PORT arguments for the server

### UDPServer
On command line, run "java UDPServer.java <PORT>

### TCPClient
On command line, run "java TCPClient.java <URL> <PORT> with the wanted server URL and port

### TCPServer
On command line, run "java TCPServer.java". You can specify port with a port parameter i.e. "java TCPServer.java 9090", the default port is 8080

### TCPMultiServer
On command line, run "java TCPMultiServer.java". You can specify port with a port parameter i.e. "java TCPMultiServer.java 9090", the default port is 8080


## Testing
The tests are located in the src/test/java/fr/ensea/rts folder

Note that unfinished tests may leave processes hanging and prevent testing. To prevent this, either reboot or manually shut down the test processes.