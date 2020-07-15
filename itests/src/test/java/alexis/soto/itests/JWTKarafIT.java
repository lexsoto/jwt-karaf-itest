package alexis.soto.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.stream.Stream;

import org.apache.karaf.itests.KarafTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JWTKarafIT extends KarafTestSupport {
	private static final Logger log = LoggerFactory.getLogger(JWTKarafIT.class);

	@Override
	@Configuration
	public Option[] config() {
		String karafVersion = System.getProperty("karaf.version");
		log.info("Start builing config options. karaf.version={}", karafVersion);

		assertNotNull(karafVersion);

		Option[] options = new Option[] {
				junitBundles(),
				features(maven()
						.groupId("org.apache.karaf.features")
						.artifactId("standard")
						.type("xml")
						.classifier("features")
						.versionAsInProject(), "scr", "war"),
				// JWT
				bundle(maven()
						.groupId("alexis.soto.itest")
						.artifactId("jwt-karaf-client")
						.versionAsInProject()
						.getURL()),
				bundle(maven()
						.groupId("io.jsonwebtoken")
						.artifactId("jjwt-api")
						.versionAsInProject()
						.getURL()),
				bundle(maven()
						.groupId("io.jsonwebtoken")
						.artifactId("jjwt-impl")
						.versionAsInProject()
						.getURL()),
				bundle(maven()
						.groupId("io.jsonwebtoken")
						.artifactId("jjwt-jackson")
						.versionAsInProject()
						.getURL()),
				// Jackson
				bundle(maven()
						.groupId("com.fasterxml.jackson.core")
						.artifactId("jackson-core")
						.versionAsInProject()
						.getURL()),
				bundle(maven()
						.groupId("com.fasterxml.jackson.core")
						.artifactId("jackson-databind")
						.versionAsInProject()
						.getURL()),
				bundle(maven()
						.groupId("com.fasterxml.jackson.core")
						.artifactId("jackson-annotations")
						.versionAsInProject()
						.getURL()),
		};
		return combineOptions(super.config(), options);
	}

	protected Option[] combineOptions(Option[]... options) {
		return Stream.of(options).flatMap(Stream::of).toArray(Option[]::new);
	}

	@Test
	public void happyPath() throws Exception {

		HttpClient client = HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.followRedirects(Redirect.NORMAL)
				.connectTimeout(Duration.ofSeconds(20))
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + getHttpPort() + "/test"))
				.build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		log.info("Status: {}", response.statusCode());
		log.info("Response: {}", response.body());

		assertEquals(200, response.statusCode());
	}
}
