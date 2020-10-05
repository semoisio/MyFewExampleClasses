package Palveluhallinta;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import toimintaalue.ToimintaAlue;
import yleisetTyokalut.Ohje;

import java.util.*;

/**
 * Tässä luokassa sijaitsee palveluhallinta näkymän kaikki toiminnot
 *
 * @author Severi Moisio
 */
public class PalveluhallintaController {
    //lista johon luetaan palvelut kannasta
    private ArrayList<Palvelu> palveluLista;

    private ArrayList<ToimintaAlue> ta = new ArrayList<>(); // alustetaan lista johon haetaan toiminta-alueet

    private Palvelu valittuPalvelu; // tähän otetaan taulokosta valittu palvelu

    @FXML
    private ObservableList<Palvelu> obsPalveluLista; // tätä listaa käytetään tableviewin asettamiseen

    //Palveluhallinta näkymässä käytetty tableview
    @FXML
    private TableView<Palvelu> tvPalvelut;

    // Yllä olevan tableview sarake
    @FXML
    private TableColumn taSarake;

    // Yllä olevan tableview sarake
    @FXML
    private TableColumn nimiSarake;

    // Yllä olevan tableview sarake
    @FXML
    private TableColumn kuvausSarake;

    // Yllä olevan tableview sarake
    @FXML
    private TableColumn hintaSarake;

    // Yllä olevan tableview sarake
    @FXML
    private TableColumn idSarake;

    @FXML
    private Button btnEtusivulle;
    @FXML
    private VBox vBoxLeft;
    @FXML
    private ComboBox cbTAlueet;
    @FXML
    private TextField tfNimi;
    @FXML
    private TextArea taKuvaus;
    @FXML
    private TextField tfHinta;
    @FXML
    private Button btnLisaaKantaan;
    @FXML
    private Label lblIlmoitukset;
    @FXML
    private AnchorPane apKeski;
    @FXML
    private Button btnLisaaUusi;

    /**
     * Tämä metodi laukeaa kun palveluhallinta näkymä avataan sovelluksessa.
     */
    @FXML
    public void initialize() {
        alustaTaulukko(); //Alustetaan tableview sarakkeet
        lisaaArvotTaulukkoon(); // lisätään arvot taulukkoon
        //Stage ikkuna = (Stage) btnEtusivulle.getScene().getWindow(); // otetaan ylös stage jolla nappi sijaitsee
        //ikkuna.setTitle("Palvelunhallinta"); // asetetaan ikkunan otsikko
    }
    /**
     * Metodi sulkee nykyisen ikkunan.
     */
    public void palaaEtusivulle(){
        /*
        try {
            // Ladataan muistiin nykyisen ikkunan (aloitusruutu)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/aloitusRuutu.fxml"));
            Stage stage = (Stage) btnEtusivulle.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        }catch (IOException io){
            io.printStackTrace();
        }

         */
        Stage ikkuna = (Stage) btnLisaaKantaan.getScene().getWindow(); // otetaan ylös stage jolla nappi sijaitsee
        ikkuna.close(); // suljetaan ikkuna
    }

