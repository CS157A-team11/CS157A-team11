package yumster;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

	private static final long serialVersionUID = 1L;

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
		
		String uname = request.getParameter("uname");
		String email = request.getParameter("email");

		if (User.checkExists(uname, email)) {
			Response res = new Response("error", "Username or Email already taken.");
			response.getWriter().print(res.toJson());
			return;
		}
			
			
		String cname = request.getParameter("cname");
		String password = request.getParameter("password");
		if (password.length() < 8) {
			Response res = new Response("error", "Password must be at least 8 characters long.");
			response.getWriter().print(res.toJson());
			return;
		}
		
		String pwHash = encoder.encode(password);
		User user = new User(uname, cname, email, pwHash);
		boolean result = User.insert(user);
		
		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to Add User");
		}
		response.getWriter().print(res.toJson());
	}
	
}