public class Huffman 
{
  //Methode für die Komprimierung eines Textes
  static public String komprimieren(String  pText){
    BinaryTree<HuffmanInhalt> wurzel = new BinaryTree<HuffmanInhalt>(new HuffmanInhalt()); //Spätere Wurzel des Codebaums
    List<BinaryTree<HuffmanInhalt>> liste = new List<BinaryTree<HuffmanInhalt>>(); //Liste der Teilbäume

    /* 
     * Teil für die Häufigkeitsanalyse
     * (Speichert Buchstaben mit ihrer Wertigkeit in Binärbäumen in der Liste)
     */
    for(int i = 0; i < pText.length(); i++) {
      liste.toFirst();
      while(liste.hasAccess() && liste.getContent().getContent().gibZeichen() != pText.charAt(i)) {
          liste.next();
      }
      if(!liste.hasAccess()) {
        liste.append(new BinaryTree<HuffmanInhalt>(new HuffmanInhalt(pText.charAt(i), 1)));
      } else {
        liste.getContent().getContent().wertErhoehen();
      }
    }

    /*
     * Teil um die Binärbäume in einem gemeinsamen Baum zusammenzufassen
     * (Nimmt immer die beiden kleinsten Binärbäume, um sie in einen Binärbaum zu verlagern -
     * beide Teilbäume werden aus der Liste gelöscht und durch den neuen Baum ersetzt)
     */
    int anzahl = liste.count();  //Anzahl der verschiedenen Buchstaben
    boolean flag = true; //Abbruchbedingung
    int zo = 1; //Wert der zwischen 1 und 0 wechselt um anzugeben, welcher der beiden Teilbäume gesetzt wird
    BinaryTree<HuffmanInhalt> aktuellerBaum = new BinaryTree<HuffmanInhalt>(); //Baum dem untergeordnete Bäume zugewiesen werden
    int kleinster; //Stelle in der Liste mit dem Binärbaum bin der geringsten Wertigkeit
    int kleinsterWert = 6205; //Geringste Wertigkeit
    if(anzahl > 2) {
      while(flag) {
        flag = false;
        kleinster = -1;
        liste.toFirst();

        //Findet den Binärbaum mit der geringsten Wertigkeit
        for(int i = 0; i < liste.count(); i++) {
          if (liste.getContent() != null && (kleinster == -1 || liste.getContent().getContent().gibWert() < kleinsterWert)) {
            kleinster = i;
            kleinsterWert = liste.getContent().getContent().gibWert();
          }
          liste.next();
        }

        //Prüft ob ein neuer übergeordneter Baum erstellt werden muss und schaltet den Indikator (zo) um
        if(zo == 1) {
          zo = 0;
          aktuellerBaum = new BinaryTree<HuffmanInhalt>(new HuffmanInhalt());
        } else {
          zo = 1;
        }
        
        //Fügt in den neuen Baum ein
        if(zo == 0) {
          //Findet den Teilbaum mit der geringsten Wertigkeit über den Index kleinster wieder
          liste.toFirst();
          for(int i = 0; i < kleinster; i++) {
            liste.next();
          }
          BinaryTree<HuffmanInhalt> kleinstesZeichen = liste.getContent();

          liste.remove(); //Entfernt diesen Binärbaum aus der Liste
          aktuellerBaum.setLeftTree(kleinstesZeichen); //Setzt den Baum anstattdessen als linken Teilbaum des neuen Baumes
          aktuellerBaum.getContent().erhoeheWertUm(kleinstesZeichen.getContent().gibWert()); //Überträgt die Wertigkeit des Teilbaumes auf den neuen Baum
        } else {
          //Findet den Teilbaum mit der geringsten Wertigkeit über den Index kleinster wieder
          liste.toFirst();
          for(int i = 0; i < kleinster; i++) {
            liste.next();
          }
          BinaryTree<HuffmanInhalt> kleinstesZeichen = liste.getContent();

          liste.remove(); //Entfernt diesen Binärbaum aus der Liste
          aktuellerBaum.setRightTree(kleinstesZeichen); //Setzt den Baum anstattdessen als rechten Teilbaum des neuen Baumes
          aktuellerBaum.getContent().erhoeheWertUm(kleinstesZeichen.getContent().gibWert()); //Überträgt die Wertigkeit des Teilbaumes auf den neuen Baum
          liste.append(aktuellerBaum); //Fügt den neuen Baum in die Liste ein, da er jetzt vollständig ist
        }

        /*
         * Testet ob ein weiterer Durchlauf nötig ist, also noch mehr als zwei freie Bäume vorhanden
         * sind, oder genau zwei, allerdings während schon ein neuer Baum begonnen wurde
         */
        if (liste.count() > 2 || liste.count() == 2 && zo == 0) {
          flag = true;
        }
      }
    }

    //Fügt die beiden verbleibenden Teilbäume an die Wurzel an
    liste.toFirst();
    wurzel.setLeftTree(liste.getContent());
    liste.next();
    wurzel.setRightTree(liste.getContent());

    //Erstellt eine Codetabelle
    Codetabelle codes = new Codetabelle();
    codesAuswerten(wurzel, codes, "");

    //Findet die Länge des längsten Codes heraus
    int laenge = 0; //Länge des längsten Codes (Für den Header)
    codes.gibTabelle().toFirst();
    while(codes.gibTabelle().hasAccess()) {
      if(codes.gibTabelle().getContent().gibCode().length() > laenge) {
        laenge = codes.gibTabelle().getContent().gibCode().length();
      }
      codes.gibTabelle().next();
    }

    //Erstellung des Headers
    String header = ""; //Variabel für den Header
    String binlongest = Integer.toBinaryString(laenge); //Binäre Darstellung der längsten Codewortlänge

    /*
     * Fügt für jede Stelle, bis auf die letzte, der binären Länge eine 0 und dann eine 1 in den Header ein
     * (Das ermöglicht es die Länge der Codelängen im Header anzugeben ohne sich auf eine bestimmte Länge
     * zu beschränken)
     */
    for(int i = 1; i < binlongest.length(); i++) {
      header += "0";
    }
    header += "1";

    //Übertragung der Codetabelle in den Header
    codes.gibTabelle().toFirst();
    while(codes.gibTabelle().hasAccess()) {
        //Angabe der Länge des folgenden Codes
        //Füllt die Länge auf um eine standartisierte Länge zu erreichen
        for(int j = 0; j < binlongest.length() - Integer.toBinaryString(codes.gibTabelle().getContent().gibCode().length()).length(); j++) {
          header += "0";
        }
        header += Integer.toBinaryString(codes.gibTabelle().getContent().gibCode().length()); //Fügt die Länge binär an

        //Angabe des Codes
        header += codes.gibTabelle().getContent().gibCode();

        //Angabe des dazugehörigen Zeichens
        //Füllt die Länge auf um eine standartisierte Länge zu erreichen
        for(int j = 0; j < 8 - Integer.toBinaryString((int) codes.gibTabelle().getContent().gibZeichen()).length(); j++) {
          header += "0";
        }
        header += Integer.toBinaryString((int) codes.gibTabelle().getContent().gibZeichen()); //Fügt die binäre Darstellung des ASCII-Codes des Zeichens ein

        codes.gibTabelle().next();
    }

    //Gibt das Ende des Headers über die Länge 0 an, was eine Angabe der Länge des gesamten Headers umgeht
    for(int i = 0; i < binlongest.length(); i++) {
      header += "0";
    }

    //Kodiert den Ausgangstext über die Codetabelle
    String finalerText = "";
    for (int i = 0; i < pText.length(); i++) {
      finalerText += codes.gibCode(pText.charAt(i));
    }

    //Gibt den header mit angefügtem kodierten Text aus
    return header + finalerText;
  }

