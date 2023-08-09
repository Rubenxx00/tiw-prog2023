package it.polimi.tiw.controllers.ria;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import it.polimi.tiw.beans.*;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.utils.Utils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.ResultDAO;
import it.polimi.tiw.utils.ConnectionHandler;
@WebServlet("/GetStudentResultRIA")
public class GetStudentResult extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public GetStudentResult() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        // get studentId from session
        Integer studentId = ((User) req.getSession().getAttribute("currentUser")).getLogin();
        if (sessionId == null || studentId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing session id or student id");
            return;
        }
        try {
            ResultDAO resultDAO = new ResultDAO(connection);
            Result result = resultDAO.getResultByKey(sessionId, studentId);
            if(result == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No result found");
                return;
            }
            // send out json result
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String json = new Gson().toJson(result);
            resp.getWriter().write(json);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover results");
        }
    }

}
