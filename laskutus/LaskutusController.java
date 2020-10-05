package laskutus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import asiakas.Asiakas;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import mokki.Mokki;
import varaus.VarauksenPalvelu;
import varaus.Varaus;
import yleisetTyokalut.Ohje;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * Tässä luokassa on kaikki toiminnallisuus liittyen laskutus näkymään.
 *
 * @author Severi Moisio
 */
public class LaskutusController {
    private Lasku lasku = new Lasku(); // käyettään laskun kuontiin

    //lista johon luetaan laskut kannasta
    private ArrayList<Lasku> laskulista;
    private ArrayList<Varaus> varaukset; // käytetaan varausten haussa

    @FXML
    private ObservableList<Lasku> obsLaskut; // tätä listaa käytetään lasku tableviewin asettamiseen
    @FXML
    private ObservableList<Varaus> obsVaraukset; // tätä listaa käytetään varaus tableviewin asettamiseen

    //seuraavaksi alustettu näkymän eri elementtejä käytettäväksi koodissä
    @FXML
    private TableColumn tcLaskuId;
    @FXML
    private TableColumn tcSumma;
    @FXML
    private TableColumn tcAlv;
    @FXML
    private TableColumn tcMaksettu;
    @FXML
    private TableColumn tcEtunimi;
    @FXML
    private TableColumn tcSukunimi;
    @FXML
    private TableColumn tcLoppu;
    @FXML
    private TableView tvLaskut;
    @FXML
    private AnchorPane apVaraus;
    @FXML
    private AnchorPane apLasku;
    @FXML
    private AnchorPane apKeski;
    @FXML
    private ComboBox cbEhdot;
    @FXML
    private TableView tvVaraukset;
    @FXML
    private TableColumn sarakeEtunimi;
    @FXML
    private TableColumn sarakeSukunimi;
    @FXML
    private TableColumn sarakeMokki;
    @FXML
    private TableColumn sarakeAlkuPvm;
    @FXML
    private TableColumn sarakeLoppuPvm;
    @FXML
    private TableColumn sarakeId;
    @FXML
    private TextField tfHaku;
    @FXML
    private Label lblIlmoitukset;
    @FXML
    private TextField tfEtunimi;
    @FXML
    private TextField tfSukunimi;
    @FXML
    private TextField tfSumma;
    @FXML
    private TextField tfALV;
    @FXML
    private CheckBox chbMaksettu;
    @FXML
    private Button btnPaivita;
    @FXML
    private Button btnTallennaPdf;
    @FXML
    private Button btnLuo;
    @FXML
    private Button btnAloita;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSpostiMuistutus;
    @FXML
    private Button btnSpostiLasku;
    @FXML
    private ListView lwPalvelut;

    /**
     * Tämä metodi laukeaa kun palveluhallinta näkymä avataan sovelluksessa. Alustaa tableview näkymät ja täyttää laskutus taulun.
     */
    public void initialize() {
        alustaLaskuTaulukko(); //Alustetaan tableview sarakkeet
        lisaaLaskutTaulukkoon(); // lisätään arvot taulukkoon
        alustaVarausTaulukko(); // alustetaan varausten hakuun käytettävää tableview
        java.awt.Button button = new java.awt.Button();
        button.setVisible(false);
    }

