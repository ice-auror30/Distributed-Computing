# Distributed-Computing
Contains the projects for distributed computing class.

## Steps to run project 1

(The following steps were followed after loggin into the remote linux servers)
1. rpcgen -k date.x
2. gcc -o client client.c date_clnt.c
3. gcc -o server server.c date_svc.c
4. ./server
5. ./client localhost
6. kill server-pid


## Steps to run project 2

In the terminal:
1. idlj -fall idlfilename.idl (This generates idlfilenamePOA.java: server skeleton, _idlfilenameStub.java: client stub, 
idlfilenameOpeations.java: contains teh method sayidlfilename(),
idlfilename.java, idlfilenameHelper.java, idlfilenameHolder.java)

2. Write the server and client codes

3. javac *.java idlfilename/*.java
4. start orbd from UNIx
orbd -ORBInitialPort 1050&
5. java idlfilenameServer -ORBInitialPort 1050 -ORBInitialHost localhost&
6. java idlfilenameClient -ORBInitialPort 1050 -ORBInitialHost localhost


## Steps to run project 3

Run separate instances of the server file and the client files.
