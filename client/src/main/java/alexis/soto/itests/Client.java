package alexis.soto.itests;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 *
 */
@Component(scope = ServiceScope.PROTOTYPE,
		service = {Client.class, HttpServlet.class},
		property = {
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/test"
		})
public class Client extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SecretKey secretKey;


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Instant now = Instant.now();
		Date from = Date.from(now);
		Date to = Date.from(now.plus(Duration.ofMinutes(30)));

		String token = Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.compressWith(CompressionCodecs.DEFLATE)
				.setSubject("Tester")
				.setIssuedAt(from)
				.setExpiration(to)
				.signWith(secretKey)
				.compact();
		try (OutputStream outStream = resp.getOutputStream()) {
			PrintWriter pw = new PrintWriter(outStream);
			pw.append(token);
		}
	}
}
