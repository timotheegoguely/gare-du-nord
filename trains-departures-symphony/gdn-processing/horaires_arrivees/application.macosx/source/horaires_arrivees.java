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

public class horaires_arrivees extends PApplet {

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
  //println("nombre d\u2019horaires stock\u00e9s : " + lstData.length);

  // le contenu du premier horaire stock\u00e9 : 
  //println(lstData[0].type + "\t" + lstData[0].num + "\t"+ lstData[0].heure + "\t"+ lstData[0].destination + "\t"+ lstData[0].information + "\t"+ lstData[0].voie);

  // le contenu du dernier horaire stock\u00e9 : 
  //println(lstData[lstData.length-1].type + "\t" + lstData[lstData.length-1].num + "\t"+ lstData[lstData.length-1].heure + "\t"+ lstData[lstData.length-1].destination + "\t"+ lstData[lstData.length-1].information + "\t"+ lstData[lstData.length-1].voie);
}


/**
 *  charge la page html
 */
public void load_page()
{
  String[] lignes = loadStrings("http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/arr/");
  rawPage = join(lignes, "");
}


/**
 *  parse le code de la page
 */
public void parse_page()
{
  // on coupe le d\u00e9but et la fin du code (qui ne nous interesse pas)
  int indexDebut = rawPage.indexOf("<tbody>");
  int indexFin = rawPage.indexOf("</tbody>");
  String rawTableau = rawPage.substring(indexDebut, indexFin);

  // remplace tous les "&eacute;" par des "\u00e9"
  //          tous les "&Eacute;" par des "\u00c9"
  //          tous les "<abbr title="heure">h</abbr>" par des "h"
  //       et tous les "<abbr title="minute">mn</abbr>" par des "mn"
  rawTableau = rawTableau.replaceAll("&eacute;", "\u00e9");
  rawTableau = rawTableau.replaceAll("&Eacute;", "\u00c9");
  rawTableau = rawTableau.replaceAll("<abbr title=\"heure\">h</abbr>", "h");
  rawTableau = rawTableau.replaceAll("<abbr title=\"minute\">mn</abbr>", "mn");
  
  // on separe chaque ligne du tableau
  String[] lignesRaw1 = split(rawTableau, "<tr"); 

  // on cr\u00e9e la liste qui va accueillir les donn\u00e9es parse\u00e9es
  lstData = new GareData[lignesRaw1.length-1];

  // parcourt chacune de ces lignes
  for (int i=1; i<lignesRaw1.length; i++) {
    // casse la ligne au niveau des "<td"
    String[] ligneRaw2 = split(lignesRaw1[i], "<td");
    // on parse la ligne et on l\u2019ajoute dans la liste des donn\u00e9es parse\u00e9es
    lstData[i-1] = parse_ligne(ligneRaw2);
  }
}


/**
 *  convertit une ligne brute du tableau d\u2019origine en objet de donn\u00e9es \u201cpropres\u201d
 */
public GareData parse_ligne(String[] ligne)
{
  // Type
  int indexDebutTypeTrain = ligne[2].indexOf("train_id")+12;
  int indexFinTypeTrain = ligne[2].indexOf("</td>"); // .length()-5; = .indexOf("</td>");
  String TypeTrain = ligne[2].substring(indexDebutTypeTrain, indexFinTypeTrain);
  // cas particulier lorsque le num\u00e9ro apr\u00e8s train_id_ contient deux chiffres
  // println(ligne[2].indexOf("\">")); = 53, puis 54 \u00e0 partir du dixi\u00e8me id
  if (ligne[2].indexOf("\">") > 53 ) {
    indexDebutTypeTrain = ligne[2].indexOf("train_id")+13;
    indexFinTypeTrain = ligne[2].indexOf("</td>");
  }
  TypeTrain = ligne[2].substring(indexDebutTypeTrain, indexFinTypeTrain);

  // Num\u00e9ro
  int indexDebutNumTrain = ligne[3].indexOf("numero_id\">")+11;
  int indexFinNumTrain = ligne[3].length()-5;
  String NumTrain = ligne[3].substring(indexDebutNumTrain, indexFinNumTrain);

  // Heure
  int indexDebutHeureTrain = ligne[4].indexOf("heure_id\">")+10;
  int indexFinHeureTrain = ligne[4].length()-5;
  String HeureTrain = ligne[4].substring(indexDebutHeureTrain, indexFinHeureTrain);

  // Destination
  int indexDebutDestTrain = ligne[5].indexOf("destination_id\">")+16;
  int indexFinDestTrain = ligne[5].length()-5;
  String DestTrain = ligne[5].substring(indexDebutDestTrain, indexFinDestTrain);
 
  // Information
  int indexDebutInfoTrain = ligne[6].indexOf("situation_id\">")+14;
  int indexFinInfoTrain = ligne[6].length()-5;
  String InfoTrain = ligne[6].substring(indexDebutInfoTrain, indexFinInfoTrain);
 
  // Voie
  int indexDebutVoieTrain = ligne[7].indexOf("voie_id\">")+9;
  int indexFinVoieTrain = ligne[7].indexOf("</td>");
  String VoieTrain = ligne[7].substring(indexDebutVoieTrain, indexFinVoieTrain); 
  
  return new GareData(TypeTrain, NumTrain, HeureTrain, DestTrain, InfoTrain, VoieTrain);
}

public void table_csv()
{
  table = new Table();

  table.addColumn("Type");
  table.addColumn("Num");
  table.addColumn("Heure");
  table.addColumn("Destination");
  table.addColumn("Information");
  table.addColumn("Voie");

  for (int i = 0; i < lstData.length; i++) {
    TableRow newRow = table.addRow();
    newRow.setString("Type", lstData[i].type);
    newRow.setString("Num", lstData[i].num);
    newRow.setString("Heure", lstData[i].heure);
    newRow.setString("Destination", lstData[i].destination);
    newRow.setString("Information", lstData[i].information);
    newRow.setString("Voie", lstData[i].voie);
  }

  int sec = second();
  int min = minute();
  int h = hour();
  int d = day();
  int m = month();
  int y = year();

  saveTable(table, "/Users/timotheegoguely/Dropbox/\u00c9SAD/DNSEP/Projet/Gare du Nord/Data/horaires_arrivees/data/horaires_arrivees_"+y+"-"+m+"-"+d+"_"+h+"."+min+"."+sec+".csv");
  
  exit();
}

public void draw()
{
}

/**
*  sert \u00e0 stocker les donn\u00e9es extraites de la page http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/arr/
*/
class GareData
{
  String type;
  String num;
  String heure;
  String destination;
  String information;
  String voie;
  
  /**
  *  constructor
  */
  GareData(String t, String n, String h, String d, String i, String v)
  {
    type = t;
    num = n;
    heure = h;
    destination = d;
    information = i;
    voie = v;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "horaires_arrivees" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
