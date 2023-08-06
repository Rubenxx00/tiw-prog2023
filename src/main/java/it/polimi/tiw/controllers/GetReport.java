package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Report;
import it.polimi.tiw.dao.ReportDAO;
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

@WebServlet("/GetReport")
public class GetReport extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public GetReport() {
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
        Integer reportId = Utils.tryParse(req.getParameter("reportId"));
        if (sessionId == null && reportId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing params");
            return;
        }
        try {
            ReportDAO reportDAO = new ReportDAO(connection);
            Report report;
            if (reportId != null) {
                report = reportDAO.getSessionByReportId(reportId);
            } else {
                report = reportDAO.getReportBySessionId(sessionId);
            }
            final WebContext ctx = new WebContext(req, resp, getServletContext(), req.getLocale());
            ctx.setVariable("report", report);
            ctx.setVariable("rows", report.getRows());
            templateEngine.process("/WEB-INF/Report.html", ctx, resp.getWriter());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover report");
            return;
        }
    }
}