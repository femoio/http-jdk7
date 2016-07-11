# FeMo.IO HTTP Library

This library offers support for platforms that only support *Java 7* (Android, ....)

[![Build Status](https://travis-ci.org/femoio/http-jdk7.svg?branch=master)](https://travis-ci.org/femoio/http)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.femo/http-jdk7/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.femo/http)

        <dependency>
            <groupId>io.femo</groupId>
            <artifactId>http-jdk7</artifactId>
            <version>0.0.2</version>
        </dependency>

This library provides a simple API for developers to perform synchronous and asynchronous HTTP Requests.
 
HTTP Versions supported:

* HTTP/1.1

## Issues

Since this version is only a backport some tests might fail that work for the normal version.

* MIME Detection makes mistakes when being used with jvm 7

## GET Requests

To perform a simple HTTP GET use the following call.

        HttpResponse response = Http.get("http://example.org/").response();
        
The request will be executed and the result cached in the HttpResponse object. All data is now exposed via simple jQuery
like getters and setters. To check the response status use
 
        if(response.statusCode() == StatusCode.OK) {
            //Response was successfull
        } else {
            //Response was not successfull
        }
        
To retrieve the content of the response use
 
        System.out.println(response.responseString());
        
## POST Request

To perform a simple HTTP POST use the following call.

        Http.post("http://example.org/post").response();
        
To append data use

        Http
            .post("http://example.org/post")
            .data("test", "test")
            .response();
            
The data is automatically UrlFormEncoded and sent to the server.

## Drivers

### Use with Android

        Http.installDriver(new AndroidDriver());
        
### Asynchronous Use
This driver spawns one new Thread to execute each request. Use this only for projects with few requests as it generates heavy load.

        Http.installDriver(new AsynchronousDriver()); 
        
### Asynchronous Batch Use
This driver creates a Thread Executor Service to execute requests in the Background. Use this driver for projects that 
perform a huge amount of requests.

        Http.installDriver(new AsynchronousDriver(5));
        
You have to supply the constructor with the amount of executor threads you want to spawn at the start of the program.

## HTTP Server

To start a simple HTTP Server on any port simply call the *server(port:int)* of the Http class and start the server
  
        Http.server(8080).start();
        
This will return an object of type HttpServer and will start an HTTP server on port 8080 that 404s every request.

To provide some content, you can use any of the *use* methods offered by HttpServer. For simplicity standard HTTP methods have predefined methods to use.

        server.get('/', (request, response) -> {response.entity("Hello World); return true}) //This print Hello World to any clients requesting GET /
        
        server.post('/', (req, res) -> {res.entity(req.requestBytes()); return true;}); //This echos everything a client sends to POST /
        
To provide funtionality for a whole path, or for all requests middleware can be used. To log every request code like this could be used:

        server.use((HttpMiddleware) (req, res) -> System.out.println(req.method() + " " + req.path()));
        
If you also want to log information that is available after a request has been handled, use the HttpServer.after() method.

        server.after((request, response) -> {
                  System.out.printf("%03d %-4s %s - %s\n", response.statusCode(), request.method(), request.path(),
                          response.hasHeader("Content-Length") ? response.header("Content-Length").value() : " -- ");
              })

To stop a running instance use the *stop()* method.

        server.stop()
        
The server will stop all it's listeners and threads after all pending requests have been handled.