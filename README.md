# jwt-karaf-itest

Integration tests that runs JWT in Karaf

Run with:

  mvn clean install verify

Reproduces error ServiceLoader unable to find Serializer: 

java.util.ServiceConfigurationError: io.jsonwebtoken.io.Serializer: Provider io.jsonwebtoken.jackson.io.JacksonSerializer not found
	at java.util.ServiceLoader.fail(ServiceLoader.java:588) ~[?:?]
	at java.util.ServiceLoader$LazyClassPathLookupIterator.nextProviderClass(ServiceLoader.java:1211) ~[?:?]
	at java.util.ServiceLoader$LazyClassPathLookupIterator.hasNextService(ServiceLoader.java:1220) ~[?:?]
	at java.util.ServiceLoader$LazyClassPathLookupIterator.hasNext(ServiceLoader.java:1264) ~[?:?]
	at java.util.ServiceLoader$2.hasNext(ServiceLoader.java:1299) ~[?:?]
	at java.util.ServiceLoader$3.hasNext(ServiceLoader.java:1384) ~[?:?]
	at io.jsonwebtoken.impl.lang.Services.loadFirst(Services.java:110) ~[?:?]
	at io.jsonwebtoken.impl.lang.Services.loadFirst(Services.java:100) ~[?:?]
	at io.jsonwebtoken.impl.lang.LegacyServices.loadFirst(LegacyServices.java:21) ~[?:?]
	at io.jsonwebtoken.impl.DefaultJwtBuilder.compact(DefaultJwtBuilder.java:300) ~[?:?]
	
  