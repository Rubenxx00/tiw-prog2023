package it.polimi.tiw.controllers.ria;

import com.google.gson.Gson;
import it.polimi.tiw.beans.Report;
import it.polimi.tiw.dao.ReportDAO;
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

@WebServlet("/api/report")
public class GetReport extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public GetReport() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
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
            if (reportId == null) {
                var session = new SessionDAO(connection).getSessionById(sessionId);
                if (!session.isReported()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Report not available yet");
                    return;
                }
                reportId = session.getReport_idreport();;
            }
            report = reportDAO.getReportByReportId(reportId);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(new Gson().toJson(report));

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover report");
            return;
        }
    }
}