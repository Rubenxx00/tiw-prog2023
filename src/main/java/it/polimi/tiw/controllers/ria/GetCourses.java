package it.polimi.tiw.controllers.ria;

import com.google.gson.Gson;
import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Utils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetCourses")
public class GetCourses extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    public GetCourses() {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("currentUser");
        try {
            if (user.getRole().equals("teacher")) {
                CourseDAO courseDAO = new CourseDAO(connection);
                List<Course> courses = courseDAO.findCoursesByTeacherId(user.getLogin());
                // send out JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(new Gson().toJson(courses));
            } else {
                String path = "/WEB-INF/StudentHome.html";
                ServletContext servletContext = getServletContext();
                final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
                CourseDAO courseDAO = new CourseDAO(connection);
                List<Course> courses = courseDAO.findEnrolledCoursesByStudentId(user.getLogin());

                // Get the selected course ID from the "selected" parameter in the URL
                Integer selectedCourseId = Utils.tryParse(request.getParameter("selected"));

                // Use the first course as fallback if no course is selected
                if (selectedCourseId == null && !courses.isEmpty()) {
                    selectedCourseId = courses.get(0).getIdcourse();
                }

                if (selectedCourseId != null) {
                    // Load sessions for selected course
                    SessionDAO sessionDAO = new SessionDAO(connection);
                    ctx.setVariable("sessions", sessionDAO.findEnrolledSessionsByStudentId(user.getLogin(), selectedCourseId));
                }
                ctx.setVariable("courses", courses);
                ctx.setVariable("selectedCourseId", selectedCourseId);
                templateEngine.process(path, ctx, response.getWriter());
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }

    }

}
