/**
*
*/
public class Codetabelle
{
  //Attribute
	private List<Code> tabelle = new List<Code>();

  //Methoden
  public char gibZeichen(String  pCode){
    tabelle.toFirst();
    while(tabelle.hasAccess() && !tabelle.getContent().gibCode().equals(pCode)) {
      tabelle.next();
    }
    if(tabelle.hasAccess()) {
      return tabelle.getContent().gibZeichen();
    } else {
      return Character.MIN_VALUE;
    }
  }
  public String gibCode(char  pZeichen){
    tabelle.toFirst();
    while(tabelle.getContent() != null && tabelle.getContent().gibZeichen() != pZeichen && tabelle.hasAccess()) {
      tabelle.next();
    }
    if(tabelle.hasAccess()) {
      return tabelle.getContent().gibCode();
    } else {
      return null;
    }
  }
  public List<Code> gibTabelle() {
    return tabelle;
  }
  public void hinzufuegen(String  pCode, char  pZeichen){
    tabelle.append(new Code(pZeichen,pCode));
  }
}

