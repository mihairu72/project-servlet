package com.tictactoe;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name="InitServlet", value = "/start")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Creating new session
        HttpSession currentSession = request.getSession();

        //Creating new play field
        Field field = new Field();
        Map<Integer, Sign> fieldData = field.getField();

        //Obtaining list of field values
        List<Sign> data = field.getFieldData();

        //Adding field parameters to session (required to maintain status between requests)
        currentSession.setAttribute("field", field);
        // and field values sorted by index (required to display crosses and noughts)
        currentSession.setAttribute("data", data);
        //Redirecting request to index.jsp via the server
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
