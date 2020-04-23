package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO mDAO;
	private int bestCosto;
	private List<Rilevamento> bestSoluzione;
	private Map<String, List<Rilevamento>> rilevamentiPerCitta;

	public Model() {
		mDAO = new MeteoDAO();
	}

	public List<List<Rilevamento>> getUmiditaMedia(int mese) {
		List<List<Rilevamento>> rilevamentiPerCitta = new ArrayList<>();
		for(String localita : mDAO.getCittaDB())
			rilevamentiPerCitta.add(mDAO.getAllRilevamentiLocalitaMese(mese, localita));
		return rilevamentiPerCitta;
	}
	
	public List<Rilevamento> trovaSequenza(int mese) {
		bestSoluzione = new ArrayList<Rilevamento>();
		rilevamentiPerCitta = new HashMap<>();
		for(String localita : mDAO.getCittaDB()) {
			rilevamentiPerCitta.put(localita, new ArrayList<>(mDAO.getRilevamenti15giorni(mese, localita)));
		}
		bestCosto = -1;
		ricorsione(new ArrayList<Rilevamento>(), 0, "", 0, mDAO.getCittaDB());
		return bestSoluzione;
	}
	
	@SuppressWarnings("deprecation")
	private void ricorsione(List<Rilevamento> parziale, int l, String cittaAttuale, int consecutivi, 
			List<String> cittaRimanenti) {
		
		if(l == (NUMERO_GIORNI_TOTALI)) {
			if(consecutivi >= NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN && cittaRimanenti.isEmpty()) {
				if(bestCosto == -1) {
					bestSoluzione = new ArrayList<>(parziale);
					bestCosto = costoPerGiornata(parziale);
					return;
				}
				else if(costoPerGiornata(parziale) < bestCosto) {
					bestSoluzione = new ArrayList<>(parziale);
					bestCosto = costoPerGiornata(parziale);
					return;
				}
			}
			return;
		}	
	
		if(l>NUMERO_GIORNI_CITTA_MAX) {
			Map<String, Integer> frequenza = new HashMap<>();
			for(Rilevamento r : parziale) {
				if(frequenza.containsKey(r.getLocalita()))
					frequenza.put(r.getLocalita(), frequenza.get(r.getLocalita()) + 1);
				else frequenza.put(r.getLocalita(), 1);
			}
			for(Integer i : frequenza.values()) {
				if(i>NUMERO_GIORNI_CITTA_MAX)
					return;
			}
		}
		
		if(consecutivi > 0 && consecutivi < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			boolean trovato = false;
			for(Rilevamento ril : rilevamentiPerCitta.get(cittaAttuale)) {
				if(ril.getData().getDate() == (l+1) && !trovato) {
					parziale.add(rilevamentiPerCitta.get(cittaAttuale).get(l));
					trovato = true;
					ricorsione(parziale, l+1, cittaAttuale, consecutivi+1, cittaRimanenti);
					return;
				}
			}
		}
		
		for(List<Rilevamento> r : rilevamentiPerCitta.values()) {
			boolean ok = false;
			for(Rilevamento ril : r) {
				if(ril.getData().getDate() == (l+1) && !ok) {
					parziale.add(ril);
					ok = true;
					List<String> copia = new ArrayList<>(cittaRimanenti);
					copia.remove(ril.getLocalita());
			
					if(ril.getLocalita().equals(cittaAttuale))
						ricorsione(parziale, l+1, cittaAttuale, consecutivi+1, copia);
					else ricorsione(parziale, l+1, ril.getLocalita(), 1, copia); 
			
					parziale.remove(parziale.size()-1);
				}
			}
		}
		
	}
	
	public int getBestCosto() {
		return bestCosto;
	}
	
	private int costoPerGiornata(List<Rilevamento> rilevamenti) {
		
		int costo = 0;
		for(Rilevamento r : rilevamenti) {
			if(rilevamenti.indexOf(r) > 0) {
				if(!r.getLocalita().equals(rilevamenti.get(rilevamenti.indexOf(r)-1).getLocalita())) {
					costo += 100;
				}	
			}
		}
		for(Rilevamento r : rilevamenti) {
			costo += r.getUmidita();
		}
		return costo;
	}

}
