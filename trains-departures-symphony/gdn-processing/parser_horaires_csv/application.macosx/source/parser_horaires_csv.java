import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class parser_horaires_csv extends PApplet {

// la page "brute"
String rawPage;
Table table;

// la liste qui contient les donnees parsee
GareData[] lstData;


public void setup()
{
  size(500, 500);
  load_page();
  parse_page();
  table_csv();

  // combien on a stocke d'horaires
  // println("nombre d'horaires stock\u00e9s : " + lstData.length);

  // le contenu du premier horaire stock\u00e9 : 
  // println(lstData[0].type + "\t" + lstData[0].numero + "\t"+ lstData[0].destination + "\t"+ lstData[0].horaire);

  // le contenu du dernier horaire stock\u00e9 : 
  // println(lstData[lstData.length-1].type + "\t" + lstData[lstData.length-1].numero + "\t"+ lstData[lstData.length-1].destination + "\t"+ lstData[lstData.length-1].horaire);
}


/**
 *  charge la page html
 */
public void load_page()
{
  String[] lignes = loadStrings("http://www.horairetrain.net/horaires-gare-parisnord.html");
  rawPage = join(lignes, "");
}


/**
 *  parse le code de la page
 */
public void parse_page()
{
  // on coupe la fin du code (qui ne nous interesse pas)
  int indexFin = rawPage.indexOf("<div id=\"footer\">");
  int indexDebut = rawPage.indexOf("<th>Fiche horaire</th>");
  String rawTableau = rawPage.substring(indexDebut, indexFin);

  // on separe chaque ligne du tableau
  String[] lignesRaw1 = split(rawTableau, "<tr"); 

  // on cree la liste qui va accueillir les donnees parsees
  lstData = new GareData[lignesRaw1.length-1];

  // parcourt chacune de ces lignes
  for (int i=1; i<lignesRaw1.length; i++) {
    // casse la ligne au niveau des "<td"
    String[] ligneRaw2 = split(lignesRaw1[i], "<td");
    // on parse la ligne et on l'ajoute dans la liste des donnees parsees
    lstData[i-1] = parse_ligne(ligneRaw2);
  }
}


/**
 *  convertit une ligne brute du tableau d'origine en objet de donnees "propres"
 */
public GareData parse_ligne(String[] ligne)
{
  // NUMERO de TRAIN
  int indexDebutNumeroTrain = ligne[1].indexOf("valign=\"top\">")+13;
  int indexFinNumeroTrain = ligne[1].indexOf("<br>");
  String NumeroTrain = ligne[1].substring(indexDebutNumeroTrain, indexFinNumeroTrain);

  // TYPE DE TRAIN
  int indexDebutTypeTrain = ligne[1].indexOf("10px\">")+6;
  int indexFinTypeTrain = ligne[1].indexOf("</span>");
  String TypeTrain = ligne[1].substring(indexDebutTypeTrain, indexFinTypeTrain);

  // HEURE DU TRAIN
  int indexDebutHeureTrain = ligne[2].indexOf("\"top\">")+6;
  int indexFinHeureTrain = ligne[2].length()-5;
  String HeureTrain = ligne[2].substring(indexDebutHeureTrain, indexFinHeureTrain);

  // DESTINATION DU TRAIN
  int indexDebutDestTrain = ligne[3].indexOf("html\">")+6;
  int indexFinDestTrain = ligne[3].length()-9;
  String DestTrain = ligne[3].substring(indexDebutDestTrain, indexFinDestTrain);
  // cas particulier : les destiniations qui n'ont pas de lien vers la fiche horaire
  if (DestTrain.indexOf("valign=\"top\">") != -1) {
    indexDebutDestTrain = ligne[3].indexOf("\"top\">")+6;
    indexFinDestTrain = ligne[3].length()-5;
  }
  DestTrain = ligne[3].substring(indexDebutDestTrain, indexFinDestTrain);  
  return new GareData(NumeroTrain, TypeTrain, HeureTrain, DestTrain);
}

public void table_csv()
{
  table = new Table();

  table.addColumn("Type");
  table.addColumn("Num\u00e9ro");
  table.addColumn("Destination");
  table.addColumn("Horaire");

  for (int i = 0; i < lstData.length; i++) {
    TableRow newRow = table.addRow();
    newRow.setString("Type", lstData[i].type);
    newRow.setString("Num\u00e9ro", lstData[i].numero);
    newRow.setString("Destination", lstData[i].destination);
    newRow.setString("Horaire", lstData[i].horaire);
  }

  int sec = second();
  int min = minute();
  int h = hour();
  int d = day();
  int m = month();
  int y = year();

  saveTable(table, "/Users/timotheegoguely/Dropbox/\u00c9SAD/DNSEP/Projet/Gare du Nord/Data/parser_horaires_csv/data/horaires_gdn_"+y+"-"+m+"-"+d+"_"+h+"."+min+"."+sec+".csv");
  exit();
}

public void draw()
{
}


/**
*  sert \u00e0 stocker les donnees extraites de la page http://www.horairetrain.net/horaires-gare-parisnord.html
*/
class GareData
{
  String numero;
  String type;
  String horaire;
  String destination;
  
  /**
  *  constructor
  */
  GareData(String n, String t, String h, String d)
  {
    numero = n;
    type = t;
    horaire = h;
    destination = d;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "parser_horaires_csv" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
