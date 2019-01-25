# Ping Pong Distributed Mutex
This project is an implementation of token-based distributed mutual exclusion
algorithm proposed by Jayadev Misra. The project currently contains a POC of solution
(critical section and variable sharing are simulated). The aim is to convert current
implementation to a ready-to-use library which you can use in your project.

## Motivation
In 1983 Jayadev Misra gave a lecture entitled "Detecting Termination of Distributed
Computations Using Markers" where proposed simple modification of token-based
mutual exclusion algorithm. Initially, there was only one token allowing a node to
enter a critical section, so lose of this token causes global system freeze.
Misra showed, that adding the second token to the ring allows losing one token
in a round (because this situation will be eventually detected and missing token
will be regenerated).

This is my graduation project from the subject _High Availability Systems (Systemy
Wysokiej Niezawodności)_ conducted at Poznan University of Technology
(Computer Science, specialization: Distributed Systems).

## Tools used
Application uses the following libraries:
- [JeroMQ](https://github.com/zeromq/jeromq) - pure Java ZeroMQ implementation
- [LogBack](https://logback.qos.ch) - nice looking logs
- [Apache Commons Lang 3](http://commons.apache.org/proper/commons-lang/) - some helpful functions
- [Gradle](https://gradle.org/) - for build task management

## Build and Run
To build application invoke `gradle build`. This will prepare nice distribution
packages on `build/distributions` directory.

To run this app directly from gradle invoke `gradle run --args 'app args'` task.
Where _app args_ should be replaced by following arguments:
```
Usage: mutexmisra previous_node publish_port node_id total_nodes [interactive]
    previous_node - IP and port of previous node
    publish_port  - a port where app will publish messages
    node_id       - numeric ID of this node
      Notice: The node with ID=1 is primary and have to be run as last!
    total_nodes   - total amount of nodes in the ring
    interactive   - ask user to lose message in round

EXAMPLE: mutexmisra 192.168.1.10:5555 5555 1 4 interactive
```

## Contribute
If you would like to fix something or implement any feature, feel free to fork
this repository and raise a pull request. You can also raise an issue if you want.
All commitment is very welcome.

## License
This software is licensed under Apache License. Refer to LICENSE.md file for
more information.
