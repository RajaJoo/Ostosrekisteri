package fxOstosrekisteri;

import java.io.PrintStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import fi.jyu.mit.fxgui.*;
import fi.jyu.mit.fxgui.ModalController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ostosrekisteri.Kategoria;
import ostosrekisteri.Kategoriat;
import ostosrekisteri.Ostos;
import ostosrekisteri.Ostosrekisteri;
import ostosrekisteri.SailoException;

/**
 * Ohjain kayttoliittymasta tuleville tapahtumille
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *
 */
public class OstosrekisteriGUIController implements Initializable {

	@FXML
	private ComboBox<String> cbKuukaudet;
	@FXML
	private ComboBox<String> cbKategoriat;
	@FXML
	private StringGrid<Ostos> tableOstokset;
	@FXML
	private ListChooser chooserOstokset;
	@FXML
	private ScrollPane panelOstos;

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		alusta();
	}

	/**
	 * Avaa dialogin ostosten lisaamiselle
	 */
	@FXML
	void handleUusiOstos() {
		uusiOstos();

	}

	/**
	 * Avaa saman dialogin kuin ostosten lisaamisessa mutta valmiilla "arvoilla"
	 * 
	 * @throws SailoException
	 */
	@FXML
	void handleMuokkaaOstosta() {
		muokkaaOstosta();
	}

	/**
	 * Poistaa ostotapahtuman listasta, kysyy viela varmistusta ennen lopullista
	 * poistoa
	 */
	@FXML
	void handlePoistaOstos() {
		poistaOstos();
	}

	/**
	 * Lisää valitulle ostokselle uuden kategorian. Voi lisätä monta.
	 */
	@FXML
	private void handleUusiKategoria() {
		uusiKategoria();
	}

	@FXML
	private void handleHaku() {
		haku(0);
	}
	
	// ============================================================================================================================================================================
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ============================================================================================================================================================================

	private Ostosrekisteri ostosrekisteri;
	private Ostos ostosKohdalla;
	private TextArea areaOstos = new TextArea();
	private ListView<Ostos> listOstokset = new ListView<Ostos>();
	private ObservableList<Ostos> listdataOstokset = FXCollections.observableArrayList();

	/**
	 * Luokka, jolla hoidellaan miten ostos näytetään listassa
	 */
	public static class CellOstos extends ListCell<Ostos> {
		@Override
		protected void updateItem(Ostos item, boolean empty) {
			super.updateItem(item, empty); // Jotta valinta näkyy
			setText(empty ? "" : item.getMaara() + "x" + " " + item.getTuote() + " | " + item.getYhteisHinta() + "e"); // väliaikainen
																													// esitystapa
																													// SAA
																													// MUUTTAA
		}
	}

	/**
	 * Väliaikainen ratkaisu joka alustaa ohjelman niin notta niihin tyhjiin
	 * tekstikenttiin voipi tulostella ja semmosta.
	 */
	protected void alusta() {
		cbKuukaudet.getSelectionModel().select(0);
		cbKategoriat.getSelectionModel().select(0);
		panelOstos.setContent(areaOstos);
		areaOstos.setFont(new Font("Courier New", 12));
		panelOstos.setFitToHeight(true);
		BorderPane parent = (BorderPane) chooserOstokset.getParent();
		parent.setCenter(listOstokset);
		listOstokset.setCellFactory(p -> new CellOstos());
		listOstokset.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			naytaOstos();
		});
		

	}
	
	/**
	 * Aliohjelma joka karsii ostokset kuukauden mukaan
	 * @param ostosnro
	 */
	protected void haku(int ostosnro) {
		int k = cbKuukaudet.getSelectionModel().getSelectedIndex();
		k += 1;
		listdataOstokset.clear();
		listOstokset.setItems(listdataOstokset);
		int index = 0;
		for (int i = 0; i < ostosrekisteri.getOstokset(); i++) {
			Ostos ostos = ostosrekisteri.getOstos(i);
			if (ostos.getOstosnumero() == ostosnro)
				index = i;
			if (ostos.getKK() == k)
			listdataOstokset.add(ostos);
		}
		listOstokset.getSelectionModel().select(index);
	}

	/**
	 * Hakee ostoksen tiedot
	 * 
	 * @param ostosnro
	 */
	protected void hae(int ostosnro) {
		listdataOstokset.clear();
		listOstokset.setItems(listdataOstokset);
		int index = 0;
		for (int i = 0; i < ostosrekisteri.getOstokset(); i++) {
			Ostos ostos = ostosrekisteri.getOstos(i);
			if (ostos.getOstosnumero() == ostosnro)
				index = i;
			listdataOstokset.add(ostos);
		}
		listOstokset.getSelectionModel().select(index); // tästä tulee
														// muutosviesti joka
														// näyttää ostoksen
	}

	/**
	 * Näyttää listasta valitun ostoksen tiedot tekstikenttään
	 */
	protected void naytaOstos() {
		ostosKohdalla = listOstokset.getSelectionModel().getSelectedItem();

		if (ostosKohdalla == null)
			return;

		areaOstos.setText("");
		try (PrintStream os = TextAreaOutputStream.getTextPrintStream(areaOstos)) {
			tulosta(os, ostosKohdalla);
		}
	}

	/**
	 * Tulostaa ostoksen tiedot
	 * 
	 * @param os
	 *            tietovirta johon tulostetaan
	 * @param ostos
	 *            tulostettava ostos
	 */
	public void tulosta(PrintStream os, final Ostos ostos) {
		os.println("----------------------------------------------");
		ostos.tulosta(os);
		os.println("----------------------------------------------");
		os.println("Kategoriat:");
		// if ( ostos == null ) return; EI TAIDA VAIKUTTAA MIHINKÄÄN

		List<Kategoria> kategoriat = ostosrekisteri.annaKategoriat(ostos);
		if (kategoriat.size() == 0)
			return;
		for (Kategoria kat : kategoriat)
			kat.tulosta(os);

	}

	/**
	 * Tarkistetaan onko tallennus tehty
	 * @return true jos saa sulkea sovelluksen, false jos ei
	 */
	public boolean voikoSulkea() {
		tallenna();
		return true;
	}

	/**
	 * Tietojen tallennus
	 */
	private String tallenna() {
		try {
			ostosrekisteri.tallenna();
			return null;
		} catch (SailoException ex) {
			Dialogs.showMessageDialog("Tallennuksessa ongelma!" + ex.getMessage());
			return ex.getMessage();
		}
	}

	/**
	 * Asettaa ostosrekisterin
	 * 
	 * @param ostosrekisteri
	 */
	public void setOstosrekisteri(Ostosrekisteri ostosrekisteri) {
		this.ostosrekisteri = ostosrekisteri;

	}

	/**
	 * Kysytään tiedoston nimi ja luetaan se
	 * 
	 * @return true jos onnistui, false jos ei
	 */
	public boolean avaa() {
		String uusinimi = "Ostosrekisteri";
		lueTiedosto(uusinimi);
		return true;
	}

	/**
	 * Luetaan tiedostosta
	 * 
	 * @param nimi
	 *            tiedosto josta luetaan
	 * @return null jos onnistuu, muuten palauttaa virheilmoituksen
	 */
	protected String lueTiedosto(String nimi) {
		try {
			ostosrekisteri.lueTiedostosta(nimi);
			hae(0);
			return null;
		} catch (SailoException e) {
			hae(0);
			String virhe = e.getMessage();
			if (virhe != null)
				Dialogs.showMessageDialog(virhe);
			return virhe;
		}
	}

	/**
	 * Luo uuden ostoksen ja avaa dialogin tietojen syötölle.
	 */
	protected void uusiOstos() {
		try {
			Ostos uusi = new Ostos();
			uusi = LisaaOstotapahtumaController.kysyOstos(null, uusi, 0);
			if (uusi == null)
				return;
			uusi.rekisteroi();
			ostosrekisteri.lisaa(uusi);
			haku(uusi.getOstosnumero());
		} catch (SailoException e) {
			Dialogs.showMessageDialog("Ongelmia uuden luomisessa " + e.getMessage());
			return;
		}
	}

	/**
	 * Metodi ostoksen poistamiseen
	 * 
	 */
	public void poistaOstos() {
		Ostos ostos = getOstosKohdalla();
		if (ostos == null)
			return;
		if (!Dialogs.showQuestionDialog("Poisto", "Poistetaanko ostos: " + ostos.getTuote(), "Kyllä", "Ei"))
			return;
		ostosrekisteri.poista(ostos.getOstosnumero());
		int index = listOstokset.getSelectionModel().getSelectedIndex();
		hae(0);
		listOstokset.getSelectionModel().select(index);
	}

	/**
	 * Antaa listasta valitun ostoksen
	 * 
	 * @return listatsta valittu kohdalla oleva ostos
	 */
	private Ostos getOstosKohdalla() {
		return listOstokset.getSelectionModel().getSelectedItem();
	}

	/**
	 * Sama kuin uusiostos, mutta ostoksen tiedoilla joita voi sitten muokata.
	 */
	protected void muokkaaOstosta() {
		try {
			ostosKohdalla = LisaaOstotapahtumaController.kysyOstos(null, ostosKohdalla, 0);
			if (ostosKohdalla == null)
				return;
			hae(ostosKohdalla.getOstosnumero());
		} catch (Exception e) {
			Dialogs.showMessageDialog("Ongelmia muuttamisessa " + e.getMessage());
			return;
		}
	}

	/**
	 * Tekee uuden kategorian ostokselle. Kategoria vetovalikosta.
	 */
	public void uusiKategoria() {
		if (ostosKohdalla == null)
			return;
		try {
			Kategoria uusi = new Kategoria(ostosKohdalla.getOstosnumero());
			// if ( uusi == null ) return;
			uusi.setNimi(valittuKategoria());
			uusi.rekisteroi();
			ostosrekisteri.lisaa(uusi);
			naytaOstos();
		} catch (SailoException e) {
			Dialogs.showMessageDialog("Lisääminen epäonnistui: " + e.getMessage());
		}
	}

	/**
	 * Palauttaa vetovalikosta valitun kategorian uusiKategoria aliohjelmalle.
	 * 
	 * @return valittu kategoria.
	 */
	public String valittuKategoria() {
		int k = cbKategoriat.getSelectionModel().getSelectedIndex();
		
		switch (k) {
		case 0:
			return "Juomat";
		case 1:
			return "Ruuat";
		case 2:
			return "Siivoustarvikkeet";
		case 3:
			return "Herkut";
		case 4:
			return "Alkoholi";
		case 5:
			return "Muut";
		default:
			return null;
		}

	}

}
