
/**
*  sert à stocker les donnees extraites de la page http://www.horairetrain.net/horaires-gare-parisnord.html
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
