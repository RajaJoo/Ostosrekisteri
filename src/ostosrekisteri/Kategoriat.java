package ostosrekisteri;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * kategoriat -luokka, joka pitää kategorioita "tallessa" ja osaa esim lisätä
 * niitä
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *
 */
public class Kategoriat implements Iterable<Kategoria> {

	private String tiedostonPerusNimi = "";
	private boolean muutettu = false;

	/**
	 * kategoriat taulukossa
	 */
	private final List<Kategoria> alkiot = new ArrayList<Kategoria>();
	

	/**
	 * Muodostaja
	 */
	public Kategoriat() {
		//
	}
	

	/**
	 * Korvaa kategorian tietorakenteessa. Ottaa ostoksen omistukseensa.
	 * Etsitään samalla tunnusnumerolla oleva kategoria. Jos ei löydy, niin
	 * lisätään uutena.
	 * 
	 * @param kat lisättävä kategoria
	 * @throws SailoException jos tietorakenne on ongelmia
	 */
	public void korvaaTaiLisaa(Kategoria kat) throws SailoException {
		int id = kat.getKattunnusnumero();
		for (int i = 0; i < alkiot.size(); i++) {
			if (alkiot.get(i).getKattunnusnumero() == id) {
				alkiot.set(i, kat);
				muutettu = true;
				return;
			}
		}
		lisaa(kat);
	}
	

	/**
	 * Lisää uuden kategorian tietorakenteeseen. Ottaa kategorian omistukseensa.
	 * 
	 * @param kat lisättävä kategoria.
	 */
	public void lisaa(Kategoria kat) {
		alkiot.add(kat);
		muutettu = true;
	}
	

