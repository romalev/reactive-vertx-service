# Fully reactive service #

This is a simple prototype of fully reactive (non-blocking) service which is built based on using vert.x, rxjava, spring boot, mongo db.

Notes: 
* MongoDB 3.4 is used as a persistent storage for given service.
* Mongo client is based on the MongoDB Async Driver.
* Service is fully written on top of vert.x core + web and handles each incoming request in a reactive manner. 
* Spring boot is responsible for deploying verticles and managing them.
* Don't event look at the UI code. I am not an UI guy so UI was sort of taken from one of the examples published on Vert.x portal.  

Application itself has a really simple concept. You, as the user, can **CRUD** entities.


 To play with service itself perform the following steps:
 1) Install MongoDB : https://www.mongodb.com/download-center?jmp=nav#community. MongoDB server by default runs on port 27017.
 2) `mvn clean install` 
 3) `mvn spring-boot:run`
 4) Hit the `http://localhost:9092/assets/index.html` and you will walk into the reactive world :)
 
 
 P.S. Is there anyone interested with playing with that ? Feel free to fork it :) 
 There's some stuff I'd like to get done : 
 1) Integrate Swagger with Vert.x web.
 2) Adding unit tests.