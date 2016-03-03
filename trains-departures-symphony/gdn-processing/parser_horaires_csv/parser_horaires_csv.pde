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

  // combien on a stocké d’horaires
  // println("nombre d’horaires stockés : " + lstData.length);

  // le contenu du premier horaire stocké : 
  // println(lstData[0].type + "\t" + lstData[0].numero + "\t"+ lstData[0].destination + "\t"+ lstData[0].horaire);

  // le contenu du dernier horaire stocké : 
  // println(lstData[lstData.length-1].type + "\t" + lstData[lstData.length-1].numero + "\t"+ lstData[lstData.length-1].destination + "\t"+ lstData[lstData.length-1].horaire);
}


/**
 *  charge la page html
 */
void load_page()
{
  String[] lignes = loadStrings("https://www.horairetrain.net/horaires-gare-parisnord.html");
  rawPage = join(lignes, "");
  //println(lignes);
}


/**
 *  parse le code de la page
 */
void parse_page()
{
  // on coupe la fin du code (qui ne nous interesse pas)
  int indexFin = rawPage.indexOf("<div id=\"footer\">");
  int indexDebut = rawPage.indexOf("<th>Fiche horaire</th>");
  String rawTableau = rawPage.substring(indexDebut, indexFin);

  // on separe chaque ligne du tableau
  String[] lignesRaw1 = split(rawTableau, "<tr"); 

  // on crée la liste qui va accueillir les données parsées
  lstData = new GareData[lignesRaw1.length-1];

  // parcourt chacune de ces lignes
  for (int i=1; i<lignesRaw1.length; i++) {
    // casse la ligne au niveau des "<td"
    String[] ligneRaw2 = split(lignesRaw1[i], "<td");
    // on parse la ligne et on l’ajoute dans la liste des données parsées
    lstData[i-1] = parse_ligne(ligneRaw2);
  }
}


/**
 *  convertit une ligne brute du tableau d’origine en objet de données “propres”
 */
GareData parse_ligne(String[] ligne)
{
  // NUMERO de TRAIN
  println(ligne[1]);
  int indexDebutNumeroTrain = ligne[1].indexOf("valign=\"top\">")+13;
  int indexFinNumeroTrain = ligne[1].indexOf("<div class=\"train-category\">");
  String NumeroTrain = ligne[1].substring(indexDebutNumeroTrain, indexFinNumeroTrain);
  println(ligne[1]);

  // TYPE DE TRAIN
  int indexDebutTypeTrain = ligne[1].indexOf("train-category")+16;
  int indexFinTypeTrain = ligne[1].indexOf("</div>");
  String TypeTrain = ligne[1].substring(indexDebutTypeTrain, indexFinTypeTrain);
  println(ligne[1]);

  // HEURE DU TRAIN
  int indexDebutHeureTrain = ligne[2].indexOf("\"top\">")+6;
  int indexFinHeureTrain = ligne[2].length()-5;
  String HeureTrain = ligne[2].substring(indexDebutHeureTrain, indexFinHeureTrain);

  // DESTINATION DU TRAIN
  int indexDebutDestTrain = ligne[3].indexOf("html\">")+6;
  int indexFinDestTrain = ligne[3].length()-9;
  String DestTrain = ligne[3].substring(indexDebutDestTrain, indexFinDestTrain);
  // cas particulier : les destiniations qui n’ont pas de lien vers la fiche horaire
  if (DestTrain.indexOf("valign=\"top\">") != -1) {
    indexDebutDestTrain = ligne[3].indexOf("\"top\">")+6;
    indexFinDestTrain = ligne[3].length()-5;
  }
  DestTrain = ligne[3].substring(indexDebutDestTrain, indexFinDestTrain);  
  return new GareData(NumeroTrain, TypeTrain, HeureTrain, DestTrain);
}

void table_csv()
{
  table = new Table();

  table.addColumn("Type");
  table.addColumn("Numéro");
  table.addColumn("Destination");
  table.addColumn("Horaire");

  for (int i = 0; i < lstData.length; i++) {
    TableRow newRow = table.addRow();
    newRow.setString("Type", lstData[i].type);
    newRow.setString("Numéro", lstData[i].numero);
    newRow.setString("Destination", lstData[i].destination);
    newRow.setString("Horaire", lstData[i].horaire);
  }

  int sec = second();
  int min = minute();
  int h = hour();
  int d = day();
  int m = month();
  int y = year();

  saveTable(table, "data/horaires_gdn_"+y+"-"+m+"-"+d+"_"+h+"."+min+"."+sec+".csv");
  exit();
}

void draw()
{
}

