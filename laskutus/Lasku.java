package laskutus;

import Palveluhallinta.Palvelu;
import asiakas.Asiakas;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.itextpdf.text.Image;
import mokki.Mokki;
import posti.Posti;
import toimintaalue.ToimintaAlue;
import varaus.VarauksenPalvelu;
import varaus.Varaus;
import yleisetTyokalut.Tietokantayhteys;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.JFileChooser;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Tämä luokka sisältää laskujen toiminnallisuuden
 * ja niiden tietokantayhteydet.
 *
 * @author Lauri Klemettinen ja Severi Moisio
 */
public class Lasku {
    private int lasku_id;
    private Varaus varaus;
    private double summa;
    private double alv;
    private boolean maksettu;

    public Lasku() {
    }

    public Lasku(int lasku_id, Varaus varaus, double summa, double alv, boolean maksettu) {
        this.lasku_id = lasku_id;
        this.varaus = varaus;
        this.summa = summa;
        this.alv = alv;
        this.maksettu = maksettu;
    }

    public int getLasku_id() {
        return lasku_id;
    }

    public void setLasku_id(int lasku_id) {
        this.lasku_id = lasku_id;
    }

    public Varaus getVaraus() {
        return varaus;
    }

    public void setVaraus(Varaus varaus) {
        this.varaus = varaus;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }

    public double getAlv() {
        return alv;
    }

    public void setAlv(double alv) {
        this.alv = alv;
    }

    public boolean isMaksettu() {
        return maksettu;
    }

    public void setMaksettu(boolean maksettu) {
        this.maksettu = maksettu;
    }

