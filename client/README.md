# Overview

Client's library for `EPMD` server.

## Usage

### Add dependency

Include the dependency to your project's pom.xml file:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>io.appulse.epmd.java</groupId>
        <artifactId>client</artifactId>
        <version>1.0.1</version>
    </dependency>
    ...
</dependencies>
```

or Gradle:

```groovy
compile 'io.appulse.epmd.java:client:1.0.1'
```

### Create client

Create `EpmdClient` with default port **4369** or extracted from `ERL_EPMD_PORT` environment variable:

```java
EpmdClient client = new EmpdClient();
```

You are able to specify `EPMD` server port manually:

```java
EpmdClient client = new EmpdClient(9999);
```

### Server interactions

You can use different client's methods:

```java
// register a new Node in EPMD server
int creation = client.register("popa", 9988, R6_ERLANG, TCP, R4, R6);

// lookup node on EPMD server (local/remote)
Optional<NodeInfo> node = client.lookup("remote-node@192.168.1.46");

// get all nodes from EPMD server (local/remote)
List<EpmdInfo.NodeDescription> nodes = client.getNodes();

// dumps all nodes from EPMD server
List<EpmdDump.NodeDump> nodes = client.dumpAll();

// kill EPMD server (if available)
boolean wasKilled = client.kill();

// close client connection
client.close();
```

For more information see `JavaDoc` and source code.
