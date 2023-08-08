package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

import static it.polimi.tiw.utils.Utils.tryParse;

@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public Login() {
		super();
	}

	public void init() throws ServletException {
		ServletContext ctx = getServletContext();
		connection = ConnectionHandler.getConnection(ctx);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(ctx);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// obtain and escape params
		String login = null;
		String pwd = null;

		login = StringEscapeUtils.escapeJava(request.getParameter("login"));
		pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));

		if (login == null || pwd == null || login.isBlank() || pwd.isBlank()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}


		User user = null;

		// query db to authenticate for user
		UserDAO userDAO = new UserDAO(connection);
		try {
			Integer loginId = tryParse(login);
			if(loginId != null){
				user = userDAO.checkCredentials(loginId, pwd);
			}
		} catch (SQLException e) {
			response.sendError(500, "Database access failed");
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// show login page with error message

		String path;
		if (user == null) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			templateEngine.process("index.html", ctx, response.getWriter());
		} else {
			HttpSession session = request.getSession();
			session.setAttribute("currentUser", user);

			// send role to client in header
			response.setHeader("role", user.getRole());
			response.setHeader("login", String.valueOf(user.getLogin()));

			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}