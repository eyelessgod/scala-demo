# scala-demo
Simple server with Akka-Http  
starts with
```sh
gradlew run
```
#### API  
Search hosts by ip/name
```sh
curl "http://localhost:8080/host?search=loc" 
```
Add host
```sh
curl "http://localhost:8080/host/add?name=gate&ip=192.168.0.1" --data ""
```
