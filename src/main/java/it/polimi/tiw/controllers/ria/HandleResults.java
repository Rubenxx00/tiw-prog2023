package it.polimi.tiw.controllers.ria;

import com.google.gson.Gson;
import it.polimi.tiw.beans.Result;
import it.polimi.tiw.dao.ReportDAO;
import it.polimi.tiw.dao.ResultDAO;
import it.polimi.tiw.dao.SessionDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.Utils;

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

@WebServlet("/api/results")
public class HandleResults extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public HandleResults() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing session id");
            return;
        }
        try {
            ResultDAO resultDAO = new ResultDAO(connection);
            List<Result> results = resultDAO.getResultsBySessionId(sessionId);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            // use Gson to serialize enum
            resp.getWriter().write(new Gson().toJson(results));
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
        }
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
            if (req.getParameter("action").equals("publish")) {
                // check if all results are not null
                if(resultDAO.hasNullResults(sessionId)){
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("text/plain");
                    resp.getWriter().println("Some results are missing yet");
                    return;
                }
                resultDAO.setResultPublished(sessionId);
            } else if (req.getParameter("action").equals("record")) {
                // if report is already recorded, redirect to results page
                SessionDAO sessionDAO = new SessionDAO(connection);
                if (sessionDAO.isReported(sessionId)) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("text/plain");
                    resp.getWriter().println("Report already recorded");
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
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("text/plain");
                resp.getWriter().println(reportId);

            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
            return;
        }
    }

}