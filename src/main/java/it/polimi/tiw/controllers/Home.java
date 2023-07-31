package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	private CourseDAO courseDAO;

    public Home() {
        super();
        // TODO Auto-generated constructor stub
    }

	public void init() throws ServletException {
		ServletContext ctx = getServletContext();
		connection = ConnectionHandler.getConnection(ctx);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(ctx);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		courseDAO = new CourseDAO(connection);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get user from session
		User user = (User) request.getSession().getAttribute("user");
		if (user == null) {
			response.sendRedirect(getServletContext().getContextPath() + "/index.html");
			return;
		}
		try {
			if (user.getRole().equals("teacher")) {
			String path = "/WEB-INF/TeacherHome.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			List<Course> courses = courseDAO.findCoursesByTeacherId(user.getLogin());
            Course selectedCourse = null;

            // Get the selected course ID from the "selected" parameter in the URL
            String selectedCourseId = request.getParameter("selected");

            if (selectedCourseId != null) {
                // Find the course with the provided ID
                selectedCourse = courseDAO.getCourseById(Integer.parseInt(selectedCourseId));
            }

            // Use the first course as fallback if no course is selected
            if (selectedCourse == null && !courses.isEmpty()) {
                selectedCourse = courses.get(0);
            }
			ctx.setVariable("courses", courses);
			ctx.setVariable("selectedCourse", selectedCourse);
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			String path = "/WEB-INF/StudentHome.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			templateEngine.process(path, ctx, response.getWriter());
		}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
		}

	}

}
