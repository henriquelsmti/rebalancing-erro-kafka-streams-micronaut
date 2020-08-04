This project is used to simulate NullPonterException when kafka streams is running on docker 

Build and run:

1. gradle dockerBuild
2. docker-compose up

An error will happen in stream1 or stream2

Update:
Apparently, the problem happens only in the openj9 JRE
