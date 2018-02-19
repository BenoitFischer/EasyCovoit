package fr.toulon.seatech.easycovoit;

/**
 * Created by akmal on 15/02/2018.
 */

public class InfoTrajet {
    private String strDepart, strArrivee, strHeure;
    public InfoTrajet() {

    }

    public InfoTrajet(String str1, String str2, String str3) {
        this.strHeure=str1;
        this.strArrivee=str2;
        this.strDepart=str3;
    }

    public String getStrDepart() {
        return strDepart;
    }

    public void setStrDepart(String strDepart) {
        this.strDepart = strDepart;
    }

    public String getStrArrivee() {
        return strArrivee;
    }

    public void setStrArrivee(String strArrivee) {
        this.strArrivee = strArrivee;
    }

    public String getStrHeure() {
        return strHeure;
    }

    public void setStrHeure(String strHeure) {
        this.strHeure = strHeure;
    }

}
