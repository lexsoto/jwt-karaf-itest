package alexis.soto.itests;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.maven;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.SecretKey;

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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

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
		Option[] result = combineOptions(super.config(), options);

		log.info("Finished builing config options: " +
				Stream.of(result).map((o -> o.toString())).collect(Collectors.toList()));

		return result;
	}

	protected Option[] combineOptions(Option[]... options) {
		return Stream.of(options).flatMap(Stream::of).toArray(Option[]::new);
	}

	@Test
	public void happyPath() {

		Instant now = Instant.now();
		Date from = Date.from(now);
		Date to = Date.from(now.plus(Duration.ofMinutes(30)));
		SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

		String token = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.compressWith(CompressionCodecs.DEFLATE)
				.setSubject("Tester")
				.setIssuedAt(from)
				.setExpiration(to)
				.signWith(secretKey)
				.compact();

		log.info("Generated token: {}", token);

		Jws<Claims> parsed = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.setAllowedClockSkewSeconds(5)
				.build()
				.parseClaimsJws(token);

		log.info("Parsed token: {}", parsed);

	}
}
