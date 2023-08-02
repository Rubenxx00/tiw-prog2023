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

import it.polimi.tiw.beans.Result;
import it.polimi.tiw.utils.Utils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.ResultDAO;
import it.polimi.tiw.utils.ConnectionHandler;
@WebServlet("/GetResults")
public class GetResults extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    private ResultDAO resultDAO;

    public GetResults() {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing session id");
            return;
        }
        try {
            List<Result> results = resultDAO.getResultsBySessionId(sessionId);
            final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
            ctx.setVariable("results", results);
            templateEngine.process("/WEB-INF/GetResults.html", ctx, resp.getWriter());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }
    }

}