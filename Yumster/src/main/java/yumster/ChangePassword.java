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
@WebServlet("/api/v1/user/change-password")
public class ChangePassword extends HttpServlet {
	Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChangePassword() {
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
		
		String newPassword = request.getParameter("password");
		// hash the password!
		String hashedNewPassword = encoder.encode(newPassword);
		
		// placeholder until Henry makes the token dependency
		//
		User user = new User(); // TODO: UPDATE user's PasswordHash WHERE token = (user's token)
		boolean result = user.updatePassword(hashedNewPassword); 
		
		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to Add User");
		}
		response.getWriter().print(res.toJson());
	}
	
}