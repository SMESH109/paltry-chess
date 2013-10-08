/**
 * 
 */
package chessengine.moveevaluator;

import java.util.LinkedList;

import chessengine.movegenerator.MoveGenerator;
import chessengine.Board;

/**
 * Klasse repraesentiert einen Zugbaum.
 * @author Dominik A. Erb
 */
public class MoveEvaluatorTree {

	private MoveEvaluatorTreeNode root;
	private LinkedList<MoveEvaluatorTreeNode> childList; 
	private MoveGenerator movegen;
	
	
        /**
         * Konstruktor mit aktuellem Board als Parameter.
         * @param board 
         */
	public MoveEvaluatorTree(Board board) {
		this.root = new MoveEvaluatorTreeNode(board);
		this.movegen = new MoveGenerator();
		generateTree();
	}
	
        /**
         * Getter-Methode fuer die Wurzel des Bauemes.
         * @return root
         */
	public MoveEvaluatorTreeNode getTree() {
		return root;
	}
	
	/**
	 * erzeugt den Baum bis Tiefe 3 
	 */
	private void generateTree() {
		MoveEvaluatorTreeNode currentNode;
		MoveEvaluatorTreeNode parentNode;
		int countOfRootLeaf = 0;
		
		//Tiefe 1
		root.setChildBoards( movegen.getZuege() );
		countOfRootLeaf = root.hasChild();
		//Tiefe 2
		countOfRootLeaf = root.hasChild();
		for (int i = 0 ; i < countOfRootLeaf ; i++) {
			currentNode = root.getChildAtPos(i);
			currentNode.setChildBoards( movegen.getZuege() );
		}
		//Tiefe 3
		for (int j = 0 ; j < countOfRootLeaf ; j++ ) {
			parentNode = root.getChildAtPos(j);
			for (int k = 0 ; j < parentNode.hasChild() ; k++) {
				currentNode = parentNode.getChildAtPos(k);
				currentNode.setChildBoards( movegen.getZuege() );
			}
		}
	}

        /**
         * Getter-Methode fuer die Liste der Kind-Knoten.
         * @return childList
         */
	public LinkedList<MoveEvaluatorTreeNode> getChildList() {
		return childList;
	}
	
        /**
         * Methode ermittelt den besten Zug und liefert diesen zurueck.
         * @return 
         */
	public MoveEvaluatorTreeNode getBestMove() {
		MoveEvaluatorTreeNode parrentNode;
		MoveEvaluatorTreeNode currentNode;
		int max = -10000;
		int maxPosition =1;
		for (int i = 0; i < root.hasChild(); i++) {
			parrentNode = root.getChildAtPos(i);
			for (int j = 0; j < parrentNode.hasChild(); j++) {
				currentNode = parrentNode.getChildAtPos(j);
				if (currentNode.getBoardValue() < currentNode.minChild) {
					currentNode.minChild = currentNode.getBoardValue();
					currentNode.posMinChild = j;
				}
			}
			if (parrentNode.getBoardValue() > max) {
				max = parrentNode.getBoardValue();
				maxPosition = i;
			}
		}
		return root.getChildAtPos(maxPosition);
	}
	
	
	
	
	
}
