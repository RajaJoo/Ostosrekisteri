package ostosrekisteri;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Luokka ostoksille, lisays, muokkaus jne
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *
 */
public class Ostokset implements Iterable<Ostos> {

	private static final int MAX_OSTOKSIA = 500; // Tätä pitää rukata
	private int lkm = 0;
	private String tiedostonimi = "";
	private Ostos ostokset[] = new Ostos[MAX_OSTOKSIA];
	private boolean muutettu = false;
	private String tiedostonPerusNimi = "ostokset";

	/**
	 * Muodostaja
	 */
	public Ostokset() {
		//kjeh
	}
	

	/**
	 * Lisää uuden ostoksen ostoksiin
	 * 
	 * @param ostos ostos joka lisätään
	 * @throws SailoException jos menee yli taulukon koon
	 */
	public void lisaa(Ostos ostos) throws SailoException {
		if (lkm > ostokset.length)
			throw new SailoException("Liikaa tavaraa taulukossa!");
		ostokset[lkm] = ostos;
		lkm++;
		muutettu = true;
	}
	

	/**
	 * Korvaa ostoksen tietorakenteessa.
	 * 
	 * @param ostos
	 * @throws SailoException
	 */
	public void korvaaTaiLisaa(Ostos ostos) throws SailoException {
		int id = ostos.getOstosnumero();
		for (int i = 0; i < lkm; i++) {
			if (ostokset[i].getOstosnumero() == id) {
				ostokset[i] = ostos;
				muutettu = true;
				return;
			}
		}
		lisaa(ostos);
	}
	

	/**
	 * get -metodi Ostoksille
	 * 
	 * @return lkm alkioiden lukumäärä Ostokset taulukossa
	 */
	public int getMaara() {
		return lkm;
	}

	/**
	 * Palauttaa viitteen Ostokseen ostokset -taulukossa
	 * 
	 * @param i
	 * @return viite jäseneen jonka indeksi on i
	 * @throws IndexOutOfBoundsException jos viallinen indeksi
	 */
	public Ostos anna(int i) throws IndexOutOfBoundsException {
		if (i < 0 || lkm <= 0)
			throw new IndexOutOfBoundsException("Viallinen indeksi: " + i);
		return ostokset[i];
	}

