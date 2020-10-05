package Palveluhallinta;

import toimintaalue.ToimintaAlue;
import yleisetTyokalut.Tietokantayhteys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Tässä luokassa hallinnoidaan Palvelu kokonaisuutta.
 *
 * @author Severi Moisio
*/
public class Palvelu{
    private int palveluId;
    private String palveluNimi;
    private String kuvaus;
    private double hinta;
    private ToimintaAlue toimintaAlue;

    public Palvelu(ToimintaAlue toimintaAlue) {
        this.toimintaAlue = toimintaAlue;
    }

    public Palvelu(String palveluNimi, String kuvaus, double hinta, ToimintaAlue toimintaAlue) {
        this.palveluNimi = palveluNimi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.toimintaAlue = toimintaAlue;
    }

    public Palvelu(int palveluId, String palveluNimi, String kuvaus, double hinta, ToimintaAlue toimintaAlue) {
        this.palveluId = palveluId;
        this.palveluNimi = palveluNimi;
        this.kuvaus = kuvaus;
        this.hinta = hinta;
        this.toimintaAlue = toimintaAlue;
    }

    public Palvelu() {
    }

    public ToimintaAlue getToimintaAlue() {
        return toimintaAlue;
    }

    public void setToimintaAlue(ToimintaAlue toimintaAlue) {
        this.toimintaAlue = toimintaAlue;
    }

    public int getPalveluId() {
        return palveluId;
    }

    public void setPalveluId(int palveluId) {
        this.palveluId = palveluId;
    }

    public String getPalveluNimi() {
        return palveluNimi;
    }

    public void setPalveluNimi(String nimi) {
        this.palveluNimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public double getHinta() {
        return hinta;
    }

    public void setHinta(double hinta) {
        this.hinta = hinta;
    }
    /**
     * Palauttaa listan Palvelu-olioita. Hakee tietokannasta koko palveluhallinta näkymän sisällön.
     *
     * @return Lista Palvelu-olioista tietokannassa.
     */
    public ArrayList<Palvelu> haePalvelut() {
        ArrayList<Palvelu> palvelut = new ArrayList<>(); //Lista ToimintaAlue-olioita, joka palautetaan lopuksi
        try {
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana); // Yhdistetään tietokantaan
            Statement stmt = con.createStatement(); // Muodostetaan hakulause
            ResultSet rs = stmt.executeQuery("select * from palveluhallinta"); // Suoritetaan kysely

            // Muodostetaan haetuista riveistä Palvelu-olioita ja lisätään ne listaan
            while (rs.next()) {
                ToimintaAlue ta = new ToimintaAlue(rs.getInt(1),rs.getString(2));
                Palvelu p = new Palvelu(rs.getInt(3), rs.getString(4),
                        rs.getString(5),rs.getDouble(6), ta);
                palvelut.add(p);
            }
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return palvelut;
    }

    /**
     * Metodi hakee annetulla parametrillä yhden palvelun ja asettaa arvot olioon.
     *
     * @param nimi haettava palvelun nimi
     * @return palauttaa true jos metodi löytää rivin kannasta ja saa lisättyä tiedot olioon. Muuten false.
     */
    public boolean haePalvelu(String nimi) {
        boolean loytyi = false;
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from palveluhallinta where pNimi = '" + nimi + "';"); // Suoritetaan kysely
            // mennään palautetulle riville ja asetetaan arvot olioon
            while (rs.next()) {
                toimintaAlue.setToimintaalue_id(rs.getInt(1));
                toimintaAlue.setNimi(rs.getString(2));
                setPalveluId(rs.getInt(3));
                setPalveluNimi(rs.getString(4));
                setKuvaus(rs.getString(5));
                setHinta(rs.getDouble(6));
                loytyi = true;
            }
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return loytyi;
    }

    /**
     * Metodi hakee annetulla parametrillä yhden palvelun ja asettaa arvot olioon.
     *
     * @param id haettava palvelun nimi
     * @return palauttaa true jos metodi löytää rivin kannasta ja saa lisättyä tiedot olioon. Muuten false.
     */
    public boolean haePalvelu(int id) {
        boolean loytyi = false;
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from palveluhallinta where palvelu_id = " + id +";" ); // Suoritetaan kysely
            // mennään palautetulle riville ja asetetaan arvot olioon
            while (rs.next()) {
                toimintaAlue.setToimintaalue_id(rs.getInt(1));
                toimintaAlue.setNimi(rs.getString(2));
                setPalveluId(rs.getInt(3));
                setPalveluNimi(rs.getString(4));
                setKuvaus(rs.getString(5));
                setHinta(rs.getDouble(6));
                loytyi = true;
            }
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return loytyi;
    }

    /**
     * Metodi lisää olion tiedot tietokantaan palvelu tauluun.
     *
     * @return palauttaa lisättyjen rivien määtän
     */
    public int lisaaPalvelu() {
        // Muutettujen rivien lukumäärä
        int muutettu = 0;
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            muutettu = stmt.executeUpdate("insert into palvelu(toimintaalue_id, nimi, kuvaus, hinta)" +
                    " values(" + this.toimintaAlue.getToimintaalue_id() +", '"+ this.getPalveluNimi()+"', '"+
                    this.getKuvaus()+"', " + this.getHinta()+");"); // Suoritetaan lisäys
            this.haePalvelu(this.getPalveluNimi()); //asetetaan olioon tällä metodilla palvelu_id
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return muutettu;
    }

    /**
     * Metodi poistaa palvelun tiedot tietokannasta palveluid:n mukaan. Voi poistaa vain yhden kerralla.
     *
     * @return Palauttaa poistettujen palvelujen rivien määrät.
     */
    public int poistaPalvelu() {
        int muutettu = 0; // Muutettujen rivien lukumäärä
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            // Suoritetaan poisto id:n mukaan
            muutettu = stmt.executeUpdate("delete from palvelu where palvelu_id = " + this.getPalveluId()+";");
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return muutettu;
    }

    /**
     * Metodo ottaa olion vastaan ja päivittää sen vastinetta kannassa.
     *
     * @param uusip Olio, jossa on uuden palvelun tiedot
     * @return päivitettujen rivien määrä
     */
    public int paivitaPalvelu(Palvelu uusip) { // jäi kesken vielä
        int muutettu = 0; // Muutettujen rivien lukumäärä
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            // Suoritetaan päivitys
            muutettu = stmt.executeUpdate("update palvelu set nimi= '" + uusip.getPalveluNimi() + "', kuvaus = '" + uusip.getKuvaus()+
                    "', hinta = " + uusip.getHinta() + " where palvelu_id = " + this.getPalveluId());
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return muutettu;
    }

    /**
     * Metodi päivittää kannantietoja muutetun olion tiedoilla.
     *
     * @return päivitettujen rivien määrä
     */
    public int paivitaPalvelu() {
        int muutettu = 0; // Muutettujen rivien lukumäärä
        try {
            // Yhdistetään tietokantaan
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana);
            // Muodostetaan hakulause
            Statement stmt = con.createStatement();
            // Suoritetaan päivitys
            muutettu = stmt.executeUpdate("update palvelu set toimintaalue_id = "+ this.getToimintaAlue().getToimintaalue_id() +
                ", nimi= '" + this.getPalveluNimi() + "', kuvaus = '" + this.getKuvaus()+
                    "', hinta = " + this.getHinta() + " where palvelu_id = " + this.getPalveluId());
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            //joskus jotain tähä
        }
        return muutettu;
    }
}
