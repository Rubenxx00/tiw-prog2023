package it.polimi.tiw.controllers.ria;

import it.polimi.tiw.InvalidValueException;
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

@WebServlet("/api/editResults")
public class EditResults extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public EditResults() {
        super();
    }

    public void init() throws ServletException {
        ServletContext ctx = getServletContext();
        connection = ConnectionHandler.getConnection(ctx);
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(ctx);
        templateResolver.setTemplateMode(TemplateMode.HTML);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // update result
        Integer sessionId = Utils.tryParse(req.getParameter("sessionId"));
        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing params");
            return;
        }
        List<Result> results = null;
        // read json
        try {
            String json = Utils.readBody(req);
            results = Utils.parseJsonList(json, Result.class);
            if(results == null){
                throw new Exception();
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid body");
            return;
        }
        try {
            ResultDAO resultDAO = new ResultDAO(connection);
            resultDAO.updateResults(sessionId, results);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
            return;
        } catch (InvalidValueException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
    }

}