	/**
	 * Lukee ostokset tiedostosta.
	 * 
	 * @param tied tiedoston nimi
	 * @throws SailoException jos tiedostosta lukeminen epäonnistuu
	 * @example
	 *<pre name="test">
	 *     #THROWS SailoException
	 *     #import java.io.File;
	 *     #import java.util.*;
	 *     
	 *     Ostokset ostokset = new Ostokset();
	 *     Ostos ostos1 = new Ostos(), ostos2 = new Ostos(), ostos3 = new Ostos();
	 *     ostos1.apuOstos();
	 *     ostos2.apuOstos();
	 *     ostos3.apuOstos();
	 *     String directory = "testi";
	 *     String tiedNimi = directory+"/ostokset";
	 *     File ftied = new File(tiedNimi +".dat");
	 *     File dir = new File(directory);
	 *     dir.mkdir();
	 *     ftied.delete();
	 *     ostokset.lueTiedostosta(tiedNimi); #THROWS SailoException
	 *     ostokset.lisaa(ostos1);
	 *     ostokset.lisaa(ostos2);
	 *     ostokset.lisaa(ostos3);
	 *     ostokset.tallenna();
	 *     
	 *     Ostokset ostokset2 = new Ostokset();
	 *     ostokset2.lueTiedostosta(tiedNimi);
	 *     Iterator<Ostos> i = ostokset2.iterator();
	 *     i.next() === ostos1;
	 *     i.next() === ostos2;
	 *     i.next() === ostos3;
	 *     i.hasNext() === false;
	 *     
	 *     ostokset2.lisaa(ostos1);
	 *     ostokset2.lisaa(ostos2);
	 *     ostokset2.tallenna();
	 *     ftied.delete() === true;
	 *     File bakup = new File(tiedNimi +".bak");
	 *     bakup.delete() === true;
	 *     dir.delete() === true;
	 *     
	 *     
	 *     
	 *     
	 *</pre>
	 */
	public void lueTiedostosta(String tied) throws SailoException {
		setTiedostonPerusNimi(tied);
		try (BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi()))) {
			String rivi = fi.readLine();
			if (rivi == null)
				throw new SailoException("Jotain pahaa tapahtui.");

			while ((rivi = fi.readLine()) != null) {
				rivi = rivi.trim();
				if ("".equals(rivi) || rivi.charAt(0) == ';')
					continue;
				Ostos ostos = new Ostos();
				ostos.parse(rivi);
				lisaa(ostos);
			}
			muutettu = false;
		} catch (FileNotFoundException e) {
			throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea.");
		} catch (IOException e) {
			throw new SailoException("Ongelma tiedoston käsittelyssä: " + e.getMessage());
		}
	}
	

	/**
	 * Luetaan aikasemmin nimetystä tiedostosta
	 * 
	 * @throws SailoException jos lukeminen menee pieleen
	 */
	public void lueTiedostosta() throws SailoException {
		lueTiedostosta(getTiedostonPerusNimi());
	}
	

	/**
	 * Tallentaa Ostokset -listan tiedot tiedostoon
	 * 
	 * Tiedostoon tallennetaan muotoon:
	 * 2|Olut|1.10|4|4.40|3|12|2014
	 * @throws SailoException jos ei mahdu tjsp
	 */
	public void tallenna() throws SailoException {

		if (!muutettu)
			return;

		File fbak = new File(getBakNimi());
		File ftied = new File(getTiedostonNimi());
		fbak.delete();
		ftied.renameTo(fbak);

		try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))) {
			fo.println(ostokset.length);
			for (Ostos ostos : this) {
				fo.println(ostos.toString());
			}
		} catch (FileNotFoundException ex) {
			throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea.");
		} catch (IOException ex) {
			throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa häikkää.");
		}
		muutettu = false;
	}
	

	/**
	 * Palauttaa tiedoston perusnimen
	 * 
	 * @return tiedostonPerusNimi
	 */
	public String getTiedostonPerusNimi() {
		return tiedostonPerusNimi;
	}
	

	/**
	 * Palauttaa tallennukseen käytettävän tiedoston nimen
	 * 
	 * @return tiedoston nimi
	 */
	public String getTiedostonNimi() {
		return getTiedostonPerusNimi() + ".dat";
	}
	

	/**
	 * Palauttaa back up -tiedoston nimen
	 * 
	 * @return backupin nimi
	 * 
	 */
	public String getBakNimi() {
		return tiedostonPerusNimi + ".bak";
	}
	

	/**
	 * Asettaa tiedostolle perusnimen
	 * 
	 * @param nimi tiedoston nimi
	 */
	public void setTiedostonPerusNimi(String nimi) {
		tiedostonPerusNimi = nimi;
	}
	

	/**
	 * Iterointiluokka ostoksille
	 * 
	 */
	public class OstoksetIterator implements Iterator<Ostos> {
		private int kohdalla = 0;

		/**
		 * tarkistetaan onko seuraavaa ostosta
		 * 
		 * @see java.util.Iterator#hasNext()
		 * @return true jos on seuraava ostos
		 */
		@Override
		public boolean hasNext() {
			return (kohdalla < getMaara());
		}
		

		/**
		 * Palautetaan seuraava ostos
		 * 
		 * @return seuraava ostos
		 * @throws NoSuchElementException
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Ostos next() throws NoSuchElementException {
			if (!hasNext())
				throw new NoSuchElementException("Ostosta ei ole.");
			return anna(kohdalla++);
		}
		

		/**
		 * Remove -metodi iteraattorille
		 * 
		 * @throws UnsupportedOperationException aina, sillä ei toteutettu
		 * @see java.util.Iterator#next()
		 */
		@Override
		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException("Ei voida poistaa.");
		}
	}
	

	/**
	 * Poistaa ostoksen ostoksista
	 * 
	 * @param ostosnumero joka poistetaan
	 * @return 1 jos onnistui, 0 jos ei
	 */
	public int poista(int ostosnumero) {
		int ind = etsiNro(ostosnumero);
		if (ind < 0)
			return 0;
		lkm--;
		for (int i = ind; i < lkm; i++)
			ostokset[i] = ostokset[i + 1];
		ostokset[lkm] = null;
		muutettu = true; 
		return 1;
	}
	

	/**
	 * Etsii poistettavan ostoksen ostosnumeron
	 * 
	 * @param ostosnumero
	 * @return ostoksen id 
	 */
	public int etsiNro(int ostosnumero) {
		for (int i = 0; i < lkm; i++)
			if (ostosnumero == ostokset[i].getOstosnumero())
				return i;
		return -1;
	}
	

	/**
	 * palautetaan iteraattori
	 * 
	 * @return OstoksetIterator
	 */
	@Override
	public Iterator<Ostos> iterator() {
		return new OstoksetIterator();
	}
	

	/**
	 * metodi tietorakenteesta hakemiselle hakuehtojen kera
	 * 
	 * @param hakuehto ehto jolla haetaan
	 * @param k kentän indeksi
	 * @return tietorakenne ostoksista
	 */
	@SuppressWarnings("unused")
	public Collection<Ostos> etsi(String hakuehto, int k) {
		Collection<Ostos> loydetyt = new ArrayList<Ostos>();
		for (Ostos ostos : this) {
			loydetyt.add(ostos);
		}

		return loydetyt;
	}
	

	/**
	 * Pääohjelma testaukseen
	 * 
	 * @param args ei käytössä
	 */
	public static void main(String args[]) {
		Ostokset ostokset = new Ostokset();

		Ostos kalja = new Ostos();
		Ostos porkkana = new Ostos();
		kalja.apuOstos();
		kalja.rekisteroi();
		porkkana.apuOstos();
		porkkana.rekisteroi();

		try {
			ostokset.lisaa(kalja);
			ostokset.lisaa(porkkana);

			System.out.println("============================TESTAILLAAS NONINNO=====================================");

			for (int i = 0; i < ostokset.getMaara(); i++) {
				Ostos ostos = ostokset.anna(i);
				System.out.println("Ostos nro:" + i);
				ostos.tulosta(System.out);
			}

		} catch (SailoException ex) {
			System.out.println(ex.getMessage());
		}
	}

}
