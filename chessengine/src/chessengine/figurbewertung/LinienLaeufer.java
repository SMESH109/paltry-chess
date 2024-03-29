package chessengine.figurbewertung;

import java.util.LinkedList;
import java.util.Stack;

import chessengine.tools.Brett;
import chessengine.tools.FenDecoder;
import chessengine.tools.Figur;
import chessengine.tools.SchachPosition;


/**
 *  erzeugt zugmoeglichkeiten fuer klassische linieneinheiten wie Laeufer und Turm und Koenig
 *  und generiet Fen mithilfe des FendDecoder
 * @author Philip Hunsicker
 * Stand : 25.09.2013
 */
public class LinienLaeufer {

	protected Brett schachBrett;
	protected Brett neuesBrett;
	protected FenDecoder decoder;
	

/**
 * 
 * @param position
 * @param istWeis
 * @param schachbrett
 * @param bewegungsMuster art die die Figure bewegt werden soll
 * @param reichWeite 1 fuer ein feld 2 fuer 2felder
 * @return
 */
	public LinkedList<String> ermittleZuege(SchachPosition position, boolean istWeis, Stack<SchachPosition> bewegungsMuster, int reichWeite) {
		return ermittleZuege( position,  istWeis,  bewegungsMuster,  reichWeite, null);
	}
	public LinkedList<String> ermittleZuege(SchachPosition position, boolean istWeis, Stack<SchachPosition> bewegungsMuster, int reichWeite, String neueRochade) {
		
		LinkedList<String> moeglichkeiten = new LinkedList<String>();
		//zeiger.setXY(position);
		int x = position.getX();
		int y = position.getY();
		int weite;
		
		while( !bewegungsMuster.isEmpty()  ){//laeuft alle Muster ab
			x =  position.getX(); //setze auf Ausgangsposition
			y =  position.getY();
			weite = 0;
			x = x + bewegungsMuster.peek().getX();
			y = y + bewegungsMuster.peek().getY();
			
			while(x < 8  && x >=0 && y >=0 && y < 8 && weite < reichWeite){ //Solange Rand nicht rand ereicht und reichweiter nich tueberschritten

				if( schachBrett.getIsEmpty(x, y) == false  ){//Wenn eine Figur auf dem Feld
					
					if(istWeis != schachBrett.getInhalt(x, y).istWeis()){// Wenn feindlich figure
						
						moeglichkeiten.push( generiereFen(x, y, position , neueRochade) );//schlage //, schachBrett[x][y].getTyp()) typ der geschlagen figur
						
					}
					x=8; //in beiden fuellen beende schleife
					
				}else{//else keine figure auf dem Feld
					
					moeglichkeiten.push( generiereFen(x, y, position , neueRochade) );
					x = x + bewegungsMuster.peek().getX();//bewege nach muster
					y = y + bewegungsMuster.peek().getY();
				}//else
				weite++ ; //weite +1 das naechstes Feld  geprueft wird. Diesen komentar ignorieren
				
			}//linie /Muster
			bewegungsMuster.pop(); // naechste muster bearbeiten
		}//richtungen
		

		return moeglichkeiten;
	}
	