  //Hilfsmethode um die Codetabelle zu erstellen (rekursiv)
  static private void codesAuswerten(BinaryTree<HuffmanInhalt> pBaum, Codetabelle pCodetab, String pCode) {
    if(!pBaum.isEmpty()) {
      if(pBaum.getContent().gibZeichen() != Character.MIN_VALUE) {
        pCodetab.hinzufuegen(pCode, pBaum.getContent().gibZeichen()); //Fügt Zeichen ein, falls gefunden
      } else {
        codesAuswerten(pBaum.getLeftTree(), pCodetab, pCode + "0"); //Testet den linken Teilbaum mit einem um eine 0 erweiterten Code
        codesAuswerten(pBaum.getRightTree(), pCodetab, pCode + "1"); //Testet den rechten Teilbaum mit einem um eine 1 erweiterten Code
      }
    }
  }

  //Methode für die Dekomprimierung eines Textes
  static public String dekomprimieren(String  pText){
    int laengenl = 0; //Länge der Längenangaben
    int i = 0; //Stelle im kodierten Text

    //Wertet den ersten Teil des Headers über die Länge der Längenangaben aus
    while(pText.charAt(i) == '0') {
      i++;
    }
    laengenl = i + 1; //Speichert den gewonnenen Wert

    //Erstellt über die Längenangabe ein Schema für die Markierung des Headerendes
    String headerEnde = "";
    for(int j = 0; j < laengenl; j++) {
      headerEnde += "0";
    }

    //Wertet die im Header angegebene Codetabelle aus
    boolean flag = true; //Abbruchbedingung
    String codelaenge = ""; //Länge des aktuellen Codes
    String code = ""; //Aktueller Code
    Codetabelle zeichen = new Codetabelle(); //Codetabelle um alle Zeichen zu speichern
    while(flag) {
      code = "";
      codelaenge = "";

      //Erfasst die Codelänge
      for(int j = 0; j < laengenl; j++) {
        i++;
        if(i < pText.length()) {
          codelaenge += pText.charAt(i);
        }
      }

      //Code in Tabelle aufnehmen, sonst Abbruch
      if(codelaenge != headerEnde && codelaenge != "" && Integer.parseInt(codelaenge,2) != 0) { //Überprüft ob die Codelänge das Headerende markiert

        //Erfasst den Code
        for(int j = 0; j < Integer.parseInt(codelaenge, 2); j++) {
          i++;
          code += pText.charAt(i);
        }

        //Erfasst den binären ASCII-Wert des Zeichens
        String charNum = "";
        for(int j = 0; j < 8; j++) {
          i++;
          if(i < pText.length()) {
            charNum += pText.charAt(i);
          }
        }
        zeichen.hinzufuegen(code, (char) Integer.parseInt(charNum,2)); //Fügt das Zeichen in die Tabelle ein
      } else {
        flag = false;
      }
    }

    //Dekodiert den kodierten Text mit der erstellten Codetabelle
    i++;
    String dektext = ""; //Dekodierter Text
    code = "";
    for(int j = i; j < pText.length(); j++) {
      code += pText.charAt(j);

      //Guckt, ob ein Zeichen mit diesem Code existiert, fügt ansonsten eine weitere Stelle an diesen an
      if(zeichen.gibZeichen(code) != Character.MIN_VALUE) {
        //Fügt das Zeichen über den Code ein und setzt diesen zurück
        dektext += zeichen.gibZeichen(code);
        code = "";
      }
    }
    return dektext;
  }

