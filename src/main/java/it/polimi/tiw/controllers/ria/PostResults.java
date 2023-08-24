package it.polimi.tiw.controllers.ria;

import it.polimi.tiw.dao.ReportDAO;
import it.polimi.tiw.dao.ResultDAO;
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

@WebServlet("/api/postResults")
public class PostResults extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    public PostResults() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
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
                ReportDAO reportDAO = new ReportDAO(connection);
                if(reportDAO.isResultRecorded(sessionId)){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Results are already recorded");
                    return;
                }
                // else, record report

                // check if all results are published
                if(resultDAO.hasNotPublishedResults(sessionId)){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Results have not been published yet");
                    return;
                }
                resultDAO.setResultRecorded(sessionId);

            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
            return;
        }
    }
}
