package it.polimi.tiw.controllers;

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

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        String sortBy = req.getParameter("sort");
        String sortOrder = req.getParameter("order");
        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing session id");
            return;
        }
        try {
            ResultDAO resultDAO = new ResultDAO(connection);
            List<Result> results = resultDAO.getResultsBySessionId(sessionId);
            if(sortBy != null){
               ResultDAO.sortResults(results, sortBy, sortOrder);
            }
            final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
            ctx.setVariable("results", results);
            ctx.setVariable("sessionId", sessionId);
            ctx.setVariable("order", sortOrder);
            templateEngine.process("/WEB-INF/GetResults.html", ctx, resp.getWriter());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }
    }

}