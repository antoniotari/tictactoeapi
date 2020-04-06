package com.antoniotari.wave.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Board {
	
	/**
	 * a point in the board
	 */
	public static class Point {
		public int x = -1;
		public int y = -1;
		
		public Point() {
			x = -1;
			y = -1;
		}
		
		public Point(final int x, final int y) { // should have probably inverted the coordinates order for readability
			this.x = x;
			this.y = y;
		}
		
		@Override
		public boolean equals(Object obj) {
			// TODO: check casting
			Point p = (Point) obj;
			return p.x == this.x && p.y == this.y;
		}
		
		@Override
		public int hashCode() {
			return (x * 10) + y;
		}
	}
	
	public static final String X = "x";
	public static final String O = "o";
	public static final String EMPTY = " "; // change to dash (-) if space doesn't work
	private static final Set<String> VALID_CHARS = new HashSet<String>() {{
	    add(X);
	    add(O);
	    add(EMPTY);
	}};
	
	private static final Set<Point> CORNERS = new HashSet<Point>() {{
		add(new Point(0,0));
		add(new Point(2,0));
		add(new Point(0,2));
		add(new Point(2,2));
	}};
	
	private static final Set<Point> SIDES = new HashSet<Point>() {{
		add(new Point(1,0));
		add(new Point(0,1));
		add(new Point(2,1));
		add(new Point(1,2));
	}};
	
	public static final Point CENTRE = new Point(1,1);
	
	private final String boardStr;
	private final String[] boardArr;
	
	public Board(String boardStr) {
		this.boardStr = boardStr.toLowerCase();
		boardArr = boardStr.split("(?<=\\G.{" + 3 + "})"); // regex splits the string in 3 rows
	}
	
	public String getValueAtPoint(final Point point) {
		return boardArr[point.y].split("")[point.x];
	}
	
	public boolean isBoardValid() {
		if (boardStr == null) return false;
		if (boardStr.length() != 9) return false;
		
		int xCount = 0;
		int oCount = 0;
		
		for (String s : boardStr.split("")) {
			if (!VALID_CHARS.contains(s.toLowerCase())) {
				return false;
			}
			if (s.toLowerCase().equalsIgnoreCase(X)) {
				++xCount;
			} else if (s.toLowerCase().equalsIgnoreCase(O)) {
				++oCount;
			}
		}
		
		// TODO: check if the board already has a winner
		return (xCount-oCount) == 1 || (oCount-xCount) == 0;
	}
	
	public boolean isWinningState() {
		return getWinningRow().y != -1;
	}
		
	public boolean isBlockState() {
		return getBlockRow().y != -1;
	}
	
	public Point getWinningRow() {
		return getPosition(O, X);
	}
	
	public Point getBlockRow() {
		return getPosition(X, O);
	}
	
	
	private Point getPosition(String player, String opponent) {
		Point point = new Point();
		List<String> optionsList = new ArrayList<String>(Arrays.asList(boardArr));
		
		// adding possible options
		// no time for clean implementation, position 3 is diagonal from (0,0), position 4 is diagonal from 0,2)
		// positions 5 to 7 are the 3 columns
		// TODO: replace with getValueAtPoint method (no time)
		optionsList.add(boardArr[0].split("")[0] + boardArr[1].split("")[1] + boardArr[2].split("")[2]);
		optionsList.add(boardArr[0].split("")[2] + boardArr[1].split("")[1] + boardArr[2].split("")[0]);
		optionsList.add(boardArr[0].split("")[0] + boardArr[1].split("")[0] + boardArr[2].split("")[0]);
		optionsList.add(boardArr[0].split("")[1] + boardArr[1].split("")[1] + boardArr[2].split("")[1]);
		optionsList.add(boardArr[0].split("")[2] + boardArr[1].split("")[2] + boardArr[2].split("")[2]);

		
		for (int i=0; i<optionsList.size(); i++) {
			String currentRow = optionsList.get(i);
			if (Pattern.compile("(?:"+player+".*?){2,}").matcher(currentRow).matches() &&
					!currentRow.contains(opponent)) {
				if (i < 3) {
					point.y = i;
					return point;
				} else {
					// case by case
					switch (i) {
					case 3:
						// diagonal from (0,0)
						// diagonal from (0,2)
						point.y = currentRow.indexOf(EMPTY);
						point.x = currentRow.indexOf(EMPTY);
						return point;
					case 4:
						// diagonal from (0,2)
						point.y = currentRow.indexOf(EMPTY);
						point.x = 2 - currentRow.indexOf(EMPTY);
						return point;
					case 5:
						// first column
						point.x = 0;
						point.y = currentRow.indexOf(EMPTY);
						return point;
					case 6:
						// second column
						point.x = 1;
						point.y = currentRow.indexOf(EMPTY);
						return point;
					case 7:
						// third column
						point.x = 2;
						point.y = currentRow.indexOf(EMPTY);
						return point;
					}
				}
			}
		}
		
		return point;
	}
	
	public String playAndReturnString(Point rowPos) {
		String rowStr = boardArr[rowPos.y];
		
		if (rowPos.x != -1) {
			char[] chars = rowStr.toCharArray();
			chars[rowPos.x] = O.toCharArray()[0];
			boardArr[rowPos.y] = String.valueOf(chars);
		} else {
			boardArr[rowPos.y] = rowStr.replace(EMPTY, O);
		}
	    
		return this.toString();//Arrays.toString(boardArr);
	}
	
	public Point getAvailableCorner() {
		for (Point p:CORNERS) {
			if (getValueAtPoint(p).equals(EMPTY)) {
				return p;
			}
		}
		
		return null;
	}
	
	public boolean isCornerAvailable() {
		return getAvailableCorner() != null;
	}
	
	public Point getAvailableSide() {
		for (Point p:SIDES) {
			if (getValueAtPoint(p).equals(EMPTY)) {
				return p;
			}
		}
		
		return null;
	}
	
	public boolean isSideAvailable() {
		return getAvailableSide() != null;
	}
	
	public boolean isGameOver() {
		return !boardStr.contains(EMPTY); // || checkBoardForWinner() ; no time to implement this method that also isBoardValid would use
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String row:boardArr) {
			sb.append(row);
		}
		return sb.toString();
	}
	
	/**
	 * checks if it's first move of x and if it is corner
	 * @return
	 */
	public boolean isFirstXMoveCorner() {
		if (boardStr.contains(O)) return false;
		int xCount = 0;
		boolean isCorner = false;
		for (Point p:CORNERS) {
			if (getValueAtPoint(p).equals(X)) {
				isCorner = true;
				++xCount;
			}
		}
		
		return (xCount == 1 && isCorner);
	}
	
	/*public String returnWinningString(Point rowPos) {
		String rowStr = boardArr[rowPos];
		boardArr[rowPos] = rowStr.replace(EMPTY, O);
		return Arrays.toString(boardArr);
	}
	
	public String returnString(Point rowPos) {
		
	}
	
	
	public int getWinningRow2() {
		for (int i=0; i<3; i++) {
			String currentRow = boardArr[i];
			
			if (Pattern.compile("(?:"+O+".*?){2,}").matcher(currentRow).matches() &&
					!currentRow.contains(X)) {
				return i;
			}
		}
		return -1;
	}*/
}
