## MightyAnt

A simple Kotlin/http4k based websocket proxy.

### Requirements

JDK 21+ (developed with Azul Zulu: 23.32.11 ARM 64 bit)

### Config (resources/config.yaml)

```
# port for listening for proxy requests
proxyPort: 9000 

# name presented upon connection
proxyName: "MightyAnt" 

# should it be silent or present name upon connection?
silent: false 

# useCompression (gzip)
useCompression: false 

# useBase64
useBase64: false 

# false = hardcoded url, true = url is supplied via json message
arbitraryMode: false 

# websocket url to proxy to (ignored in arbitrary mode)
destinationUrl: "ws://localhost:7300/"

# websocket urls to mirror proxing to (ignored in arbitrary mode)
mirrorUrls:
  - "ws://localhost:7301"
  - "ws://localhost:7302"
  - "ws://localhost:7303"
```

### Message Format

Once you are connected to the socket, you can send messages.

The details of how to structure your message for proxying depends on what arbitraryModel is set to.

A. If arbitraryMode = false, your websocket message is simply the content you want to proxy.

B. If arbitraryMode = true, your websocket message should look like this:

```
{
    "url": "ws://theurltoproxyto.com",
    "content: "The websocket message I want to proxy"
}
```

### TODO

* organize config by nesty option-y stuff (debatable)
* implement some form of authentication in arbitraryMode

## Tech

Kotlin, IntelliJ, Gradle

<p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=kotlin,idea,gradle" />
  </a>
</p>