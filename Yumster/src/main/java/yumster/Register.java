package yumster;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import yumster.dao.UserDaoImpl;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/register")
@MultipartConfig
public class Register extends HttpServlet {
	Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
	private static final long serialVersionUID = 1L;
	
	// https://emailregex.com/
    Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
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
		
		String uname = request.getParameter("username");
		String email = request.getParameter("email");
		
		// Check if username has an @ character, disallow
		if (uname.indexOf('@') != -1) {
			Response res = new Response("error", "We do not allow @ in username.");
			response.getWriter().print(res.toJson());
			return;
		}
		
		// Check if email given matches the email regex.
	    Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			Response res = new Response("error", "Email does not look like an email.");
			response.getWriter().print(res.toJson());
			return;
		}
		
		// Check that the username nor email is not already taken
		if (userDao.checkExists(uname, email)) {
			Response res = new Response("error", "Username or Email already taken.");
			response.getWriter().print(res.toJson());
			return;
		}
			
		// Check that the password is long enough
		String password = request.getParameter("password");
		if (password.length() < 8) {
			Response res = new Response("error", "Password must be at least 8 characters long.");
			response.getWriter().print(res.toJson());
			return;
		}

		String cname = request.getParameter("common_name");
		
		// Hash the password
		String pwHash = encoder.encode(password);
		// Create user and store into database
		User user = new User(uname, cname, email, pwHash);
		boolean result = userDao.insert(user);
		
		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to Add User");
		}
		response.getWriter().print(res.toJson());
	}
	
}