	/**
	 *  inizialisiert das schachbrett fue alle darauffolgende aufrufe von ermittle zuege
	 * @param schachBrett
	 */
	public void inizialisiere(Brett schachBrett, FenDecoder decoder) {
		this.schachBrett = schachBrett;
		this.decoder = decoder;
	}
	
	
	/**
	 * Generiert mit Hilfe des Decoder einen Fen aus Asugang und ZielPosition
	 * @param x
	 * @param y
	 * @param position
	 * @param neueRochade
	 * @return
	 */
	public String generiereFen(int x, int y, SchachPosition position, String neueRochade){
		neuesBrett = schachBrett.bewegeFigur(x, y ,position);
		return decoder.codiererNeuenZug(neuesBrett, neueRochade);
	}
	public LinkedList<String> generiereFenPromotion(int x, int y, SchachPosition position, String neueRochade, boolean promotion){
		LinkedList<String> moeglichkeiten = new LinkedList<String>();
		neuesBrett = schachBrett.bewegeFigur(x, y ,position);
		
		;
		if(promotion){
			neuesBrett = neuesBrett.copy();
			neuesBrett.promotionQueen(x, y);
			moeglichkeiten.push(decoder.codiererNeuenZug(neuesBrett, neueRochade));
			neuesBrett = neuesBrett.copy();
			neuesBrett.promotionKnight(x, y);
			moeglichkeiten.push(decoder.codiererNeuenZug(neuesBrett, neueRochade));
			neuesBrett = neuesBrett.copy();
			neuesBrett.promotionBishop(x, y);
			moeglichkeiten.push(decoder.codiererNeuenZug(neuesBrett, neueRochade));
			neuesBrett = neuesBrett.copy();
			neuesBrett.promotionRook(x, y);
			moeglichkeiten.push(decoder.codiererNeuenZug(neuesBrett, neueRochade));
			neuesBrett.degradiereToPawn(x, y);//Da neuesBrett und schachBrett auf das das gleiche Object Figur zeigt muss dieses auf Ausgangstellungn zurueckgesetzt werden
		}else{
			moeglichkeiten.push(decoder.codiererNeuenZug(neuesBrett, neueRochade));
		}
		return moeglichkeiten;
	}

	
	/**
	 * variations von generiereFend spezialisiertfuer rochade
	 * @param x
	 * @param y
	 * @param position
	 * @param ausgangsPostionTurm
	 * @param zielDesTurms
	 * @return
	 */
	public  String generiereRochadenFen(int x, int y, SchachPosition position, SchachPosition ausgangsPostionTurm, SchachPosition zielDesTurms, String neueRochade){

		neuesBrett = schachBrett.bewegeFigur(x,y,position); // bewegt den koenig von position nach x y
		neuesBrett = neuesBrett.bewegeFigurOhneKopie(zielDesTurms.getX(),zielDesTurms.getY(),ausgangsPostionTurm);
		
		return decoder.codiererNeuenZug(neuesBrett, neueRochade);
	}
	/**
	 *   variations von generiereFend spezialisiertfuer EnPassant schlaege
	 * @param x
	 * @param y
	 * @param position
	 * @param feindlicherBauer
	 * @return
	 */
	public  String generiereEnPassantFenSchlage(int x, int y, SchachPosition position, SchachPosition feindlicherBauer){
		neuesBrett = schachBrett.bewegeFigur(x,y,position);
		neuesBrett.setEmpty(feindlicherBauer); // entfernt den geschlagen Bauer
		return decoder.codiererNeuenZug(neuesBrett);
	}
	/**
	 *  aender den FEN so als waere ein Bauer um 2 nach vorn gerueckt und speichert die SchlagPosition ala EnPassant in den FEN
	 * @param x
	 * @param y
	 * @param position
	 * @param schlagPosition
	 * @return
	 */
	public  String generiereEnPassantFenZiehe(int x, int y, SchachPosition position, SchachPosition schlagPosition){
		neuesBrett = schachBrett.bewegeFigur(x,y,position);
		return decoder.codiererNeuenZugEnpassant(neuesBrett, schlagPosition);
	}
	
	//speziall fuer Koenig
	/** modizfiziert Kopie von ermittlerZuege
	 * laeuft die Muster ab und  liefert  angetroffen "schlagbaren" einheiten zurueckt
	 * @return Figure der anderen Farbe auf den "Linien/Mustern"
	 */
	public Stack<Figur> ermittleSchlaege(SchachPosition position, boolean istWeis, SchachPosition[] bewegungsMuster, int reichWeite) {
		int zeiger = 0;
		Figur figur ; // zwischenspeicher
		Stack<Figur> treffer = new Stack<Figur>();
		//zeiger.setXY(position);
		int x = position.getX();
		int y = position.getY();
		int weite;
		
		while( zeiger < bewegungsMuster.length  ){//laeuft alle Muster ab
			x =  position.getX(); //setze auf Ausgangsposition
			y =  position.getY();
			weite = 0;
			x = x + bewegungsMuster[zeiger].getX();
			y = y + bewegungsMuster[zeiger].getY();
			while(x < 8  && x >=0 && y >=0 && y < 8 && weite < reichWeite){    //Solange Rand nicht rand ereicht und reichweiter nich tueberschritten

				if( schachBrett.getIsEmpty(x, y) == false ){//Wenn eine Figur auf dem Feld
					figur =  schachBrett.getInhalt(x, y);
					if(istWeis != figur.istWeis()){// Wenn feindlich figure
						treffer.add(figur);
					}
					x=8; //in beiden faellen beende schleife
					
				}else{//else keine figure auf dem Feld
					
					x = x + bewegungsMuster[zeiger].getX();//bewege nach muster
					y = y + bewegungsMuster[zeiger].getY();
				}//else
				weite++ ; //weite +1 das naechstes Feld  geprueft wird. Diesen komentar ignorieren
				
			}//linie /Muster
			zeiger++;// naechste muster bearbeiten
		}//richtungen
		

		return treffer;
	}
}//class
