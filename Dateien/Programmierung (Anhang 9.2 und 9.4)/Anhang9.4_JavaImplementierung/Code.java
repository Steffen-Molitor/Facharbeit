/**
*
*/
public class Code 
{
  //Attribute
	private String code;
	private char zeichen;

  //Konstruktor
  public Code(char pZeichen, String pCode) {
    zeichen = pZeichen;
    code = pCode;
  }

  //Getter-Methoden
  public String gibCode(){
    return code;
  }
  public char gibZeichen(){
    return zeichen;
  }
}

