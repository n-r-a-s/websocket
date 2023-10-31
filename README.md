# WebSocket Example for Apache Tomcat

This contribution may be useful for any one who likes tho implement a simple WebSocket Server/Client for the use with Apache Tomcat for education purposes or further development.

Implements server/client communication over the WebSocket protocol for the integration into the Apache Tomcat Web Application Framework. The following features are demonstrated (full-duplex,bidirectional):
- Message created in one session seen on all other sessions
- Message removal on all sessions
- Message added and removed for specific sessions

This is an adaption from the Oracle tutorial with the following modifications:
- Edited for deployment on Apache Tomcat instead of Glassfish
- Generalized message model
- Message propagation for specific session
Java EE 7: Building Web Applications with WebSocket, JavaScript and HTML5 
https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/HomeWebsocket/WebsocketHome.html

Niklaus Aeschbacher (com.nras.*)
