package chessengine;

/**
 *
 * @author Alexander Kessler
 */
public class Manager {

    private UCI newUCI;
    private boolean stop;
    
    public Manager() {
        stop = false;
    }

    public void initialize (){
        try {
            newUCI = new UCI();
            //muss noch in einen extra Thread
            newUCI.input();
            stop = newUCI.getStop();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Setter fuer den Stop-Befehl.
     * @Param boolean neuer Wert fuer stop-variable
     */
    public void setStop(boolean stop){
        this.stop = stop;
    }
}
