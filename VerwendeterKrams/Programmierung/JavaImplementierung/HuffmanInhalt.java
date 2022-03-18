public class HuffmanInhalt 
{
  //Attribute
	private char zeichen;
	private int wert;
  
  //Konstruktoren
  public HuffmanInhalt() {
    zeichen = Character.MIN_VALUE;
    wert = 0;
  }
  public HuffmanInhalt(char pZeichen, int pWert) {
    zeichen = pZeichen;
    wert = pWert;
  }

  //Getter-Methoden
  public char gibZeichen(){
    return zeichen;
  }
  public int gibWert(){
    return wert;
  }

  //Methoden um die Wertigkeit zu erhöhen
  public void wertErhoehen() {
    wert++;
  }
  public void erhoeheWertUm(int pWert) {
    wert += pWert;
  }
}

