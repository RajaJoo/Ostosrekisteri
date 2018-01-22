package fxOstosrekisteri;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ostosrekisteri.Ostosrekisteri;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

/**
 * @author Lauri, Joonas
 * @version 28.7.2016 Main - luokka ostosrekisterille.
 */
public class OstosrekisteriMain extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader ldr = new FXMLLoader(getClass().getResource("OstosrekisteriGUIView.fxml"));
			final Pane root = (Pane) ldr.load();
			final OstosrekisteriGUIController ostosrekisteriCtrl = (OstosrekisteriGUIController) ldr.getController();

			final Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("ostosrekisteri.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Ostosrekisteri");

			primaryStage.setOnCloseRequest((event) -> {
				if (!ostosrekisteriCtrl.voikoSulkea())
					event.consume();
			});

			Ostosrekisteri ostosrekisteri = new Ostosrekisteri();
			ostosrekisteriCtrl.setOstosrekisteri(ostosrekisteri);
			primaryStage.show();
			if (!ostosrekisteriCtrl.avaa())
				Platform.exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Käynnistetään käyttöliittymä
	 * 
	 * @param args
	 *            komentorivin parametrit
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
