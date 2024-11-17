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

import yumster.dao.UserDaoImpl;
import yumster.dao.UserTokenDaoImpl;
import yumster.helper.Response;
import yumster.obj.User;
import yumster.obj.UserToken;

/**
 * Servlet implementation class Register
 */
@WebServlet("/api/v1/change-username")
@MultipartConfig
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
		UserDaoImpl userDao = new UserDaoImpl();
		UserTokenDaoImpl userTokenDao = new UserTokenDaoImpl();
		
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated");
			response.getWriter().print(res.toJson());
			return;
		}
		User user = null;
		for (int i = 0; i < cookies.length; i++) {
			String name = cookies[i].getName();
			String value = cookies[i].getValue();

			if (name == "token") {
				UserToken userToken = userTokenDao.getByToken(value);
				if (userToken != null) {
					user = userDao.getById(userToken.getUserId());
				} 
			}
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			Response res = new Response("error", "Unauthenticated");
			response.getWriter().print(res.toJson());
			return;
		}
		
		String newUsername = request.getParameter("uname").trim();
		if (StringUtils.isEmpty(newUsername)) {
			Response res = new Response("error", "Required Field not filled.");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().print(res.toJson());
			return;
		}
	
		if (userDao.checkExists(newUsername)) {
			Response res = new Response("error", "Username already taken.");
			response.getWriter().print(res.toJson());
			return;
		}
		
		boolean result = userDao.updateUsername(newUsername, user);
		
		Response res = new Response();
		if (!result) {
			res.setStatus("error");
			res.setDescription("Failed to change username");
		}
		response.getWriter().print(res.toJson());
	}
	
}