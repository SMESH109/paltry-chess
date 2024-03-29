
package chessengine;


import chessengine.figurbewertung.*;

/**
 * Klasse Board, repraesentiert das Spielbrett.
 * @author Christian Koenig & Dominik Erb
 */
public class Board implements  BoardInterface  {
	
	//Variablendeklaration
	private static final String boardStart = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	public int[] boardArray;
	public boolean color;			//true = weiss ; false = schwarz
	public String boardFen;
	public String IncomingFen;
	public String OutgoingFen;
	private boolean enPassent;
	private int rochade_gross;
	private int rochade_klein;
	private int zugnummer;
	private int halbzuege;
	private FigurBewertung bewertung;
	
	//Ende Variablendeklaration
	
        /**
         * Standartkonstruktor der Klasse Board
         */
	public Board(FigurBewertung bewertung) {
		boardArray = new int[128];	//Boardarray
		color = true;				//Farbe am Zug, true = weiss; false = schwarz
		enPassent = false;			//En Passents Verfuegbarkeit, true = ja; false= nein
		rochade_gross = 0;			//0 keiner,	1 weiss, 2 schwarz, 3 beide
		rochade_klein = 0;			//0 keiner,	1 weiss, 2 schwarz, 3 beide
		zugnummer = 0;				
		halbzuege = 0;				
		this.bewertung = bewertung;
	}
	
