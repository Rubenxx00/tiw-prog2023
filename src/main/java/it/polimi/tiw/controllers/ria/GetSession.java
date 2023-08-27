package it.polimi.tiw.controllers.ria;

import com.google.gson.Gson;
import it.polimi.tiw.beans.Session;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Utils;
import org.thymeleaf.TemplateEngine;

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

@WebServlet("/api/sessions")
public class GetSession extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    public GetSession() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        // get courseId from request
        Integer courseId = Utils.tryParse(req.getParameter("courseId"));
        if (courseId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid course id");
            return;
        }

        // get session from db
        try {
            var sessionDAO = new SessionDAO(connection);
            List<Session> sessions = null;
            if(user.getRole().equals("teacher")){
                sessions = sessionDAO.getSessionsByCourseId(courseId);
            }
            else {
                sessions = sessionDAO.findEnrolledSessionsByStudentId(user.getLogin(), courseId);
            }
            if (sessions == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
                return;
            }
            // return session as json
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String json = new Gson().toJson(sessions);
            resp.getWriter().write(json);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }

    }
}
