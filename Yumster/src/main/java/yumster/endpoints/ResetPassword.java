package yumster.endpoints;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import yumster.dao.EmailVerificationDaoImpl;
import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.EmailVerification;
import yumster.obj.User;
import yumster.obj.UserToken;

/**
 * Servlet implementation class Reset Password
 */
@WebServlet("/api/v1/user/reset-password")
@MultipartConfig
public class ResetPassword extends HttpServlet {
	Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ResetPassword() {
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
		EmailVerificationDaoImpl emailVerificationDao = new EmailVerificationDaoImpl();

		String token = request.getParameter("token").trim();
		String password = request.getParameter("password");

		
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
		
		EmailVerification emailVerification = emailVerificationDao.getByToken(token);
		
		if (emailVerification == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated");
			response.getWriter().print(res.toJson());
			return;
		}
		
		// Token is valid.
		
		User user = userDao.getById(emailVerification.getUserId());
		
		// Check that the password is long enough
		if (password.length() < 8) {
			Response res = new Response("error", "Password must be at least 8 characters long.");
			response.getWriter().print(res.toJson());
			return;
		}
		// hash the password!
		String hashedNewPassword = encoder.encode(password);

		boolean result = userDao.updatePassword(hashedNewPassword, user);
		
		// delete the token, single use only
		emailVerificationDao.deleteToken(token);

		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to Add User");
		}
		response.getWriter().print(res.toJson());
	}

}