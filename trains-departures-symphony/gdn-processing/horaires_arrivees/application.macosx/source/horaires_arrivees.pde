// la page "brute"
String rawPage;
Table table;

// la liste qui contient les donnees parsee
GareData[] lstData;


void setup()
{
  size(500, 500);
  load_page();
  parse_page();
  table_csv();

  // combien on a stocke d'horaires
  //println("nombre d’horaires stockés : " + lstData.length);

  // le contenu du premier horaire stocké : 
  //println(lstData[0].type + "\t" + lstData[0].num + "\t"+ lstData[0].heure + "\t"+ lstData[0].destination + "\t"+ lstData[0].information + "\t"+ lstData[0].voie);

  // le contenu du dernier horaire stocké : 
  //println(lstData[lstData.length-1].type + "\t" + lstData[lstData.length-1].num + "\t"+ lstData[lstData.length-1].heure + "\t"+ lstData[lstData.length-1].destination + "\t"+ lstData[lstData.length-1].information + "\t"+ lstData[lstData.length-1].voie);
}


/**
 *  charge la page html
 */
void load_page()
{
  String[] lignes = loadStrings("http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/arr/");
  rawPage = join(lignes, "");
}


/**
 *  parse le code de la page
 */
void parse_page()
{
  // on coupe le début et la fin du code (qui ne nous interesse pas)
  int indexDebut = rawPage.indexOf("<tbody>");
  int indexFin = rawPage.indexOf("</tbody>");
  String rawTableau = rawPage.substring(indexDebut, indexFin);

  // remplace tous les "&eacute;" par des "é"
  //          tous les "&Eacute;" par des "É"
  //          tous les "<abbr title="heure">h</abbr>" par des "h"
  //       et tous les "<abbr title="minute">mn</abbr>" par des "mn"
  rawTableau = rawTableau.replaceAll("&eacute;", "é");
  rawTableau = rawTableau.replaceAll("&Eacute;", "É");
  rawTableau = rawTableau.replaceAll("<abbr title=\"heure\">h</abbr>", "h");
  rawTableau = rawTableau.replaceAll("<abbr title=\"minute\">mn</abbr>", "mn");
  
  // on separe chaque ligne du tableau
  String[] lignesRaw1 = split(rawTableau, "<tr"); 

  // on crée la liste qui va accueillir les données parseées
  lstData = new GareData[lignesRaw1.length-1];

  // parcourt chacune de ces lignes
  for (int i=1; i<lignesRaw1.length; i++) {
    // casse la ligne au niveau des "<td"
    String[] ligneRaw2 = split(lignesRaw1[i], "<td");
    // on parse la ligne et on l’ajoute dans la liste des données parseées
    lstData[i-1] = parse_ligne(ligneRaw2);
  }
}


/**
 *  convertit une ligne brute du tableau d’origine en objet de données “propres”
 */
GareData parse_ligne(String[] ligne)
{
  // Type
  int indexDebutTypeTrain = ligne[2].indexOf("train_id")+12;
  int indexFinTypeTrain = ligne[2].indexOf("</td>"); // .length()-5; = .indexOf("</td>");
  String TypeTrain = ligne[2].substring(indexDebutTypeTrain, indexFinTypeTrain);
  // cas particulier lorsque le numéro après train_id_ contient deux chiffres
  // println(ligne[2].indexOf("\">")); = 53, puis 54 à partir du dixième id
  if (ligne[2].indexOf("\">") > 53 ) {
    indexDebutTypeTrain = ligne[2].indexOf("train_id")+13;
    indexFinTypeTrain = ligne[2].indexOf("</td>");
  }
  TypeTrain = ligne[2].substring(indexDebutTypeTrain, indexFinTypeTrain);

  // Numéro
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

void table_csv()
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

  saveTable(table, "/Users/timotheegoguely/Dropbox/ÉSAD/DNSEP/Projet/Gare du Nord/Data/horaires_arrivees/data/horaires_arrivees_"+y+"-"+m+"-"+d+"_"+h+"."+min+"."+sec+".csv");
  
  exit();
}

void draw()
{
}

