# Overview

`EPMD` server - library and `CLI` parts in one jar.

## Usage

### As application

Run it as a regular `Java` CLI app:

```bash
$> java -jar epmd-2.0.0.jar
2019-06-06 00:10:18.869  INFO : Starting server on port 4369

```

To get names of all registered nodes:

```bash
$> java -jar epmd-2.0.0.jar -names
popa
echo
```

To see another options and commands, just type:

```bash
$> java -jar epmd-2.0.0.jar --help
usage: java -jar epmd.jar [-d|-debug] [DbgExtra...] [-address List]
                          [-port No] [-daemon] [-relaxed_command_check]
       java -jar epmd.jar [-d|-debug] [-port No] [-names|-kill|-stop name]

Regular options
    -address List
        Let epmd listen only on the comma-separated list of IP
        addresses (and on the loopback interface).
    -port No
        Let epmd listen to another port than default 4369
    -d
    -debug
        Enable debugging. This will give a log to
        the standard error stream. It will shorten
        the number of saved used node names to 5.

        If you give more than one debug flag you may
        get more debugging information.
    -relaxed_command_check
        Allow this instance of epmd to be killed with
        epmd -kill even if there are registered nodes.
        Also allows forced unregister (epmd -stop).

DbgExtra options
    -packet_timeout Seconds
        Set the number of seconds a connection can be
        inactive before epmd times out and closes the
        connection (default 60).

    -delay_accept Seconds
        To simulate a busy server you can insert a
        delay between epmd gets notified about that
        a new connection is requested and when the
        connections gets accepted.

    -delay_write Seconds
        Also a simulation of a busy server. Inserts
        a delay before a reply is sent.

Interactive options
    -names
        List names registered with the currently running epmd
    -kill
        Kill the currently running epmd
        (only allowed if -names show empty database or
        -relaxed_command_check was given when epmd was started).
    -stop Name
        Forcibly unregisters a name with epmd
        (only allowed if -relaxed_command_check was given when
        epmd was started).

```

### As library

**Maven**:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>io.appulse.epmd.java</groupId>
        <artifactId>server</artifactId>
        <version>2.0.0</version>
    </dependency>
    ...
</dependencies>
```

**Gradle**:

```groovy
compile 'io.appulse.epmd.java:server:2.0.0'
```

To start EPMD server in your code:

```java
// ...
CommonOptions commonOptions = new CommonOptions();
commonOptions.setPort(4369);

ServerCommandExecutor server = new ServerCommandExecutor(commonOptions, new ServerCommandOptions());

server.execute(); // it starts Netty server and blocks current thread
// ...
```
