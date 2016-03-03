/**
*  sert à stocker les données extraites de la page http://www.gares-en-mouvement.com/fr/frpno/horaires-temps-reel/arr/
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
