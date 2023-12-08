## MightyAnt

A simple Kotlin/http4k based websocket proxy.

### Config (resources/config.yaml)

```
# port of the server listening for proxy requests
proxyPort: 9000 

# name it identifies itself with upon connection
proxyName: "MightyAnt" 

# should it be silent or identify itself upon connection?
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

Once connected to socket, how to structure your message for proxying depends on how you have arbitraryMode configuration set.

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
