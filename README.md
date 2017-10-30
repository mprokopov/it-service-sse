# it-service-sse

This microservice is a part of the IT-Service Adminka to deliver ticket and other notifications for 
the javascript fronted via Server Sent Events technology. User connects with his own ID and listens only for 
those messages, which are only for his eyes. Service also counts number of concurrent user connections.

## Configuration

This microservice uses 9292 port by default. Via docker and jwilder/nginx-proxy it exposes itself via HTTPS and proper domain name.

### Logging

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).

## Build

### [Docker](https://www.docker.com/) container support

1. Build an uberjar of your service: `lein uberjar`
2. Build a Docker image: `sudo docker build -t it-service-sse .`
3. Run your Docker image: `docker run -p 9292:9292 -e REDIS_HOST=redis --link itservice_redis_1 it-service-sse`


## What's interesting here?

- it uses [Carmine library](https://github.com/ptaoussanis/carmine) to connect to Redis
- it uses [Pedestal library](https://github.com/pedestal/pedestal) to serve SSE connections
- it uses [Environ library](https://github.com/weavejester/environ) to get redis settings from Docker (and not only Docker) environment
- it uses core.async to listen Redis message and deliver to SSE channels
- [JSON CORS](https://www.eriwen.com/javascript/how-to-cors/) is also configured here

