/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.meteo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.meteo.model.Model;
import it.polito.tdp.meteo.model.Rilevamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxMese"
    private ChoiceBox<String> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnUmidita"
    private Button btnUmidita; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalcola"
    private Button btnCalcola; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaSequenza(ActionEvent event) {
    	txtResult.clear();
    	Integer mese = 0;
    	try {
    		mese = this.convertiMese(boxMese.getValue());
    	} catch (NullPointerException e) {
    		txtResult.setText("Selezionare un mese su cui si desidera calcolare "
    				+ "l'umidità media per le città presenti nel database\n");
    		return;
    	}
    	if(mese.equals(0)) {
    		txtResult.setText("Selezionare un mese su cui si desidera calcolare "
    				+ "l'umidità media per le città presenti nel database\n");
    		return;
    	}
    	List<Rilevamento> migliorPercorso = this.model.trovaSequenza(mese);
    	if(migliorPercorso.isEmpty()) {
    		txtResult.setText("Nessuna soluzione possibile trovata coi valori specificati\n");
    		return;
    	}
    	txtResult.setText("Le tappe che permettono di minimizzare il costo del calcolo dell'umidità "
    			+ "nel mese di " + boxMese.getValue() + " ottenendo un costo di "
    					+ this.model.getBestCosto() + ", sottoposte ai vincoli specificati, sono: \n");
    	for(Rilevamento r : migliorPercorso) {
    		txtResult.appendText(r.toString() + "\n");
    	}
    }

    @FXML
    void doCalcolaUmidita(ActionEvent event) {
    	txtResult.clear();
    	Integer mese = 0;
    	try {
    		mese = this.convertiMese(boxMese.getValue());
    	} catch (NullPointerException e) {
    		txtResult.setText("Selezionare un mese su cui si desidera calcolare "
    				+ "l'umidità media per le città presenti nel database\n");
    		return;
    	}
    	if(mese.equals(0)) {
    		txtResult.setText("Selezionare un mese su cui si desidera calcolare "
    				+ "l'umidità media per le città presenti nel database\n");
    		return;
    	}
    	List<List<Rilevamento>> risultato = this.model.getUmiditaMedia(mese);
    	for(List<Rilevamento> risultatoPerCitta : risultato) {
    		int somma = 0;
    		int dati = 0;
    		for(Rilevamento r : risultatoPerCitta) {
    			somma += r.getUmidita();
    			dati ++;
    		}
    		txtResult.appendText("L'umidità media nel mese di " + boxMese.getValue().toLowerCase() + 
    				" nella città di " + risultatoPerCitta.get(0).getLocalita()
    				+ " è stata: " + (somma/dati) + "%\n");
    	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {

		this.model = model;
		boxMese.getItems().addAll("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio",
				"Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
	}
	
	private Integer convertiMese(String mese) {
		
		switch (mese) {
			case "Gennaio" : return 1;
			case "Febbraio" : return 2;
			case "Marzo" : return 3;
			case "Aprile" : return 4;
			case "Maggio" : return 5;
			case "Giugno" : return 6;
			case "Luglio" : return 7;
			case "Agosto" : return 8;
			case "Settembre" : return 9;
			case "Ottobre" : return 10;
			case "Novembre" : return 11;
			case "Dicembre" : return 12;
			default : return 0;
		}
	}
}