  //Main-Methode, um die Funktionsweise beim Ausführen zu testen
  public static void main(String[] args) {
    System.out.println(dekomprimieren(komprimieren("Testtext1hinundherXYSonderzeichen!\"$²³³²³³³³")) + " (Testtext1hinundherXYSonderzeichen!\"$²³³²³³³³)");
    System.out.println(komprimieren("Testtext1hinundherXYSonderzeichen!\"$²³³²³³³³") + " (ASCII-Länge: " + "Testtext1hinundherXYSonderzeichen!\"$²³³²³³³³".length() * 8 + " | Huffman-Länge: " + komprimieren("Testtext1hinundherXYSonderzeichen!\"$²³³²³³³³").length() + ")");
    System.out.println(dekomprimieren(komprimieren("Steffen Molitor Facharbeit")) + " (Steffen Molitor Facharbeit)");
    System.out.println(komprimieren("Steffen Molitor Facharbeit") + " (ASCII-Länge: " + "Steffen Molitor Facharbeit".length() * 8 + " | Huffman-Länge: " + komprimieren("Steffen Molitor Facharbeit").length() + ")");
    System.out.println(dekomprimieren(komprimieren("Huffman-Komprimierung")) + " (Huffman-Komprimierung)");
    System.out.println(komprimieren("Huffman-Komprimierung") + " (ASCII-Länge: " + "Huffman-Komprimierung".length() * 8 + " | Huffman-Länge: " + komprimieren("Huffman-Komprimierung").length() + ")");
    System.out.println(dekomprimieren(komprimieren("LangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmet")) + " (LangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmet)");
    System.out.println(komprimieren("LangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmet") + " (ASCII-Länge: " + "LangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmet".length() * 8 + " | Huffman-Länge: " + komprimieren("LangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmetLangerlangerlangerTextZumTestenDerLängeLoremIpsumDolorSedAmet").length() + ")");
  }
}