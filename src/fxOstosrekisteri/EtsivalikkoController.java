package fxOstosrekisteri;

import fi.jyu.mit.fxgui.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *Luokka Etsi -valikon toimintoja varten. Turha lopullisessa versiossa. Voi halutessa toki tehdä.
 */
public class EtsivalikkoController {

    @FXML private TextField textHakusanat;

    @FXML private TextField textTuotemerkki;

    @FXML private Button peruutaNappi;

    @FXML void handleEtsiOstosVarmistus() {
        Dialogs.showMessageDialog("En osaa vielä etsiä ostosta!");
    }

    @FXML void peruutaNappiToiminto() {
     // handle "stageen"
        Stage stage = (Stage) peruutaNappi.getScene().getWindow();
        
        stage.close(); // suljetaan
    }

}