    /**
     * Metodi alustaa taulukon sarakkeet.
     */
    public void alustaTaulukko(){

        taSarake.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Palvelu, String>, ObservableValue<String>>() {
            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Palvelu, String> cellDataFeatures) {
                if (cellDataFeatures != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getToimintaAlue().getNimi());
                }
                else {
                    return new SimpleStringProperty("<ei nimeä>");
                }}});

        nimiSarake.setCellValueFactory(new PropertyValueFactory<Palvelu,String>("palveluNimi"));
        kuvausSarake.setCellValueFactory(new PropertyValueFactory<Palvelu,String>("kuvaus"));
        hintaSarake.setCellValueFactory(new PropertyValueFactory<Palvelu,Double>("hinta"));
        idSarake.setCellValueFactory(new PropertyValueFactory<Palvelu,Integer>("palveluId"));
    }

    /**
     * Metodi lisää kannasta haeutut palvelut sovelluksen taulukko näkymään.
     */
    public void lisaaArvotTaulukkoon() {
        Palvelu p = new Palvelu(); // luodaan uusi palvelu
        palveluLista = p.haePalvelut(); // haetaan toiminta-alueet kannasta
        obsPalveluLista = FXCollections.observableArrayList(); //alustetaan observable lista
        for (Palvelu palvelu: palveluLista) { // käydään toiminta-alue lista läpi ja lisätään palvelut observablelistalle
            obsPalveluLista.add(palvelu); // lisätään palvelu listaan
        }
        tvPalvelut.setItems(obsPalveluLista); // asetetaan taulukkonäkymän obs. listaksi alueiden nimet
    }

    /**
     * Metodi laittaa palvelun lisäyksen käytettäväksi ja mahdollistaa sen lisäyksen.
     */
    public void aloitaLisays(){
        btnLisaaUusi.setDisable(true); // aloitus nappi pois päältä
        tyhjaaArvot(); // tyhjennetään arvot
        apKeski.setDisable(true);//keski napit pois käytöstä jos ovat auki
        vBoxLeft.setDisable(false); // asetetaan lisäys näkyville
        tvPalvelut.setDisable(true); // estetään rivin valitseminen taulukosta
        btnLisaaKantaan.setText("Lisää"); // vaihdetaan lisäys napin teksi oikeaksi
        lblIlmoitukset.setVisible(false);

        ObservableList<String> alueet = FXCollections.observableArrayList(); //alustetaan observable list

        ta = ToimintaAlue.haeToimintaAlueet(); // haetaan kannasta alueet
        for (ToimintaAlue ta1: ta) { // lisätään alueet oblistaan
            String nimi = ta1.getNimi();
            alueet.add(nimi);
        }
        cbTAlueet.setItems(alueet); // laitetaan alueet comboboxiin

        tvPalvelut.getSelectionModel().clearSelection(); // TYHJENNETÄÄN VALINTA TAULUSTA
    }

    /**
     * Metodi peruuttaa palvelun lisäyksen. Tyhjää kaikki kentät jos niissä on jotain. Palauttaa ikkunan alkutilaan.
     */
    public void peruuta(){
        lblIlmoitukset.setVisible(false);// ohjeteksi pois näkyvistä
        vBoxLeft.setDisable(true); // laitetaan lisäys kiinni
        tvPalvelut.setDisable(false); // avataan taulokko
        cbTAlueet.setItems(null); // tyhjennetään combobox
        btnLisaaUusi.setDisable(false); //lisäysnappi saataville
        btnLisaaUusi.setDisable(false); // aloitus nappi auki

        //seuraavaksi asetetaan teksti kenttät tyhjiksi
        tfNimi.setText("");
        taKuvaus.setText("");
        tfHinta.setText("");
        tvPalvelut.getSelectionModel().clearSelection(); // TYHJENNETÄÄN VALINTA TAULUSTA
    }

    /**
     * Metodi lisää ja päivittää palvelun kantaan.
     */
    public void lisaa() {
        // tarkistetaan onko teksti kenttiin syötetty jotain. Jos ei annetaan virhe.
        if(tfNimi.getText().equals("") != true && taKuvaus.getText().equals("") != true) {
            if (btnLisaaKantaan.getText().equals("Lisää")) { // tutkitaan ollaanko lisäys tilassa ja jos ollaan mennään sisään
                try {
                    lblIlmoitukset.setVisible(false);// ohje teksi pois näkyvistä

                    ToimintaAlue ta = new ToimintaAlue(); // luodaan toiminta alue olio
                    Palvelu p = new Palvelu(); // luodaan palvelu
                    String alue = cbTAlueet.getSelectionModel().getSelectedItem().toString(); //otetaan comboboxista alueen nimi
                    ta = ToimintaAlue.haeYksiAlue(alue); // haetaan nimellä kannasta ja saadaan myös toiminta-alueen id
                    p.setToimintaAlue(ta); // asetetaan toiminta-alue palvelulle
                    p.setPalveluNimi(tfNimi.getText()); // asetataan Palvelun nimi
                    p.setKuvaus(taKuvaus.getText());
                    p.setToimintaAlue(ta); // asetetaan palvelun kuvaus
                    p.setHinta(Double.parseDouble(tfHinta.getText())); // asetetaan palvelun hinta

                    int rivit = p.lisaaPalvelu(); // lisätään palvelu kantaan ja otetaan ylös tulos

                    if (rivit >= 1) {  // tähän mennään jos lisäys onnistui
                        lblIlmoitukset.setVisible(true); // onnistumis teksi esille
                        lblIlmoitukset.setText("Onnistui");
                        lisaaArvotTaulukkoon(); // päivitetään juuri lisätty arvo taulokkoon
                        vBoxLeft.setDisable(true); // laitetaan lisäys kiinni
                        tvPalvelut.setDisable(false); // avataan taulokko
                        cbTAlueet.setItems(null); // tyhjennetään combobox
                        tyhjaaArvot(); // tyhjennetään arvot
                        btnLisaaUusi.setDisable(false); // lisäysnappi takaisin käyttöön
                        tvPalvelut.getSelectionModel().clearSelection(); // TYHJENNETÄÄN VALINTA TAULUSTA
                    } else {
                        lblIlmoitukset.setVisible(true); // ilmoitetaan epäonnistuminen
                        lblIlmoitukset.setText("Kantaan tallennus epäonnistui");
                    }

                } catch (Exception ex) {
                    lblIlmoitukset.setVisible(true); // jos virhettä muunnoksissa ohjelma antaa virheen ja mennään tänne
                    lblIlmoitukset.setText("Tarkista arvot");
                }
            } else { // tähän mennään muokkaus tilassa
                try {
                    lblIlmoitukset.setVisible(false);// ohje teksi pois näkyvistä

                    ToimintaAlue ta = new ToimintaAlue(); // luodaan toiminta alue olio
                    String alue = cbTAlueet.getSelectionModel().getSelectedItem().toString(); //otetaan comboboxista alueen nimi
                    ta = ToimintaAlue.haeYksiAlue(alue); // haetaan nimellä kannasta ja saadaan myös toiminta-alueen id
                    valittuPalvelu.setToimintaAlue(ta); // asetetaan toiminta-alue palvelulle
                    valittuPalvelu.setPalveluNimi(tfNimi.getText()); // asetataan Palvelun nimi
                    valittuPalvelu.setKuvaus(taKuvaus.getText()); // asetetaan palvelun kuvaus
                    valittuPalvelu.setHinta(Double.parseDouble(tfHinta.getText())); // asetetaan palvelun hinta

                    int rivit = valittuPalvelu.paivitaPalvelu(); // pivitetään kantaa ja otetaan ylös tulos

                    if (rivit >= 1) {
                        lblIlmoitukset.setVisible(true); // onnistumis teksi esille
                        lblIlmoitukset.setText("Onnistui");
                        lisaaArvotTaulukkoon(); // päivitetään juuri lisätty arvo taulokkoon
                        vBoxLeft.setDisable(true); // laitetaan lisäys kiinni
                        tvPalvelut.setDisable(false); // avataan taulokko
                        cbTAlueet.setItems(null); // tyhjennetään combobox
                        apKeski.setDisable(true);// keksi napit pois käytöstä
                        tyhjaaArvot(); // tyhjennetään arvot
                        btnLisaaUusi.setDisable(false); // lisäysnappi takaisin käyttöön
                        tvPalvelut.getSelectionModel().clearSelection(); // TYHJENNETÄÄN VALINTA TAULUSTA
                    } else {
                        lblIlmoitukset.setVisible(true);// ilmoitetaan epäonnistuminen
                        lblIlmoitukset.setText("Kantaan tallennus epäonnistui");
                    }
                } catch (Exception ex) {
                    lblIlmoitukset.setVisible(true);// jos virhettä muunnoksissa ohjelma antaa virheen ja mennään tänne
                    lblIlmoitukset.setText("Tarkista arvot");
                }
            }
        }
        else{
            lblIlmoitukset.setVisible(true);// jos virhettä muunnoksissa ohjelma antaa virheen ja mennään tänne
            lblIlmoitukset.setText("Täytä kaikki kentät");
        }
    }

    /**
     * Metodi ottaa arvot taulukosta ja lisää ne muokkaus näkymään
     */
    public void muokkaa(){
        lblIlmoitukset.setVisible(false);// ohje pois
        btnLisaaUusi.setDisable(true); // lisäys nappi pois käytöstä muokkauksen ajaksi
        vBoxLeft.setDisable(false); // asetetaan lisäys näkyville
        btnLisaaKantaan.setText("Päivitä"); // päivitetään napin asua
        valittuPalvelu = tvPalvelut.getSelectionModel().getSelectedItem(); // otetaan olio taulokosta
        // lisätään toiminta-alueet comboboxiin
        ObservableList<String> alueet = FXCollections.observableArrayList(); //alustetaan observable list
        ta = ToimintaAlue.haeToimintaAlueet(); // haetaan kannasta alueet
        for (ToimintaAlue ta1: ta) { // lisätään alueet oblistaan
            String nimi = ta1.getNimi();
            alueet.add(nimi);
        }
        int index = vertaaToimintaAlueet(ta ,valittuPalvelu.getToimintaAlue()); // etsitään oikea toiminta-alue listalta
        cbTAlueet.setItems(alueet); // asetetaan alueet comboboxiin
        cbTAlueet.getSelectionModel().select(index); // asetetaan comboboxiin oikea toiminta-alua näkyville
        //cbTAlueet.selectionModelProperty().setValue(valittuPalvelu.getToimintaAlue().getNimi());
        tfNimi.setText(valittuPalvelu.getPalveluNimi()); //asetetaan nimi kenttään teksti
        taKuvaus.setText(valittuPalvelu.getKuvaus()); // asetetaan kuvaus kenttään teksti
        tfHinta.setText(Double.toString(valittuPalvelu.getHinta())); // asetetataan hinta kenttään teksti

        apKeski.setDisable(true); // keski napit pois käytöstä
        tvPalvelut.setDisable(true); // taulokko pois käytöstä
    }

    /**
     * Metodi aktivoituu taulukkoa klikatessa ja valitsee arvon taulokosta.
     */
    public void valittuTableview(){
        if (tvPalvelut.getSelectionModel().getSelectedItem() != null) {
            apKeski.setDisable(false); // lisäys ja muokkaus napit esiin
            tyhjaaArvot(); // tyhjennetään arvot lisäykestä
            lblIlmoitukset.setVisible(true);
            lblIlmoitukset.setText("Palvelu on käytössä");
        }
    }

    /**
     * Metodi tyhjentää arvot kentistä.
     */
    public void tyhjaaArvot(){
        vBoxLeft.setDisable(true); // laitetaan lisäys kiinni
        tvPalvelut.setDisable(false); // avataan taulokko
        cbTAlueet.setItems(null); // tyhjennetään combobox

        //seuraavaksi asetetaan teksti kenttät tyhjiksi
        tfNimi.setText("");
        taKuvaus.setText("");
        tfHinta.setText("");
        vBoxLeft.setDisable(true);
    }

    /**
     * Metodi kysyy ensiksi poiston varmuutta. Poistaa taulukosta valitun palvelun kannasta.
     */
    public void poista(){
        lblIlmoitukset.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // määritetään ilmoitus
        alert.setTitle("Poisto"); // asetetaan otsikko ilmoituselle
        alert.setContentText("Haluatko varmasti poistaa palvelun?"); //lisätään teksti
        // Määritetään eka nappi ilmoitykseen
        ButtonType okButton = new ButtonType("Kyllä", ButtonBar.ButtonData.YES);
        // Määritetään eka nappi ilmoitykseen
        ButtonType noButton = new ButtonType("Ei", ButtonBar.ButtonData.NO);
        // asetetaan napit ilmoitukseen
        alert.getButtonTypes().setAll(okButton, noButton);

        //avataan ilmoitus ja jäähään odottamaan vastausta
        Optional<ButtonType> result = alert.showAndWait();

        if (result.orElse(okButton) == okButton){ // Tutkitana mitä on painettu jos kyllä niin aktivoidaan poisto
            valittuPalvelu = tvPalvelut.getSelectionModel().getSelectedItem(); // otetaan olio taulokosta
            int arvo = valittuPalvelu.poistaPalvelu(); //postetaan palvelu kannasta ja otetaan ylös tulos
            if (arvo >=1){ // jos onnistunut mennään tähän
                lisaaArvotTaulukkoon(); // päivitetään taulukko
                lblIlmoitukset.setVisible(true); // ilmoitetaan poiston onnistimisesta
                lblIlmoitukset.setText("Onnistui");
                apKeski.setDisable(true); // napit pois käytöstä
            }
            else {
                lblIlmoitukset.setVisible(true);
                lblIlmoitukset.setText("Poisto epäonnistui. Palveluun liittyy varauksia."); // ilmoitetaan poiston epäonnistumisesta
            }
        }
    }

    /**
     * Metodi vertailee toiminta-aluetta toiminta-alue listan kanssa.
     *
     * @param alueet
     * @param ta
     * @return indexin paikan jossa on haluttu toiminta-alue
     */
    public int vertaaToimintaAlueet(ArrayList<ToimintaAlue> alueet,ToimintaAlue ta){
        int index = 0;
        for(int i = 0; i < alueet.size();i++){
            if (alueet.get(i).getToimintaalue_id() == ta.getToimintaalue_id()){ // vertaillaan listan Toimintaalue_id:tä ja annetun alueean getToimintaalue_id:tä
                index = i;
            }
        }
        return index;
    }
    /**
     * Metodi avaa pdf ohjeen selaimeen.
     */
    public void avaaOhje() {
        Ohje.avaaOhje();
    }
}
