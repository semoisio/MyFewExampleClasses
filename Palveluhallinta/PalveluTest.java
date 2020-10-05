package Palveluhallinta;

import static org.junit.Assert.*;
import org.junit.Test;
import toimintaalue.ToimintaAlue;

import java.util.ArrayList;

public class PalveluTest {

    @Test
    public void getPalveluId() {
    }

    @Test
    public void setPalveluId() {
    }

    @Test
    public void getPalveluNimi() {
    }

    @Test
    public void setPalveluNimi() {
    }

    @Test
    public void getKuvaus() {
    }

    @Test
    public void setKuvaus() {
    }

    @Test
    public void getHinta() {
    }

    @Test
    public void setHinta() {
    }

    @Test
    public void haePalvelut() {
        ArrayList<Palvelu> palvelut = new ArrayList<>();
        Palvelu p = new Palvelu(); //Luodaan palvelu
        palvelut = p.haePalvelut(); // haetaan palvelut kannasta ja lisätään listaan
        assertEquals(3,palvelut.size()); // oletettu tulos
    }

    @Test
    public void haePalvelu() {
        //ToimintaAlue ta = new ToimintaAlue(2, "Testi");
        //Palvelu p = new Palvelu(1,"Koe","TEsti",44,ta);
        ToimintaAlue ta = new ToimintaAlue(); // luodaan uusi olio
        Palvelu p = new Palvelu(ta); // luodaan uusi olio johon liitetään haun tulos
        ToimintaAlue ta1 = new ToimintaAlue();
        Palvelu p1 = new Palvelu(ta1);
        assertEquals(true,p.haePalvelu("Testi")); // oletettu tulos joka pitää paikkansa
        assertEquals(false,p1.haePalvelu("kk")); // testattu väärä haku
    }

    @Test
    public void testHaePalvelu() {
        ToimintaAlue ta = new ToimintaAlue(); // luodaan uusi olio
        Palvelu p = new Palvelu(ta);
        Palvelu p1 = new Palvelu();
        assertEquals(true,p.haePalvelu(1)); // oletettu onnistuminen
        assertEquals(false,p1.haePalvelu(-1)); // oletettu epäonnistuminen
    }

    @Test
    public void lisaa() {
        ToimintaAlue ta = new ToimintaAlue(2, "Testi");
        Palvelu p = new Palvelu(1,"Koe","TEsti",44,ta);
        assertEquals(1,p.lisaaPalvelu()); // lisätään olio kantaan ja oletetaan onnistuvaksi
        p.poistaPalvelu(); // poistetaan juuri luotu rivi kannasta.
        ta.setToimintaalue_id(-9);
        Palvelu p1 = new Palvelu(-1,"Kokkeilu","Jeppis",88,ta);
        assertEquals(0,p1.lisaaPalvelu()); //yritetään lisätä ja oletetaan epäonnistuminen
    }

    @Test
    public void poista() {
        ToimintaAlue ta = new ToimintaAlue(2, "Testi");
        Palvelu p = new Palvelu(1,"Koe","TEsti",44,ta);
        p.lisaaPalvelu(); // lisätään olio kantaan
        assertEquals(1,p.poistaPalvelu()); // poistetaan juuri lisätty rivi
        assertEquals(0,p.poistaPalvelu()); // rivi on jo poistettu joten voidaan olettaa epäonnistumista.
    }

    @Test
    public void paivita() {
        ToimintaAlue ta = new ToimintaAlue(2, "Koe");
        Palvelu p = new Palvelu(1,"Koe","TEsti",44,ta);
        ToimintaAlue ta1 = new ToimintaAlue(2, "Testi");
        Palvelu vanha = new Palvelu(1,"Koe","TEsti",44,ta1);
        ToimintaAlue ta2 = new ToimintaAlue(2, "Testi");
        Palvelu vanha1 = new Palvelu(-1,"Koe","TEsti",44,ta2);
        vanha.haePalvelu(10);
        assertEquals(1,vanha.paivitaPalvelu(p)); // päivitetään
        assertEquals(1,vanha.paivitaPalvelu(vanha));// päivitetään takaisin
        assertEquals(0,vanha1.paivitaPalvelu(p));
    }

    @Test
    public void testPaivita() {
        ToimintaAlue ta = new ToimintaAlue(2, "Koe");
        Palvelu p = new Palvelu(1,"Koe","TEsti",44,ta);
        assertEquals(1,p.paivitaPalvelu());
        ToimintaAlue ta1 = new ToimintaAlue(2, "Testi");
        Palvelu p1 = new Palvelu(1,"Koe","TEsti",44,ta1);
        assertEquals(1,p1.paivitaPalvelu());
        Palvelu p2 = new Palvelu();
        assertEquals(0,p2.paivitaPalvelu());
    }
}