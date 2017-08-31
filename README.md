# it-service-sse

This microservice is a part of the IT-Service Adminka
listens broadcasted pmessage from Redis and streams as SSE channel

## Getting Started

1. Start the application: `lein run-dev` for dev mode

## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).


## Developing your service

1. Start a new REPL: `lein repl`
2. Start your service in dev-mode: `(def dev-serv (run-dev))`
3. Connect your editor to the running REPL session.
   Re-evaluated code will be seen immediately in the service.

### [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
2. Build a Docker image: `sudo docker build -t it-service-sse .`
3. Run your Docker image: `docker run -p 9292:9292 -e REDIS_HOST=redis --link itservice_redis_1 it-service-sse`

### [OSv](http://osv.io/) unikernel support with [Capstan](http://osv.io/capstan/)

1. Build and run your image: `capstan run -f "8080:8080"`

Once the image it built, it's cached.  To delete the image and build a new one:

1. `capstan rmi it-service-sse; capstan build`


## Links
* [Other examples](https://github.com/pedestal/samples)