	/**
	 * Lukee ostokset tiedostosta.
	 * 
	 * @param tied
	 *            tiedoston hakemisto
	 * @throws SailoException
	 *             jos lukeminen epäonnistuu
	 * @example
     * <pre name="test">
     * #THROWS SailoException
     * #import java.io.File;
     * #import java.util.*;
     * Kategoriat kattesti = new Kategoriat();
     * Kategoria testi1 = new Kategoria(), testi2 = new Kategoria(), testi3 = new Kategoria(), testi4 = new Kategoria();
     * testi1.jeesiKategoria(1);
     * testi2.jeesiKategoria(2);
     * testi3.jeesiKategoria(3);
     * testi4.jeesiKategoria(4);
     * String tiedNimi = "testikategoriat";
     * File katfile = new File(tiedNimi+".dat");
     * katfile.delete();
     * kattesti.lueTiedostosta(tiedNimi); #THROWS SailoException
     * kattesti.lisaa(testi1);
     * kattesti.lisaa(testi2);
     * kattesti.lisaa(testi3);
     * kattesti.lisaa(testi4);
     * kattesti.tallenna();
     * Kategoriat kattesti2 = new Kategoriat();
     * kattesti2.lueTiedostosta(tiedNimi);
     * Iterator<Kategoria> i = kattesti2.iterator();
     * i.next().toString() === testi1.toString();
     * i.next().toString() === testi2.toString();
     * i.next().toString() === testi3.toString();
     * i.next().toString() === testi4.toString();
     * i.hasNext() === false;
     * kattesti2.lisaa(testi1);
     * kattesti2.tallenna();
     * katfile.delete() === true;
     * File bakup = new File(tiedNimi+".bak");
     * bakup.delete() === true;
     * </pre>
	 */
	public void lueTiedostosta(String tied) throws SailoException {
		setTiedostonPerusNimi(tied);
		try (BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi()))) {

			String rivi;
			while ((rivi = fi.readLine()) != null) {
				rivi = rivi.trim();
				if ("".equals(rivi) || rivi.charAt(0) == ';')
					continue;
				Kategoria kategoria = new Kategoria();
				kategoria.parse(rivi);
				lisaa(kategoria);
			}
			muutettu = false;

		} catch (FileNotFoundException e) {
			throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea.");
		} catch (IOException e) {
			throw new SailoException("Ongelma tiedostossa: " + e.getMessage());
		}
	}
	

	/**
	 * Lukee ostokset jo nimetystä tiedostosta
	 * 
	 * @throws SailoException
	 *             jos joku menee pieleen
	 */
	public void lueTiedostosta() throws SailoException {
		lueTiedostosta(getTiedostonPerusNimi());
	}
	

	/**
	 * Tallentaa ostokset tiedostoon.
	 * 
	 * @throws SailoException
	 *             jos talletus epäonnistuu
	 */
	public void tallenna() throws SailoException {
		if (!muutettu)
			return;

		File fbak = new File(getBakNimi());
		File ftied = new File(getTiedostonNimi());
		fbak.delete();
		ftied.renameTo(fbak);

		try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))) {
			for (Kategoria kat : this) {
				fo.println(kat.toString());
			}
		} catch (FileNotFoundException ex) {
			throw new SailoException("Tiedosto: " + ftied.getName() + " ei aukea.");
		} catch (IOException e) {
			throw new SailoException("Tiedostoon: " + ftied.getName() + " kirjoittaminen ei onnistunut.");
		}

		muutettu = false;
	}
	

	/**
	 * Asettaa tiedostolle perusnimen
	 * 
	 * @param tied
	 *            nimi joka asetetaan
	 */
	public void setTiedostonPerusNimi(String tied) {
		tiedostonPerusNimi = tied;
	}
	

	/**
	 * Palauttaa tallentamiseen käytettävän tiedoston nimen
	 * 
	 * @return tiedostonnimi
	 */
	public String getTiedostonPerusNimi() {
		return tiedostonPerusNimi;
	}
	

	/**
	 * Palauttaa tallennettavan tiedoston nimen
	 * 
	 * @return tallennettavan tiedoston nimi
	 */
	public String getTiedostonNimi() {
		return tiedostonPerusNimi + ".dat";
	}
	

	/**
	 * Palauttaa backup-tiedoston nimen
	 * 
	 * @return backupin nimi
	 */
	public String getBakNimi() {
		return tiedostonPerusNimi + ".bak";
	}
	

	/**
	 * Palauttaa kategorioiden lukumäärän
	 * 
	 * @return kategoriat lukumäärä
	 */
	public int getLkm() {
		return alkiot.size();
	}
	

	/**
	 * Iteraattori kategorioiden läpikäymiseen
	 * @example
	 * <pre name="test">
	 * #PACKAGEIMPORT
	 * #import java.util.*;
	 * 
	 * Kategoriat kategoriat = new Kategoriat();
	 * Kategoria kategoria1 = new Kategoria(1); kategoriat.lisaa(kategoria1);
	 * Kategoria kategoria2 = new Kategoria(2); kategoriat.lisaa(kategoria2);
	 * Kategoria kategoria3 = new Kategoria(1); kategoriat.lisaa(kategoria3);
	 * Kategoria kategoria4 = new Kategoria(2); kategoriat.lisaa(kategoria4);
	 * Kategoria kategoria5 = new Kategoria(2); kategoriat.lisaa(kategoria5);
	 * 
	 * Iterator<Kategoria>iteraattori = kategoriat.iterator();
	 * 
	 * iteraattori.next() === kategoria1;
	 * iteraattori.next() === kategoria2;
	 * iteraattori.next() === kategoria3;
	 * iteraattori.next() === kategoria4;
	 * iteraattori.next() === kategoria5;
	 * iteraattori.hasNext() === false;
	 * iteraattori.next() === kategoria1 #THROWS NoSuchElementException
	 * </pre>
	 */
	@Override
	public Iterator<Kategoria> iterator() {
		return alkiot.iterator();
	}
	

	/**
	 * Haetaan yhden ostoksen kaikki kategoriat
	 * 
	 * @param ostosnumero
	 * @return lista löydetyistä
	 * @example
	 * <pre name="test">
	 * #import java.util.*;
	 * 
	 * Kategoriat kategoriat = new Kategoriat();
	 * Kategoria testi1 = new Kategoria(1); kategoriat.lisaa(testi1);
	 * Kategoria testi2 = new Kategoria(5); kategoriat.lisaa(testi2);
	 * Kategoria testi3 = new Kategoria(1); kategoriat.lisaa(testi3);
	 * Kategoria testi4 = new Kategoria(4); kategoriat.lisaa(testi4);
	 * Kategoria testi5 = new Kategoria(4); kategoriat.lisaa(testi5);
	 * Kategoria testi6 = new Kategoria(4); kategoriat.lisaa(testi6);
	 * 
	 * List<Kategoria> loydetyt = kategoriat.annaKategoriat(2);
	 * loydetyt.size() === 0;
	 * loydetyt = kategoriat.annaKategoriat(1);
	 * loydetyt.size() === 2;
	 * loydetyt.get(0) == testi1 === true;
	 * loydetyt.get(1) == testi3 === true;
	 * loydetyt = kategoriat.annaKategoriat(4);
	 * loydetyt.size() === 3;
	 * loydetyt.get(0) == testi4 === true;
	 * loydetyt.get(1) == testi5 === true;
	 * loydetyt.get(2) == testi6 === true;
	 * loydetyt = kategoriat.annaKategoriat(5);
	 * loydetyt.size() === 1;
	 * loydetyt.get(0) == testi2 === true;
	 * </pre>
	 */
	public List<Kategoria> annaKategoriat(int ostosnumero) {
		List<Kategoria> loydetyt = new ArrayList<Kategoria>();
		for (Kategoria kat : alkiot)
			if (kat.getOstoksenTunnusnumero() == ostosnumero)
				loydetyt.add(kat);
		return loydetyt;
	}
	

	/**
	 * Laitetaan muutos, jolloin pakotetaan tallentamaan.
	 */
	public void setMuutos() {
		muutettu = true;
	}
	

	/**
	 * Testiohjelma kategorioille
	 * 
	 * @param args
	 *            ei käytössä
	 */
	public static void main(String[] args) {
		Kategoriat kategoriat = new Kategoriat();
		Kategoria kategoria1 = new Kategoria();
		kategoria1.jeesiKategoria(2);
		Kategoria kategoria2 = new Kategoria();
		kategoria2.jeesiKategoria(1);
		Kategoria kategoria3 = new Kategoria();
		kategoria3.jeesiKategoria(2);
		Kategoria kategoria4 = new Kategoria();
		kategoria4.jeesiKategoria(2);

		kategoriat.lisaa(kategoria1);
		kategoriat.lisaa(kategoria2);
		kategoriat.lisaa(kategoria3);
		kategoriat.lisaa(kategoria4);

		System.out.println(
				"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Testataaaaan~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		List<Kategoria> kategoriatlist = kategoriat.annaKategoriat(2);

		for (Kategoria kat : kategoriatlist) {
			System.out.println(kat.getKattunnusnumero() + " ");
			kat.tulosta(System.out);
		}

	}

}