        /**
         * Konstruktor der Klasse Board mit FEN-String als Parameter.
         * @param fen FEN-String, der beim erzeugen als Startwert verwendet wird.
         */
	public Board(String fen, FigurBewertung bewertung) {
		boardArray = new int[128];	//Boardarray
		color = true;				//Farbe am Zug, true = weiss; false = schwarz
		enPassent = false;			//En Passents Verfuegbarkeit, true = ja; false= nein
		rochade_gross = 0;			//0 keiner,	1 weiss, 2 schwarz, 3 beide
		rochade_klein = 0;			//0 keiner,	1 weiss, 2 schwarz, 3 beide
		zugnummer = 0;				
		halbzuege = 0;		
		InitBoard(fen);
		this.boardFen = fen;
		this.bewertung = bewertung;	
	}
	
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#InitBoard(java.lang.String)
	 */
	@Override
	public void InitBoard(String fen) {
		FenDecode(fen);
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#ResetBoard()
	 */
	@Override
	public boolean ResetBoard() {
		IncomingFen = boardStart;
		FenDecode(IncomingFen);
		
		return true;
		
	}

	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getBoard()
	 */
	@Override
	public BoardInterface getBoard() {
		BoardInterface currentBoard = new Board(bewertung);
		return currentBoard;
	}
	
        /**
         * Methode, analysiert den Rest des FEN-Strings um Moeglichkeiten von 
         * Sonderzuegen zu ermitteln.
         * @param s
         * @param pos 
         */
	private void fenRemainingParts(String s, int pos) {
		//rochademoeglichkeit initialisieren
		rochade_gross = 0;
		rochade_klein = 0;
		//enPassant reset, notwendig da '-' nicht behandelt wird 
		enPassent = false;
		Character fenPart;
		int spaces = 0;
		StringBuffer numberOfMoves = new StringBuffer();
		int laengeString = s.length();
		pos--;
		while (spaces < 4) {
			fenPart = s.charAt(pos);
			
			/**How it works
			 * zu beginn werden moegliche Rochaden auf 0 gesetzt
			 * durch addition ergeben sich ab dann die Rochedemoeglichkeiten
			 * K ist kleine Rochade fuer weiss und hat den Wert 1
			 * k ist kleine Rochade fuer schwarz und hat den Wert 2 
			 * Q,q simultan fuer grosse rochade
			 * ist eine Rochade nicht moeglich so setzt sie keinen Wert
			 * so ergibt sich 1 fuer weiss 2 fuer schwarz und 3 sollten beide die 
			 * moeglichkeit haben. 0 sollte keiner rochieren koennen
			 * Aendert sich dies nach einem Zug wird beim naechsten fen-decode 
			 * dementsprechen der wert angepasst da startwert erneut 0 
			 */
			switch (fenPart) {
			case ' ':
				spaces++;
			case 'K':
				rochade_klein = rochade_klein + 1;
				break;
			case 'Q':
				rochade_gross = rochade_gross + 1;
				break;
			case 'k':
				rochade_klein = rochade_klein + 2;
				break;
			case 'q':
				rochade_gross = rochade_gross + 2;
				break;
			case ('a'|'b'|'c'|'d'|'e'|'f'|'g'|'h'):
				enPassent = true;
			    pos++;
				break;
			default:
				break;
			}
			pos++;
		}
		/**Halbzuege
		 * hier werden die Halbzuege ermittelt
		 * und aus dem String gezogen
		 * nicht mit case da werte von 1 bis 50 ohne weiteres entstehen
		 */
		while ( !Character.isSpaceChar(s.charAt(pos))) { 
			numberOfMoves.append(s.charAt(pos));
			pos++;
		} 
		halbzuege = Integer.parseInt(numberOfMoves.toString());
		//zuruecksetzen des numberOfMoves damit keine falschen Zugzahlen entstehen
		numberOfMoves.delete(0, numberOfMoves.length());
		/**Zuege
		 * hier werden die Zuege ermittelt
		 * und aus dem String gezogen
		 * nicht mit case da werte von 1 bis 50 ohne weiteres entstehen
		 */
		while ( pos < laengeString) {
			numberOfMoves.append(s.charAt(pos));
			pos++;
		}
		zugnummer = Integer.parseInt(numberOfMoves.toString().trim());
		/**zuruecksetzen des numberOfMoves damit keine falschen Zugzahlen entstehen
		 * bei einer erneuten ausfuehrung des Fen-decodes
		 */
		numberOfMoves.delete(0, numberOfMoves.length());
	}
	
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#FenDecode(java.lang.String)
	 */
	@Override
	public int[] FenDecode(String s) {
		int[] ausgabe = new int[128];
		int i = 0; //pos im Feld
		Character fenPart;
		int pos = 0; //pos in FenString
		
		fenPart = s.charAt(pos);
		
		while ( (!Character.isSpaceChar(fenPart))  ) {
			
			fenPart = s.charAt(pos);
			
			switch (fenPart) {
			case 'p':
				ausgabe[i] = 1;
				break;
			case 'P':
				ausgabe[i] = 11;
				break;
			case 'r':
				ausgabe[i] = 2;
				break;
			case 'R':
				ausgabe[i] = 12;
				break;
			case 'n':
				ausgabe[i] = 3;
				break;
			case 'N':
				ausgabe[i] = 13;
				break;
			case 'b':
				ausgabe[i] = 4;
				break;
			case 'B':
				ausgabe[i] = 14;
				break;
			case 'q':
				ausgabe[i] = 5;
				break;
			case 'Q':
				ausgabe[i] = 15;
				break;
			case 'k':
				ausgabe[i] = 6;
				break;
			case 'K':
				ausgabe[i] = 16;
				break;
			case ('1' | '2' | '3' | '4' | '5' | '6' | '7' | '8'):
				int leerFelder = Integer.parseInt( fenPart.toString() );
				for (int j = 0; j < leerFelder; j++) {
					ausgabe[i] = 0;
					i++;
				}
				i--;
				break;
			case '/':
				i = i + 8;
				break;
			default:
				break;
			}
			i++;
			pos++;
		} //endWhile
		fenRemainingParts(s, pos);
		return ausgabe;
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#FenEncode()
	 */
	@Override
	public String FenEncode() {
		//decodiert FEN Strings
		
		return OutgoingFen;
		
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#IntToFen(int)
	 */
	@Override
	public Character IntToFen(int toConvert) {
		switch (toConvert) {
		case 1:
			return 'p';
		case 11:
			return 'P';
		case 2:
			return 'r';
		case 12:
			return 'R';
		case 3:
			return 'n';
		case 13:
			return 'N';
		case 4:
			return 'b';
		case 14:
			return 'B';
		case 5:
			return 'q';
		case 15:
			return 'Q';
		case 6:
			return 'k';
		case 16:
			return 'K';
		default:
			return '1';
		}
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getBoardValue()
	 */
	@Override
	public int getBoardValue() {

		int figur;
		int value = 0;
		for(int i = 0; i < 128; i++ ){
			figur = boardArray[i];
			switch (figur) {
			case 1:
				value -= bewertung.getPawnBewertung();
				break;
			case 11:
				value += bewertung.getPawnBewertung();
				break;
			case 2:
				value -= bewertung.getRookBewertung();
				break;
			case 12:
				value += bewertung.getRookBewertung();
				break;
			case 3:
				value -= bewertung.getKnightBewertung();
				break;
			case 13:
				value += bewertung.getKnightBewertung();
				break;
			case 4:
				value -= bewertung.getBishopBewertung();
				break;
			case 14:
				value += bewertung.getBishopBewertung();
				break;
			case 5:
				value -= bewertung.getQueenBewertung();
				break;
			case 15:
				value += bewertung.getQueenBewertung();
				break;
			case 6:
				value -= bewertung.getKingBewertung(); //koenig ueberpruefung auf ungueltige zeuge 100000
				break;
			case 16:
				value += bewertung.getKingBewertung();
				break;
			default:
				break;
			}

		}
		return value;
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getRochadeGross()
	 */
	@Override
	public int getRochadeGross() {
		return this.rochade_gross;
	}
	
	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getRochadeKlein()
	 */
	@Override
	public int getRochadeKlein() {
		return this.rochade_klein;
	}

	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#isEnPassent()
	 */
	@Override
	public boolean isEnPassent() {
		return enPassent;
	}

	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getZugnummer()
	 */
	@Override
	public int getZugnummer() {
		return zugnummer;
	}

	/* (non-Javadoc)
	 * @see chessengine.BoardInterface#getHalbzuege()
	 */
	@Override
	public int getHalbzuege() {
		return halbzuege;
	}
	
	public void setZuege()  {
		halbzuege++;
		zugnummer++;
	}
	
	public String getBoardFen() {
		return boardFen;
	}
	
	
}