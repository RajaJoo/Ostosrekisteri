package fxOstosrekisteri;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import fi.jyu.mit.ohj2.Mjonot;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ostosrekisteri.Ostos;

/**
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016 Luodaan kontrolleriluokka ostotapahtumien lisaamiselle ja
 *          muokkaamiselle
 */
public class LisaaOstotapahtumaController implements ModalControllerInterface<Ostos>, Initializable {

	@FXML
	private Button peruutaNappi;
	@FXML
	private Button tallennaNappi;
	@FXML
	private Label labelVirhe;

	@FXML
	private GridPane gridi;
	@FXML
	private ScrollPane panelOstos;
	@FXML
	private DatePicker ostoPvm;

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		alusta();
	}

	@FXML
	void peruutaNappiToiminto() {
		ostosKohdalla = null;
		ModalController.closeStage(labelVirhe);
	}

	/**
	 * Sulkee dialogin ja samalla asettaa p‰iv‰m‰‰r‰n ostokselle. Ei anna painaa
	 * jos nimi tai p‰iv‰m‰‰r‰ tyhji‰.
	 */
	@FXML
	private void handleOK() {
		if (ostosKohdalla != null && ostosKohdalla.getTuote().trim().equals("")) {
			naytaVirhe("Nimi ei saa olla tyhj‰");
			return;
		}
		LocalDate pvm = ostoPvm.getValue();
		if (pvm == null) {
			naytaVirhe("Valitseppas p‰iv‰m‰‰r‰");
			return;
		}
		ostosKohdalla.setDate(naytaDate(0), naytaDate(1), naytaDate(2));
		ModalController.closeStage(labelVirhe);
	}

	// =============================================================================================================

	private Ostos ostosKohdalla;
	private static Ostos apuostos = new Ostos(); // J‰sen jolta voidaan kysell‰
													// tietoja.
	private TextField[] edits;
	private int kentta = 0;

	/**
	 * Aliohjelma joka poimii tiedot p‰iv‰m‰‰r‰lle date pickerist‰.
	 * 
	 * @param k eli keissin valitsija
	 * @return haluttu p‰iv‰m‰‰r‰n osa
	 */
	public int naytaDate(int k) {
		LocalDate pvm = ostoPvm.getValue();

		switch (k) {
		case 0:
			return pvm.getDayOfMonth();
		case 1:
			return pvm.getMonthValue();
		case 2:
			return pvm.getYear();
		default:
			return 0;
		}
	}

	/**
	 * Luodaan GridPaneen ostoksen tiedot
	 * 
	 * @param gridi mihin tiedot luodaan
	 * @return luodut tekstikent‰t
	 */
	public static TextField[] luoKentat(GridPane gridi) {
		gridi.getChildren().clear();
		TextField[] edits = new TextField[apuostos.getKenttia()];

		for (int i = 0, k = apuostos.ekaKentta(); k < apuostos.getKenttia(); k++, i++) {
			Label label = new Label(apuostos.getKysymys(k));
			gridi.add(label, 0, i);
			TextField edit = new TextField();
			edits[k] = edit;
			edit.setId("e" + k);
			gridi.add(edit, 1, i);
		}
		return edits;
	}
	

	/**
	 * Palautetaan komponentin id:st‰ saatava luku
	 * 
	 * @param obj
	 *            tutkittava komponentti
	 * @param oletus
	 *            mik‰ arvo jos id ei ole kunnollinen
	 * @return komponentin id lukuna
	 */
	public static int getFieldId(Object obj, int oletus) {
		if (!(obj instanceof Node))
			return oletus;
		Node node = (Node) obj;
		return Mjonot.erotaInt(node.getId().substring(1), oletus);
	}
	

	/**
	 * Tekee tarvittavat muut alustukset, nyt vaihdetaan GridPanen tilalle yksi
	 * iso tekstikentt‰, johon voidaan tulostaa ostoksen tiedot.
	 */
	protected void alusta() {
		edits = luoKentat(gridi);

		for (TextField edit : edits)
			if (edit != null)
				edit.setOnKeyReleased(e -> kasitteleMuutosOstokseen((TextField) (e.getSource())));
		panelOstos.setFitToHeight(true);
	}
	

	@Override
	public Ostos getResult() {
		return ostosKohdalla;
	}
	

	private void setKentta(int kentta) {
		this.kentta = kentta;
	}
	

	/**
	 * Mit‰ tehd‰‰n kun dialogi on n‰ytetty
	 */
	@Override
	public void handleShown() {
		kentta = Math.max(apuostos.ekaKentta(), Math.min(kentta, apuostos.getKenttia() - 1));
		edits[kentta].requestFocus();
	}
	

	/**
	 * K‰sitell‰‰n ostokseen tullut muutos
	 * 
	 * @param edit
	 *            muuttunut kentt‰
	 */
	protected void kasitteleMuutosOstokseen(TextField edit) {
		if (ostosKohdalla == null)
			return;
		int k = getFieldId(edit, apuostos.ekaKentta());
		String s = edit.getText();
		String virhe = null;
		virhe = ostosKohdalla.aseta(k, s);
		if (virhe == null) {
			Dialogs.setToolTipText(edit, "");
			edit.getStyleClass().removeAll("virhe");
			naytaVirhe(virhe);
		} else {
			Dialogs.setToolTipText(edit, virhe);
			edit.getStyleClass().add("virhe");
			naytaVirhe(virhe);
		}
	}
	

	/**
	 * N‰ytet‰‰n ostoksen tiedot TextField komponentteihin
	 * 
	 * @param edits
	 *            taulukko TextFieldeist‰ johon n‰ytet‰‰n
	 * @param ostos
	 */
	public static void naytaOstos(TextField[] edits, Ostos ostos) {
		if (ostos == null)
			return;
		for (int k = ostos.ekaKentta(); k < ostos.getKenttia(); k++) {
			edits[k].setText(ostos.anna(k));
		}
	}
	

	/**
	 * Luodaan ostoksen kysymisdialogi ja palautetaan sama tietue muutettuna tai
	 * null
	 * 
	 * @param modalityStage
	 *            mille ollaan modaalisia, null = sovellukselle
	 * @param oletus
	 *            mit‰ dataan n‰ytet‰‰n oletuksena
	 * @param kentta
	 *            mik‰ kentt‰ saa fokuksen kun n‰ytet‰‰n
	 * @return null jos painetaan Cancel, muuten t‰ytetty tietue
	 */
	public static Ostos kysyOstos(Stage modalityStage, Ostos oletus, int kentta) {
		return ModalController.<Ostos, LisaaOstotapahtumaController> showModal(
				LisaaOstotapahtumaController.class.getResource("LisaaOstotapahtumaView.fxml"), "Ostosrekisteri",
				modalityStage, oletus, ctrl -> ctrl.setKentta(kentta)

		);
	}
	

	@Override
	public void setDefault(Ostos oletus) {
		ostosKohdalla = oletus;
		naytaOstos(edits, ostosKohdalla);
	}

	private void naytaVirhe(String virhe) {
		if (virhe == null || virhe.isEmpty()) {
			labelVirhe.setText("");
			labelVirhe.getStyleClass().removeAll("virhe");
			return;
		}
		labelVirhe.setText(virhe);
		labelVirhe.getStyleClass().add("virhe");
	}

}