    /**
     * Palauttaa listan laskuista tietokannassa.
     *
     * @return Lista Lasku-olioista tietokannassa
     */
    public static ArrayList<Lasku> haeKaikkiLaskut() {
        ArrayList<Lasku> laskut = new ArrayList<Lasku>();
        try {
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana); // Yhdistetään tietokantaan
            Statement stmt = con.createStatement(); // Muodostetaan hakulause
            ResultSet rs = stmt.executeQuery("select * from laskutus"); // Suoritetaan kysely laskutus näkymästä
            // Muodostetaan haetuista riveistä Lasku-olioita ja lisätään ne listaan
            while (rs.next()) {
                Lasku la = new Lasku();
                Varaus va = new Varaus();
                Asiakas a = new Asiakas();
                Mokki m = new Mokki();
                Posti p = new Posti();

                //laskuntiedot tulee eka
                la.setLasku_id(rs.getInt(1));
                va.setVaraus_id(rs.getInt(2));
                la.setSumma(rs.getDouble(3));
                la.setAlv(rs.getDouble(4));
                la.setMaksettu(rs.getBoolean(5));

                //asiakas id
                a.setAsiakasid(rs.getInt(6));

                //mökin id
                m.setMokki_id(rs.getInt(7));

                // alustetaan varaus
                /*
                va.setVarattu_pvm(rs.getDate(8));
                va.setVahvistus_pvm(rs.getDate(9));
                va.setVarattu_alkupvm(rs.getDate(10));
                va.setVarattu_loppupvm(rs.getDate(11));
                */
                if (rs.getDate(8) != null) {
                    va.setVarattu_pvm(new Date(rs.getDate(8).getTime()));
                }
                if (rs.getDate(9) != null) {
                    va.setVahvistus_pvm(new Date(rs.getDate(9).getTime()));
                }
                if (rs.getDate(10) != null) {
                    va.setVarattu_alkupvm(new Date(rs.getDate(10).getTime()));
                }
                if (rs.getDate(11) != null) {
                    va.setVarattu_loppupvm(new Date(rs.getDate(11).getTime()));
                }

                //asiakkaan postin alustus
                p.setPostinro(rs.getString(12));
                Posti p1 = p.haePosti(p.getPostinro());
                a.setPosti(p1); // asetetaan asiakkaan postitoimipaikka

                // alustetaan asiakkaan tiedot
                a.setEnimi(rs.getString(13));
                a.setSnimi(rs.getString(14));
                a.setLahiosoite(rs.getString(15));
                a.setEmail(rs.getString(16));
                a.setPuhelinnro(rs.getString(17));

                //asetetaan varaukselle asiakas
                va.setAsiakas(a);

                //alustetaan mökki
                p.setPostinro(rs.getString(18));
                Posti p2 = p.haePosti(p.getPostinro());
                m.setPosti(p2);
                m.setMnimi(rs.getString(19));
                m.setOsoite(rs.getString(20));
                m.setHinta(rs.getInt(21));

                // asetetaan varuksen mökki
                va.setMokki(m);
                // laskulle asetetaan varaus
                la.setVaraus(va);


                laskut.add(la);
            }
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            // Tässä voisi ilmoittaa käyttäjälle virheestä jotenkin
        }
        return laskut;
    }

    /**
     * Lisää laskun tietokantaan. Palauttaa lisättyjen rivien lukumäärän.
     *
     * @return Lisättyjen rivien lukumäärä
     */
    public int lisaa() {
        int lisatyt = 0;
        int maksettuLuku = 0;
        if (this.isMaksettu()) { // muutetaan boolean arvo luvuksi 0 tai 1
            maksettuLuku = 1;
        }
        try {
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana); // Yhdistetään tietokantaan
            Statement stmt = con.createStatement(); // Muodostetaan hakulause
            lisatyt = stmt.executeUpdate("insert into lasku(varaus_id, summa, alv, maksettu) " +
                    "values (" + this.getVaraus().getVaraus_id()
                    + ", " + this.getSumma()
                    + ", " + this.getAlv()
                    + ", " + maksettuLuku + ")");
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            // Tässä voisi ilmoittaa käyttäjälle virheestä jotenkin
        }
        return lisatyt;
    }

    /**
     * Päivittää laskun tiedot tietokannassa. Parametrina annetaan uusi lasku,
     * päivitys kohdistuu metodia kutsuvaan laskuun. Palauttaa päivitettyjen rivien lukumäärän.
     *
     * @param uusi Lasku, joka sisältää uudet tiedot
     * @return Päivitettyjen rivien lukumäärä
     */
    public int paivita(Lasku uusi) {
        int muutetutRivit = 0; // Muutettujen rivien lukumäärä
        int maksettuLuku = 0;
        if (uusi.isMaksettu()) { // muutetaan boolean arvo luvuksi 0 tai 1
            maksettuLuku = 1;
        }
        try {
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana); // Yhdistetään tietokantaan
            Statement stmt = con.createStatement(); // Muodostetaan hakulause
            muutetutRivit = stmt.executeUpdate("update lasku set varaus_id = " + uusi.getVaraus().getVaraus_id()
                    + ", summa = " + uusi.getSumma()
                    + ", alv = " + uusi.getAlv()
                    + ", maksettu = " + maksettuLuku
                    + " where lasku_id = " + this.getLasku_id()); // Suoritetaan päivitys
            con.close(); // Suljetaan yhteys
        } catch (Exception ex) {
            // Tässä voisi ilmoittaa käyttäjälle virheestä jotenkin
        }
        return muutetutRivit;
    }

    /**
     * Poistaa laskun tietokannasta lasku_id:n mukaan.
     * Palauttaa poistettujen rivien lukumäärän.
     *
     * @return Poistettujen rivien lukumäärä
     */
    public int poista() {
        int poistetut = 0;
        try {
            Connection con = DriverManager.getConnection(Tietokantayhteys.osoite, Tietokantayhteys.kayttaja, Tietokantayhteys.salasana); // Yhdistetään tietokantaan
            Statement stmt = con.createStatement(); // Muodostetaan hakulause
            poistetut = stmt.executeUpdate("delete from lasku where lasku_id = " + this.getLasku_id()); // Suoritetaan poisto
            con.close(); // Suljetaan yhteys
        }
        catch (Exception ex) {
            // Tässä voisi ilmoittaa käyttäjälle virheestä jotenkin
        }
        return poistetut;
    }

    /**
     * Metodi laskee summan laskulle.
     * @param v varaus olio
     * @param paivat integer luku päivien lukumäärä
     */
    public void laskeSumma(Varaus v, int paivat) {
        double summa = paivat * v.getMokki().getHinta();// lasketaan hinta yöpymiselle

        ArrayList<VarauksenPalvelu> vp = new ArrayList<>(); // alustetaan lista
        ArrayList<VarauksenPalvelu> vpuusi = new ArrayList<>(); // lista johon lisätään halutut arvot
        vp = VarauksenPalvelu.haeKaikkiVarauksenPalvelut(); // haetaan kaikki varatut palvelut

        for (VarauksenPalvelu vp1 : vp){ // käydään lista läpi ja otetaan talteen halutit palvelut
            if (vp1.getVaraus().getVaraus_id() == v.getVaraus_id()){
                vpuusi.add(vp1);
            }
        }

        for (VarauksenPalvelu vp1 : vpuusi){
            Palvelu p = new Palvelu();
            ToimintaAlue ta = new ToimintaAlue();
            p.setToimintaAlue(ta);

            p.haePalvelu(vp1.getPalvelu().getPalveluId());
            vp1.setPalvelu(p);
            double hinta = vp1.getPalvelu().getHinta();

            summa = summa + hinta * vp1.getLkm();
        }

        setSumma(summa);

    }

    /**
     * Metodi laskee alv 10% luvun laskun summasta.
     */
    public void laskeALV(){
        setAlv(getSumma() * 0.1);
    }

    /**
     * Metodi luo pdf tiedoston ja kirjoittaa sen sisällön
     * @param path
     */
    public void luoPdf(String path, String sisalto) {
        Document document = new Document(); // dokumentti jota muokataan
        try
        {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path)); // asetetaan directory minne tiedosto lisätään
            document.open(); // avataa dokumentti kirjoitusta varten
            document.addAuthor("NobCot Oy");
            document.addCreationDate();
            document.addCreator("NobCot");
            document.addTitle("Lasku");
            document.addSubject("Mökin laskun laskutusta");
            //Luodaan kuva
            com.itextpdf.text.Image image1 = Image.getInstance("src/sample/logo.png");
            // Skaalaa kuva hieman pienemmäksi
            image1.scaleAbsolute(148, 69);
            document.add(image1); // lisätään kuva

            document.add(new Paragraph(sisalto)); // lisätään documenttiin sisältö
            document.close(); // suljetaan dokumentti

            writer.close(); // suljetaan kirjoitun

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodi luo laskun sisällön.
     * @return String arvon jossa laskun sisältö.
     */
    public String luoLaskunSisalto(){
        String teksti;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");  // käytetään localdate arvon muokaamiseen
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // käytetaan DAte arvon muokkaamiseen

        ArrayList<VarauksenPalvelu> vp = VarauksenPalvelu.haeKaikkiVarauksenPalvelut(); // lista jossa kaikki varaukset ja palvelut
        ArrayList<VarauksenPalvelu> uusi = new ArrayList<>(); // lista johon otetaan oikeat varauksen palvelut

        //käydään varauksen palvelut lista läpi ja otetaan kyseisen varauksen palvelut ylös
        for (VarauksenPalvelu v : vp){
            if (v.getVaraus().getVaraus_id() == getVaraus().getVaraus_id()){
                uusi.add(v);
            }
        }

        // luodaan varaus josta saadaan laskuun päivät jonka ajan varaus kestänyt
        Varaus varaus = new Varaus();
        varaus.setVarattu_alkupvm(getVaraus().getVarattu_alkupvm());
        varaus.setVarattu_loppupvm(getVaraus().getVarattu_loppupvm());
        varaus.setVaraus_id(getVaraus().getVaraus_id());

        StringBuilder sb = new StringBuilder(); // tätä käytetään string tekstin rakentamiseen

        //laskun esittely
        sb.append("Village Newbies Oy lasku\n");
        sb.append("\n");

        //asiakkaan tiedot
        sb.append("Asiakas:\n");
        sb.append(getVaraus().getAsiakas().getEnimi() + " " + getVaraus().getAsiakas().getSnimi()+"\n");
        sb.append(getVaraus().getAsiakas().getLahiosoite()+ ", "
                + getVaraus().getAsiakas().getPosti().getPostinro()+" "+getVaraus().getAsiakas().getPosti().getToimipaikka()+"\n");
        sb.append("\n");

        //varauksentiedot
        sb.append("Varaus:\n");
        sb.append("Varaus ID: " +getVaraus().getVaraus_id()+"\n");
        sb.append("Mökki: " +getVaraus().getMokki().getMnimi()+ "\n" + getVaraus().getMokki().getOsoite()+
                ", " + getVaraus().getMokki().getPosti().getPostinro()+" " + getVaraus().getMokki().getPosti().getToimipaikka()+"\n");
        sb.append("Aika: " + dateFormat.format(getVaraus().getVarattu_alkupvm()) + " - " +
                dateFormat.format(getVaraus().getVarattu_loppupvm()) + " päiviä " + varaus.palutaEro()+ "\n");
        sb.append("Hinta per yö: " + getVaraus().getMokki().getHinta()+" euroa\n");
        // käydään varauksen palvelut läpi ja lisätään jokainen laskuun
        for (VarauksenPalvelu vp1 : uusi){
            sb.append("Palvelut: " + vp1.getPalvelu().getPalveluNimi() + " " + vp1.getLkm()+" kpl. Kpl hinta: "+ vp1.getPalvelu().getHinta()+" euroa\n");
        }
        sb.append("\n");

        //laskutustiedot
        sb.append("Maksettavat:\n");
        sb.append("Summa: " +getSumma() +" euroa\n");
        sb.append("Tilille: FIxxxxxxx\n");
        sb.append("Viitenumero: xxxx\n");

        //otetaan aika ylös
        LocalDateTime now = LocalDateTime.now();
        sb.append("Lähetetty: " + dtf.format(now) +"\n");
        sb.append("Eräpäivä: " + dtf.format(now.plusDays(14)) +"\n");

        teksti = sb.toString(); // muutetaan sb string muotoon
        return teksti;
    }

    public String luoMuistutus(){
        String teksti;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");  // käytetään localdate arvon muokaamiseen
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy"); // käytetaan DAte arvon muokkaamiseen

        ArrayList<VarauksenPalvelu> vp = VarauksenPalvelu.haeKaikkiVarauksenPalvelut(); // lista jossa kaikki varaukset ja palvelut
        ArrayList<VarauksenPalvelu> uusi = new ArrayList<>(); // lista johon otetaan oikeat varauksen palvelut

        //käydään varauksen palvelut lista läpi ja otetaan kyseisen varauksen palvelut ylös
        for (VarauksenPalvelu v : vp){
            if (v.getVaraus().getVaraus_id() == getVaraus().getVaraus_id()){
                uusi.add(v);
            }
        }

        // luodaan varaus josta saadaan laskuun päivät jonka ajan varaus kestänyt
        Varaus varaus = new Varaus();
        varaus.setVarattu_alkupvm(getVaraus().getVarattu_alkupvm());
        varaus.setVarattu_loppupvm(getVaraus().getVarattu_loppupvm());
        varaus.setVaraus_id(getVaraus().getVaraus_id());

        StringBuilder sb = new StringBuilder(); // tätä käytetään string tekstin rakentamiseen

        //laskun esittely
        sb.append("Village Newbies Oy muistutuslasku\n");
        sb.append("\n");

        //asiakkaan tiedot
        sb.append("Asiakas:\n");
        sb.append(getVaraus().getAsiakas().getEnimi() + " " + getVaraus().getAsiakas().getSnimi()+"\n");
        sb.append(getVaraus().getAsiakas().getLahiosoite()+ ", "
                + getVaraus().getAsiakas().getPosti().getPostinro()+" "+getVaraus().getAsiakas().getPosti().getToimipaikka()+"\n");
        sb.append("\n");

        //varauksentiedot
        sb.append("Varaus:\n");
        sb.append("Varaus ID: " +getVaraus().getVaraus_id()+"\n");
        sb.append("Mökki: " +getVaraus().getMokki().getMnimi()+ "\n" + getVaraus().getMokki().getOsoite()+
                ", " + getVaraus().getMokki().getPosti().getPostinro()+" " + getVaraus().getMokki().getPosti().getToimipaikka()+"\n");
        sb.append("Aika: " + dateFormat.format(getVaraus().getVarattu_alkupvm()) + " - " +
                dateFormat.format(getVaraus().getVarattu_loppupvm()) + " päiviä " + varaus.palutaEro()+ "\n");
        sb.append("Hinta per yö: " + getVaraus().getMokki().getHinta()+" euroa\n");
        // käydään varauksen palvelut läpi ja lisätään jokainen laskuun
        for (VarauksenPalvelu vp1 : uusi){
            sb.append("Palvelut: " + vp1.getPalvelu().getPalveluNimi() + " " + vp1.getLkm()+" kpl. Kpl hinta: "+ vp1.getPalvelu().getHinta()+" euroa\n");
        }
        sb.append("\n");

        //laskutustiedot
        sb.append("Maksettavat:\n");
        sb.append("Summa: " +getSumma() +" euroa\n");
        sb.append("Tilille: FIxxxxxxx\n");
        sb.append("Viitenumero: xxxx\n");

        //otetaan aika ylös
        LocalDateTime now = LocalDateTime.now();
        sb.append("Lähetetty: " + dtf.format(now) +"\n");
        sb.append("Laskun eräpäivä on mennyt jo. Maksa seuraavapan 5 päivän kuluessa tai lasku siirretään perintään.");

        teksti = sb.toString(); // muutetaan sb string muotoon
        return teksti;
    }
}
