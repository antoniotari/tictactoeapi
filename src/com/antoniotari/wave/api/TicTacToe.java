package com.antoniotari.wave.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TicTacToe
 */
@WebServlet("/TicTacToe")
public class TicTacToe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicTacToe() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		final String queryStr = request.getQueryString().replace("%20", Board.EMPTY); // FIXME: might throw null pointer exception 
		final String[] params = queryStr.split("="); // we are expecting only the board parameter 
		// check params
		if (params.length < 2 || !params[0].equals("board") || params[1]== null) {
	    	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	    final String responseStr = apiResponse(params[1]);
	    if (responseStr == null) {
	    	response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	    }
	    response.getWriter().println(responseStr);
	}

	/**
	 * main method of the api
	 * @param boardStr
	 * @return
	 */
	private String apiResponse(String boardStr) {
		Board board = new Board(boardStr);		
		if (!board.isBoardValid()) return null;

		String returnStr;
		if (board.isFirstXMoveCorner())	{
			System.out.println("first move corner");
			// in case is o first defensive move and x is in the corner play the centre
			returnStr = board.playAndReturnString(Board.CENTRE);
		} else if (board.isWinningState()) {
			System.out.println("win state");
			returnStr = board.playAndReturnString(board.getWinningRow());
		} else if (board.isBlockState()) {
			System.out.println("block state");
			returnStr = board.playAndReturnString(board.getBlockRow());
		} else if (board.isCornerAvailable()) {
			System.out.println("corner");
			returnStr =  board.playAndReturnString(board.getAvailableCorner());
		}  else if (board.isSideAvailable()) {
			System.out.println("side");
			returnStr = board.playAndReturnString(board.getAvailableSide());
		} else {
			System.out.println("centre");
			returnStr = board.playAndReturnString(Board.CENTRE);
		}
		return returnStr;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
