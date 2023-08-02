package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Result;
import it.polimi.tiw.dao.ResultDAO;
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

@WebServlet("/EditResult")
public class EditResult extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    private ResultDAO resultDAO;

    public EditResult() {
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

        resultDAO = new ResultDAO(connection);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // get sessionId and studentId from form
        Integer studentId = Utils.tryParse(req.getParameter("studentId"));
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        if (sessionId == null || studentId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing params");
            return;
        }
        try {
            Result result = resultDAO.getResultByKey(sessionId, studentId);
            final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
            ctx.setVariable("result", result);
            ctx.setVariable("student", result.getStudent());
            templateEngine.process("/WEB-INF/EditResult.html", ctx, resp.getWriter());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }
    }
}
