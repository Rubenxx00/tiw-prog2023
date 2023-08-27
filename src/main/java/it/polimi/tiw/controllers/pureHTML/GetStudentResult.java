package it.polimi.tiw.controllers.pureHTML;
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

import it.polimi.tiw.beans.*;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.utils.Utils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.ResultDAO;
import it.polimi.tiw.utils.ConnectionHandler;
@WebServlet("/GetStudentResult")
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
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(ctx);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
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
            CourseDAO courseDAO = new CourseDAO(connection);
            Course course = courseDAO.findCourseBySession(result.getSession_idsession());

            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(req, resp, servletContext, req.getLocale());
            ctx.setVariable("result", result);
            ctx.setVariable("student", result.getStudent());
            ctx.setVariable("course", course);

            String path = "/WEB-INF/GetStudentResult.html";
            templateEngine.process(path, ctx, resp.getWriter());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover results");
        }
    }


    // reject Result
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            if(!result.isEditableByStudent()){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Result is not editable");
                return;
            }
            resultDAO.refuseResult(sessionId, studentId);
            resp.sendRedirect(getServletContext().getContextPath() + "/GetStudentResult?sessionId=" + result.getSession_idsession());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Update failed");
        }
    }

}
