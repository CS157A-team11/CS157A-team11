package yumster.endpoints;

import java.io.IOException;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/login")
@MultipartConfig

public class Login extends HttpServlet {
	Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	Encoder b64encoder = Base64.getEncoder();
    SecureRandom random = new SecureRandom();
	private static final long serialVersionUID = 1L;
	
	// https://emailregex.com/
    Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		UserDaoImpl userDao = new UserDaoImpl();
		UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();

		String usernameEmail = request.getParameter("username_email").trim();
		String password = request.getParameter("password").trim();
		
		if (StringUtils.isEmpty(usernameEmail) || StringUtils.isEmpty(password)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		User user = null;
		if (usernameEmail.indexOf('@') == -1) {
			// Handle as username
			user = userDao.getByUsername(usernameEmail);
		} else {
			// Handle as email
			user = userDao.getByEmail(usernameEmail);
		}

		if (user == null) {
			encoder.encode(password); // prevent timing attack by hashing input
			Response res = new Response("error", "Your input does not match our records.");
			response.getWriter().print(res.toJson());
			return;
		}
		if (!encoder.matches(password, user.getPasswordHash())) {
			Response res = new Response("error", "Your input does not match our records.");
			response.getWriter().print(res.toJson());
			return;
		}
		// authenticated 
		
		// generate token
		byte[] arr = new byte[36]; // 36 * 8 = 288 / 6 = 48 b64 chars
		random.nextBytes(arr);
		String token = b64encoder.encodeToString(arr);
		
		// set cookie
		Cookie cookie = new Cookie("token", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(86400); // 60*60*24 = 1 day
		response.addCookie(cookie);
		
		// store token, expiration 1 day
		userTokenDao.insert(user.getId(), token, (System.currentTimeMillis() / 1000L) + 86400);
		
		Response res = new Response();
		response.getWriter().print(res.toJson());
		return;
	}
}