    /**
     * Metodi alustaa varausten hakuun käytettävän tauukon.
     */
    public void alustaVarausTaulukko() {
        //alustetaan etunimi sarake
        sarakeEtunimi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getAsiakas().getEnimi());
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });
        //alustetaan sukunimi sarake
        sarakeSukunimi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getAsiakas().getSnimi());
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });
        //alustetaan mokkinimi sarake
        sarakeMokki.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getMokki().getMnimi());
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });

        // alustetaan ID sarake
        sarakeId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(Integer.toString(cellDataFeatures.getValue().getVaraus_id()));
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });

        //alustetaan alkupvm sarake
        sarakeAlkuPvm.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, Date>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, Date> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    // formatoidaan Date stringiin
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    return new SimpleStringProperty(dateFormat.format(cellDataFeatures.getValue().getVarattu_alkupvm()));
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });

        // alustetaan loppupvm sarake
        sarakeLoppuPvm.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Varaus, Date>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Varaus, Date> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    // formatoidaan Date stringiin
                    return new SimpleStringProperty(dateFormat.format(cellDataFeatures.getValue().getVarattu_loppupvm()));
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });

    }

    /**
     * Metodi alustaa lasku tableview sarakkeet
     */
    public void alustaLaskuTaulukko() {
        //alustetaan etunimi sarake
        tcEtunimi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Lasku, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Lasku, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getVaraus().getAsiakas().getEnimi()); // tässä haetaan oikea arvo sarakkeeseen
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });
        //alustetaan sukunimi sarake
        tcSukunimi.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Lasku, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Lasku, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getVaraus().getAsiakas().getSnimi());
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });
        //alustetaan id sarake
        tcLaskuId.setCellValueFactory(new PropertyValueFactory<Lasku, Integer>("lasku_id"));
        //alustetaan summa sarake
        tcSumma.setCellValueFactory(new PropertyValueFactory<Lasku, Double>("summa"));
        // alustetaan alv sarake
        tcAlv.setCellValueFactory(new PropertyValueFactory<Lasku, Double>("alv"));
        //alustetaan maksettu sarake
        tcMaksettu.setCellValueFactory(new PropertyValueFactory<Lasku, Boolean>("maksettu"));
        // alustetaan varauksen loppumis ajankohta sarake
        tcLoppu.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Lasku, Date>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Lasku, Date> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    DateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy");
                    // formatoidaan Date stringiin
                    return new SimpleStringProperty(dateFormat.format(cellDataFeatures.getValue().getVaraus().getVarattu_loppupvm()));
                } else {
                    return new SimpleStringProperty("<ei nimeä>");
                }
            }
        });
    }

    /**
     * Metodi lisää laskut sille tarkoitettuun taulukkoon
     */
    public void lisaaLaskutTaulukkoon() {

        laskulista = Lasku.haeKaikkiLaskut(); // haetaan toiminta-alueet kannasta
        obsLaskut = FXCollections.observableArrayList(); //alustetaan observable lista
        for (Lasku l : laskulista) { // käydään toiminta-alue lista läpi ja lisätään palvelut observablelistalle
            obsLaskut.add(l); // lisätään palvelu listaan
        }
        tvLaskut.setItems(obsLaskut); // asetetaan taulukkonäkymän obs. listaksi alueiden nimet
    }

    /**
     * Metodi aukaisee varausten haun. Sekä lisää hakuehdot näkyville.
     */
    public void aloitaLaskunLuonti() {
        lblIlmoitukset.setVisible(false); // ohjeet pois näkyvistä
        apVaraus.setDisable(false); // aukaistaan anchorpane
        apKeski.setDisable(true); // keski napit kiinni
        lisaaHakuEhdot(); // lisätään hakuehdot comboboxiin
        tvLaskut.setDisable(true); //laskutaulu pois käytöstä tässä
        btnCancel.setDisable(false); // peruuta nappi auki
    }

    /**
     * Metodi lisää comboboxiin mahdolliset hakuehdot
     */
    public void lisaaHakuEhdot() {
        ObservableList<String> ehdot = FXCollections.observableArrayList();// alustetaan lista joka lisätään comboboxiin
        //lisätään listaan alkiot
        ehdot.add("Asiakas etunimi");
        ehdot.add("Asiakas sukunimi");
        ehdot.add("Mökin nimi");
        ehdot.add("Varaus ID");
        cbEhdot.setItems(ehdot);// lisätään comboboxiin sisältö
    }

    /**
     * Metodi hakee varaukset halutulla arvolla ja lisää ne sitten taulukkoon.
     */
    public void haeVaraukset() {
        lblIlmoitukset.setVisible(false); // ilmoitus pois näkyvistä jos on jäänyt päälle
        try {
            String haku = cbEhdot.getSelectionModel().getSelectedItem().toString(); // otetaan comboboxista hakuehto ylös
            lblIlmoitukset.setVisible(false); // ohjepois näkyvistä
            switch (haku) { // switchataan mikä vaihtoehto on valittu
                case "Asiakas etunimi":
                    Asiakas a = new Asiakas();
                    a.setEnimi(tfHaku.getText());
                    varaukset = Varaus.haeKaikkiVaraukset(a); // haetaan hauehdoll avaraukset
                    break;
                case "Asiakas sukunimi":
                    varaukset = Varaus.haeKaikkiVaraukset(tfHaku.getText()); // haetaan hauehdoll avaraukset
                    break;
                case "Mökin nimi":
                    Mokki m = new Mokki();
                    m.setMnimi(tfHaku.getText());
                    varaukset = Varaus.haeKaikkiVaraukset(m);
                    break;
                case "Varaus ID":
                    try {
                        varaukset = Varaus.haeKaikkiVaraukset(Integer.parseInt(tfHaku.getText())); // haetaan hauehdoll avaraukset
                    } catch (Exception ex) {
                        lblIlmoitukset.setText("Kokonaisluku vain kelpaa");
                        lblIlmoitukset.setVisible(true);
                    }
                    break;
                default:
                    lblIlmoitukset.setText("Aseta hakuehto");
                    lblIlmoitukset.setVisible(true);
            }

            LocalDate nyt = LocalDate.now();// otetaan tämänhetnen aika ylös

            obsVaraukset = FXCollections.observableArrayList(); //alustetaan observable lista
            for (Varaus v : varaukset) { // käydään toiminta-alue lista läpi ja lisätään palvelut observablelistalle
                //muutetaan alku pvm localdate muotoon, eetä voidaan vertailla
                LocalDate alku = v.getVarattu_alkupvm().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                alku = alku.minusDays(7); // viikko pois ajasta eli viikko ennen varauksen alkue voidaan laskusttaa.
                if (nyt.compareTo(alku) >= 0) { //jos nyt aika on suurempi kuin palvelun alku - 7
                    obsVaraukset.add(v); // lisätään palvelu listaan
                }
            }
            tvVaraukset.setItems(obsVaraukset); // asetetaan taulukkonäkymän obs. listaksi alueiden nimet
        } catch (Exception ex) {
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Valitse hakuehto");
        }
    }

    /**
     * Metodi laskee laskulle kaikki arvot ja asettaa ne esille käyttöliitttmään.
     */
    public void aloitaLuoLasku() {
        Varaus varaus = (Varaus) tvVaraukset.getSelectionModel().getSelectedItem(); // otetaan haluttu varaus ylös taulusta
        if (varaus != null) { // jos klikataan taulussa jotain muuta kuin varausta niin ei tehdä mitään
            btnPaivita.setDisable(true); // päivitys nappi pois käytöstä
            btnLuo.setDisable(false); // oikea luonti nappi auki
            apLasku.setDisable(false); // alku valinta pois päältä
            //apVaraus.setDisable(true); // laskun hallinta auki
            btnAloita.setDisable(true); // aloitus nappi auki


            tfEtunimi.setText(varaus.getAsiakas().getEnimi()); // Asetetaan asiakkaan etunimi näkyville
            tfSukunimi.setText(varaus.getAsiakas().getSnimi()); // asetetaan asiakkaan sukunim inäkyville

            int maara = varaus.palutaEro(); // lasketaan varauksen kesto päivinä

            lasku.laskeSumma(varaus, maara); // lasketaan laskulle summa ja asetetaan se lasku olioon
            lasku.laskeALV(); // lasketaan alv
            lasku.setVaraus(varaus); // asetetaan laskulle varaus

            tfSumma.setText(Double.toString(lasku.getSumma())); // laitetaan summa esille ikkunaan
            tfALV.setText(Double.toString(lasku.getAlv())); // laitetaan alv esiin ikkunaan

            ArrayList<VarauksenPalvelu> vp = VarauksenPalvelu.haeKaikkiVarauksenPalvelut(); // lista jossa kaikki varaukset ja palvelut
            ArrayList<VarauksenPalvelu> uusi = new ArrayList<>(); // lista johon otetaan oikeat varauksen palvelut

            //käydään varauksen palvelut lista läpi ja otetaan kyseisen varauksen palvelut ylös
            for (VarauksenPalvelu v : vp) {
                if (v.getVaraus().getVaraus_id() == lasku.getVaraus().getVaraus_id()) {
                    uusi.add(v);
                }
            }

            ObservableList<String> obspalvelut = FXCollections.observableArrayList();

            for (VarauksenPalvelu vp2 : uusi) {
                String p = vp2.getPalvelu().getPalveluNimi() + " " + vp2.getLkm() + " kpl";
                obspalvelut.add(p);
            }
            lwPalvelut.setItems(obspalvelut);
        }
    }

    /**
     * Metodi peruuttaa laskun lisäyksen/ muokkauksen ja tyhjää arvot
     */
    public void peruuta() {
        lblIlmoitukset.setVisible(false); // ilmoitus pois päältä
        btnCancel.setDisable(true); // peruuta nappi kiinni
        apVaraus.setDisable(true); // varaus ap kiinni
        tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
        if (btnLuo.isDisable() == false) { // jos ollaan lisäämässä laskua kantaan mennään tähän
            peruutaKokoLisays();
            btnAloita.setDisable(false); //laskun luonnin aloitusnappi auki
            lwPalvelut.setItems(null);
            //btnSpostiLasku.setDisable(false); // peruutukset pois napeista
            //btnSpostiMuistutus.setDisable(false); // peruutukset pois napeista
        } else if (btnPaivita.isDisable() == false) { // jos muokkaus tila niin tänne
            tyhjaaArvot(); // tyhjätään arvot
            tvLaskut.setDisable(false);
            apLasku.setDisable(true);
            btnAloita.setDisable(false); //laskun luonnin aloitusnappi auki
            lwPalvelut.setItems(null);
            //btnSpostiLasku.setDisable(false); // peruutukset pois napeista
            //btnSpostiMuistutus.setDisable(false); // peruutukset pois napeista
        } else {
            peruutaKokoLisays();
            cbEhdot.setItems(null); // ehdot tyhjiksi
            tvVaraukset.setItems(null); // varaus taulu yyhjäksi
            lwPalvelut.setItems(null);
            //btnSpostiLasku.setDisable(false); // peruutukset pois napeista
            //btnSpostiMuistutus.setDisable(false); // peruutukset pois napeista
        }
    }

    /**
     * Metodi tyhjää laskun arvot tekstikentistä.
     */
    public void tyhjaaArvot() {
        //tyhjätään kaikki kentät
        tfEtunimi.setText("");
        tfALV.setText("");
        tfSumma.setText("");
        tfSukunimi.setText("");
    }

    /**
     * Metodi lisää laskun kantaan ja päivittää sen taulukkoon näkyviin
     */
    public void luoLasku() {
        try {
            lasku.setSumma(Double.parseDouble(tfSumma.getText())); // jos arvoja muutettu päivitetään laskuun
            lasku.setAlv(Double.parseDouble(tfALV.getText()));// jos arvoja muutettu päivitetään laskuun

            lasku.lisaa(); // lisätään lasku kantaan
            lisaaLaskutTaulukkoon(); // päivitetään taulukko
            peruutaKokoLisays();
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Onnistui"); // ilmoitetaan onnistuminen
            tvLaskut.setDisable(false);
            btnAloita.setDisable(false);
            btnCancel.setDisable(true); // peruutus pois päältä
            lwPalvelut.setItems(null); // palvelut pois näkyvistä
        } catch (Exception ex) {
            // ilmoitetaan virheestä jos summat annettu väärässä formaatissa
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Tarkasta arvot");
        }
    }

    /**
     * Metodi sulkee nykyisen ikkunan
     */
    public void etusivulle() {
        Stage ikkuna = (Stage) btnAloita.getScene().getWindow(); // otetaan ylös stage jolla nappi sijaitsee
        ikkuna.close(); // suljetaan ikkuna
    }

    /**
     * Metodi resetoi koko lisäyksen
     */
    public void peruutaKokoLisays() {
        tyhjaaArvot(); // tyhjätään arvot
        apLasku.setDisable(true); // lasku kiinni
        apVaraus.setDisable(true); // laitetaan laskun haku pois saatavilta
        tfHaku.setText(""); // tyhjennetään haku
        cbEhdot.setItems(null); // comboboxin hakuvaihtoehdot tyhjäksi
        tvVaraukset.setItems(null); // taulukko tyhjennetään
        tvLaskut.setDisable(false); // varaus taulu auki
    }

    /**
     * Metodi luo ensiksi pdf tiedoston laskusta tai muistutuksesta ja tallentaa sen sitten haluuttuun paikkaan.
     */
    public void tallennaPdfLasku(ActionEvent event) throws IOException {

        DirectoryChooser dc = new DirectoryChooser(); // luodaan työkäly polun valitsemiseen
        File file = dc.showDialog(btnTallennaPdf.getScene().getWindow()); // avataan valitsija
        if (file != null) {
            if (event.getSource().equals(btnTallennaPdf)) {
                if (file.isDirectory()) { //jos hyväksytään mennään sisään
                    //luodaan pdf lasku
                    // luodaan koko tiedostoolku
                    String polku = file.getPath() + "\\" + lasku.getLasku_id() + lasku.getVaraus().getAsiakas().getSnimi() + ".pdf";

                    String sisalto = lasku.luoLaskunSisalto();
                    lasku.luoPdf(polku, sisalto); // kutsutaan pdf:n luonti metodia. Eli luodaan pdf lasku
                    apKeski.setDisable(true); //keskön napit poist päältä
                    btnAloita.setDisable(false); // aloitus napi auki
                    lblIlmoitukset.setText("Pdf luotu");
                    tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
                }
            } else { // tässä luodaan muistutus

                if (file.isDirectory()) { //jos hyväksytään mennään sisään

                    // asetetaan tiedostopolku
                    String polku = file.getPath() + "\\" + lasku.getLasku_id() + lasku.getVaraus().getAsiakas().getSnimi() + " muistutus.pdf";

                    String sisalto = lasku.luoMuistutus();
                    lasku.luoPdf(polku, sisalto); // kutsutaan pdf:n luonti metodia. Eli luodaan pdf lasku
                    apKeski.setDisable(true); //keskön napit poist päältä
                    btnAloita.setDisable(false); // aloitus napi auki
                    lblIlmoitukset.setText("Pdf luotu");
                    tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
                }
            }
        }
    }

    /**
     * MEtodi valitsee lasku olion taulukosta ja ottaa sen ylös luokassa käytettävään olioon.
     */
    public void valitseLasku() {
        lasku = (Lasku) tvLaskut.getSelectionModel().getSelectedItem(); // otetaan olio talteen

        if (lasku != null) {// jos lasku on null ei tehdä mitään

            apKeski.setDisable(false);
            //btnAloita.setDisable(true);
            if (lasku.getVaraus().getAsiakas().getEmail() == null){
                btnSpostiLasku.setDisable(true);
                btnSpostiMuistutus.setDisable(true);
            }
            else{
                btnSpostiLasku.setDisable(false); // peruutukset pois napeista
                btnSpostiMuistutus.setDisable(false); // peruutukset pois napeista
            }
            // ilmoitetaan onnistuminen
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Valittu lasku on käytössäsi");
        }
    }

    /**
     * Metodi antaa mahdollisuuden muokata laskua
     */
    public void muokkaa() {
        apLasku.setDisable(false); // alku valinta pois päältä
        apVaraus.setDisable(true); // laskun hallinta auki
        tvLaskut.setDisable(true); // taulukko pois käytöstä
        lblIlmoitukset.setVisible(false); // ilmoitus pois päältä
        btnCancel.setDisable(false); // peruutus auki

        //asetetaan tiedot kenttiin
        tfEtunimi.setText(lasku.getVaraus().getAsiakas().getEnimi());
        tfSukunimi.setText(lasku.getVaraus().getAsiakas().getSnimi());
        tfALV.setText(Double.toString(lasku.getAlv()));
        tfSumma.setText(Double.toString(lasku.getSumma()));
        chbMaksettu.setSelected(lasku.isMaksettu());

        // keski osan napit pois käytöstä
        apKeski.setDisable(true);
        btnPaivita.setDisable(false);
        btnLuo.setDisable(true);

        ArrayList<VarauksenPalvelu> vp = VarauksenPalvelu.haeKaikkiVarauksenPalvelut(); // lista jossa kaikki varaukset ja palvelut
        ArrayList<VarauksenPalvelu> uusi = new ArrayList<>(); // lista johon otetaan oikeat varauksen palvelut

        //käydään varauksen palvelut lista läpi ja otetaan kyseisen varauksen palvelut ylös
        for (VarauksenPalvelu v : vp) {
            if (v.getVaraus().getVaraus_id() == lasku.getVaraus().getVaraus_id()) {
                uusi.add(v);
            }
        }

        ObservableList<String> obspalvelut = FXCollections.observableArrayList();

        for (VarauksenPalvelu vp2 : uusi) {
            String p = vp2.getPalvelu().getPalveluNimi() + " " + vp2.getLkm() + " kpl";
            obspalvelut.add(p);
        }
        lwPalvelut.setItems(obspalvelut);

        tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
    }

    /**
     * Metodi päivittää laskun tiedot kantaan. Päivitys suoritetaan joka tapauksessa oli muokattu tai ei.
     */
    public void paivita() {
        try {
            lasku.setSumma(Double.parseDouble(tfSumma.getText())); // jos arvoja muutettu päivitetään laskuun
            lasku.setAlv(Double.parseDouble(tfALV.getText()));// jos arvoja muutettu päivitetään laskuun
            lasku.setMaksettu(chbMaksettu.isSelected());

            lasku.paivita(lasku); // päivitetään laskun summat
            lisaaLaskutTaulukkoon(); // päivitetään taulukko
            //btnSpostiLasku.setDisable(false); // peruutukset pois napeista
            //btnSpostiMuistutus.setDisable(false); // peruutukset pois napeista
            peruutaKokoLisays();// lopetetaan lisäys'
            lblIlmoitukset.setVisible(true); // ilmoitetaan onnistuminen
            lblIlmoitukset.setText("Onnistui");
            tvLaskut.setDisable(false);
            btnAloita.setDisable(false); //laskun luonnin aloitusnappi auki
            lwPalvelut.setItems(null); // palvelut pois näkyvistä
            btnCancel.setDisable(true); // peruutus nappi pois päältä
        } catch (Exception ex) {
            // ilmoitetaan virheestä jos summat annettu väärässä formaatissa
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Tarkasta arvot");
        }
    }

    /**
     * Metodi poistaa valitun laskun kannasta.
     */
    public void poista() {
        if (lasku.isMaksettu() == true) { // tutkitaan onko jo lasku maksettu. Jos on niin ei voi poistaa
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Maksettua laskua ei voida poistaa");
            //apKeski.setDisable(true); // napit pois käytöstä
            //tvLaskut.setDisable(false); // taulukko auki
            //tvLaskut.getSelectionModel().clearSelection();
        } else {
            //seuraavaksi avataan kysely ikkuna jossa kysytyään poistoon varmuutta
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // määritetään ilmoitus
            alert.setTitle("Poisto"); // asetetaan otsikko ilmoituselle
            alert.setContentText("Haluatko varmasti poistaa laskun?"); //lisätään teksti

            // Määritetään eka nappi ilmoitykseen
            ButtonType okButton = new ButtonType("Kyllä", ButtonBar.ButtonData.YES);
            // Määritetään eka nappi ilmoitykseen
            ButtonType noButton = new ButtonType("Ei", ButtonBar.ButtonData.NO);
            // asetetaan napit ilmoitukseen
            alert.getButtonTypes().setAll(okButton, noButton);

            //avataan ilmoitus ja jäähään odottamaan vastausta
            Optional<ButtonType> result = alert.showAndWait();

            if (result.orElse(okButton) == okButton) { // Tutkitana mitä on painettu jos kyllä niin aktivoidaan poisto
                lasku = (Lasku) tvLaskut.getSelectionModel().getSelectedItem(); // otetaan olio taulokosta
                int arvo = lasku.poista(); //poistetaan lasku kannasta ja otetaan ylös tulos
                if (arvo >= 1) { // jos onnistunut mennään tähän
                    lisaaLaskutTaulukkoon();// päivitetään taulukko
                    lblIlmoitukset.setVisible(true); // ilmoitetaan poiston onnistimisesta
                    lblIlmoitukset.setText("Onnistui");
                    apKeski.setDisable(true); // napit pois käytöstä
                    tvLaskut.setDisable(false); // taulukko auki
                    btnAloita.setDisable(false); //laskun luonnin aloitusnappi auki
                    tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
                } else {
                    lblIlmoitukset.setVisible(true);
                    lblIlmoitukset.setText("Poisto epäonnistui"); // ilmoitetaan poiston epäonnistumisesta
                }
            }
        }
    }

    /**
     * MEtodi lähettää laskun tai muistutuksen asiakkaan sähköpostiin. Sisältö otetaan valitusta lasku oliosta.
     */
    public void lahetaSahkoposti(ActionEvent event) {
        //kysytään varmuus lähettämiseen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // määritetään ilmoitus
        alert.setTitle("Sähköpostin lähetys"); // asetetaan otsikko ilmoituselle
        alert.setContentText("Onko sähköpostiosoite oikein?\n\n" + lasku.getVaraus().getAsiakas().getEmail()); //lisätään teksti

        // Määritetään eka nappi ilmoitykseen
        ButtonType okButton = new ButtonType("Kyllä", ButtonBar.ButtonData.YES);
        // Määritetään eka nappi ilmoitykseen
        ButtonType noButton = new ButtonType("Ei", ButtonBar.ButtonData.NO);
        // asetetaan napit ilmoitukseen
        alert.getButtonTypes().setAll(okButton, noButton);

        //avataan ilmoitus ja jäädään odottamaan vastausta
        Optional<ButtonType> result = alert.showAndWait();

        // jos on oikein lähetetään sähköposti
        if (result.orElse(okButton) == okButton) { // Tutkitana mitä on painettu jos kyllä niin aktivoidaan lähetys
            //String to = "severi.moisio@hotmail.fi"; //oma sähköposti testiä varten
            String to = lasku.getVaraus().getAsiakas().getEmail(); // osoite mihin lähetetään
            String user = "seve45@gmail.com"; // käyttäjänimi minun gmail palvelimeen
            String host = "smtp.gmail.com";//or IP address gmail palvelin
            String password = "Ebksnznf1"; // minun salasana gmailiin

            //asetetaan lähetyksen propestiesit
            Properties props = System.getProperties();
            props.put("mail.smtp.host", host); // mihin otetaan yhteys
            props.put("mail.smtp.port", "465"); // portti mitä käyttää
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");

            // kirjaudutaan gmailiin sisään minun tiedoilla
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication(user, password);

                }

            });
            // viestin muodostus ja lähetys
            if (event.getSource().equals(btnSpostiLasku)) {
                try {
                    MimeMessage message = new MimeMessage(session);

                    message.setFrom(new InternetAddress(user));// asetetaan lähettävä sposti
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // asetetaan osoite mihin lähetetään
                    message.setSubject("Lasku"); // emailin aihe
                    message.setText(lasku.luoLaskunSisalto()); // emailin sisältö

                    //lähetetään viesti
                    Transport.send(message);

                    //ilmoitetaan onnistuminen
                    lblIlmoitukset.setVisible(true);
                    lblIlmoitukset.setText("Lähetys onnistui");
                    apKeski.setDisable(true); //keskön napit poist päältä
                    btnAloita.setDisable(false); // aloitus napi auki
                    tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
                } catch (MessagingException e) {
                    e.printStackTrace();
                    lblIlmoitukset.setVisible(true);
                    lblIlmoitukset.setText("Lähetys epäonnistui");
                }
            } else {
                try {
                    MimeMessage message = new MimeMessage(session);

                    message.setFrom(new InternetAddress(user));// asetetaan lähettävä sposti
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // asetetaan osoite mihin lähetetään
                    message.setSubject("Muistutuslasku"); // emailin aihe
                    message.setText(lasku.luoMuistutus()); // emailin sisltö

                    //lähetetään viesti
                    Transport.send(message);

                    //ilmoitetaan onnistuminen
                    lblIlmoitukset.setVisible(true);
                    lblIlmoitukset.setText("Lähetys onnistui");
                    apKeski.setDisable(true); //keskön napit poist päältä
                    btnAloita.setDisable(false); // aloitus napi auki
                    tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
                } catch (MessagingException e) {
                    e.printStackTrace();
                    lblIlmoitukset.setVisible(true);
                    lblIlmoitukset.setText("Lähetys epäonnistui");
                }
            }
        } else if (result.orElse(noButton) == noButton) {
            alert.setTitle("Ohje"); // asetetaan otsikko ilmoituselle
            alert.setContentText("Käy muokkaamassa asiakkaan tietoja asiakkaanhallinnassa."); //lisätään teksti
            // Määritetään eka nappi ilmoitykseen
            ButtonType okNappi = new ButtonType("OK", ButtonBar.ButtonData.YES);
            alert.getButtonTypes().setAll(okNappi);
            alert.show();
            apKeski.setDisable(true); //keskön napit poist päältä
            btnAloita.setDisable(false); // aloitus napi auki
        }
    }

    /**
     * Metodi peruuttaa tilan kun ollaan valittu taulukosta lasku, muttei halutakkaan tehd sille mitään.
     */
    public void cancel() {
        apKeski.setDisable(true); // keski napit pois käytöstä
        btnAloita.setDisable(false); // laskun luonti aloitusnappi auki
        lblIlmoitukset.setVisible(false); // ilmoitus pois päältä
        tvLaskut.getSelectionModel().clearSelection(); // poistetaan valinta lasku taulusta
        btnSpostiLasku.setDisable(false);
        btnSpostiMuistutus.setDisable(false);
    }

    /**
     * Metodi avaa pdf ohjeen selaimeen.
     */
    public void avaaOhje() {
        Ohje.avaaOhje();
    }
}
