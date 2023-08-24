package it.polimi.tiw.controllers.pureHTML;

import it.polimi.tiw.dao.ReportDAO;
import it.polimi.tiw.dao.ResultDAO;
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

@WebServlet("/PostResults")
public class PostResults extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public PostResults() {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResultDAO resultDAO = new ResultDAO(connection);

        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing params");
            return;
        }
        // check submit value

        try {
            if (req.getParameter("submit").equals("Publish")) {
                // check if all results are not null
                if(resultDAO.hasNullResults(sessionId)){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Some results are missing yet");
                    return;
                }
                resultDAO.setResultPublished(sessionId);
                resp.sendRedirect(getServletContext().getContextPath() + "/GetResults?sessionId=" + sessionId);
            } else if (req.getParameter("submit").equals("Record")) {
                // if report is already recorded, redirect to results page
                SessionDAO sessionDAO = new SessionDAO(connection);
                if(sessionDAO.isReported(sessionId)){
                    resp.sendRedirect(getServletContext().getContextPath() + "/GetReport?sessionId=" + sessionId);
                    return;
                }
                // else, record report

                // check if all results are published
                if(resultDAO.hasNotPublishedResults(sessionId)){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Results have not been published yet");
                    return;
                }
                ReportDAO reportDAO = new ReportDAO(connection);
                int reportId = reportDAO.createReport(sessionId);
                resp.sendRedirect(getServletContext().getContextPath() + "/GetReport?reportId=" + reportId);
            }
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
                return;
        }
    }
}
