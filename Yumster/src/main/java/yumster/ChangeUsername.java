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
@WebServlet("/api/v1/change-username")
public class ChangeUsername extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChangeUsername() {
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
		
		String newUsername = request.getParameter("uname");
	
		if (User.checkExists(newUsername)) {
			Response res = new Response("error", "Username already taken.");
			response.getWriter().print(res.toJson());
			return;
		}
		
		// placeholder until Henry makes the token dependency
		//
		User user = new User(); // TODO: UPDATE the user's username WHERE token = (user's token)
		boolean result = user.updateUsername(newUsername);
		
		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to change username");
		}
		response.getWriter().print(res.toJson());
	}
	
}