package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Obtaining current session
        HttpSession currentSession = request.getSession();

        //Obtaining play field object from the session
        Field field = extractField(currentSession);

        //Obtaining clicked cell index
        int index = getSelectedIndex(request);
        Sign currentSign = field.getField().get(index);

        //Checking that the clicked cell is empty.
        //Otherwise do nothing and redirect the user to the same page
        //without changing any session parameters
        if (Sign.EMPTY != currentSign) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        //Putting cross in the cell clicked by the user
        field.getField().put(index, Sign.CROSS);

        //Checking whether the crosses win after the user's click
        if (checkWin(response, currentSession, field)) return;

        //Obtaining an empty field cell
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            //Checking whether the noughts win after adding the nought
            if (checkWin(response, currentSession, field)) return;
        } else {
            //Adding flag to the session that there is a draw
            currentSession.setAttribute("draw", true);

            //Reading sign list
            List<Sign> data = field.getFieldData();

            //Updating this list in the session
            currentSession.setAttribute("data", data);

            //Sending redirect
            response.sendRedirect("/index.jsp");
            return;
        }

        //Reading sign list
        List<Sign> data = field.getFieldData();

        //Updating field object and sign list in the session
        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);

        response.sendRedirect("/index.jsp");
    }

    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    //This method checks for three crosses/noughts in a row.
    //Returns true/false
    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            //Adding flag showing the winner
            currentSession.setAttribute("winner", winner);

            //Reading sign list
            List<Sign> data = field.getFieldData();

            //Updating this list in the session
            currentSession.setAttribute("data", data);

            //Sending redirect
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
