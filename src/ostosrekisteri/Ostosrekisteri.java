package ostosrekisteri;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Ostosrekisteri -luokka
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *
 */
public class Ostosrekisteri {

	private Ostokset ostokset = new Ostokset();
	private Kategoriat kategoriat = new Kategoriat();

	/**
	 * Palauttaa yksitt‰isten ostosten m‰‰r‰n
	 * 
	 * @return ostosten m‰‰r‰
	 */
	public int getOstokset() {
		return ostokset.getMaara();
	}
	

	/**
	 * Lis‰‰ ostoksen ostoksiin
	 * 
	 * @param ostos Lis‰tt‰v‰ ostos
	 * @throws SailoException jos ei voida lis‰t‰
	 * @example
	 * <pre name="test">
	 * #THROWS SailoException
	 * Ostosrekisteri ostosrekisteri = new Ostosrekisteri();
	 * Ostos ostos1 = new Ostos(); Ostos ostos2 = new Ostos();
	 * ostos1.rekisteroi(); ostos2.rekisteroi();
	 * ostosrekisteri.getOstokset() === 0;
	 * ostosrekisteri.lisaa(ostos1);
	 * ostosrekisteri.getOstokset() === 1;
	 * ostosrekisteri.lisaa(ostos2);
	 * ostosrekisteri.getOstokset() === 2;
	 * </pre>
	 */
	public void lisaa(Ostos ostos) throws SailoException {
		ostokset.lisaa(ostos);
	}
	

	/**
	 * List‰‰n uusi kategoria ostosrekisteriin
	 * 
	 * @param kat lis‰tt‰v‰ kategoria
	 * @throws SailoException jos tulee ongelmia
	 */
	public void lisaa(Kategoria kat) throws SailoException {
		kategoriat.korvaaTaiLisaa(kat);
	}
	

	/**
	 * Palauttaa hakuehtoihin m‰ts‰‰vien ostosten viitteet
	 * 
	 * @param hakuehto
	 * @param k kent‰n indeksi
	 * @return tietorakenne lˆytyneist‰ ostoksista
	 * @throws SailoException jos ei onnistu
	 */
	public Collection<Ostos> etsi(String hakuehto, int k) throws SailoException {
		return ostokset.etsi(hakuehto, k);
	}
	

	/**
	 * Palauttaa i:nnen ostoksen
	 * 
	 * @param i
	 * @return ostos joka on indeksiss‰ i
	 */
	public Ostos getOstos(int i) {
		return ostokset.anna(i);
	}
	

	/**
	 * Haetaan kaikki ostoksen kategoriat
	 * 
	 * @param ostos
	 * @return ostoksen kategoriat
	 */
	public List<Kategoria> annaKategoriat(Ostos ostos) {
		return kategoriat.annaKategoriat(ostos.getOstosnumero());
	}
	

	/**
	 * Laitetaan kategoriat muuttuneeksi niin pakottaa tallennuksen
	 */
	public void setKategoriaMuutos() {
		kategoriat.setMuutos();
	}
	

	/**
	 * Tallentaa kaikki ostosrekisterin tiedot tiedostoon Jos ostosten
	 * tallentaminen ep‰onnistuu yritet‰‰n silti tallentaa kategoriat enne
	 * poikkeuksen heitt‰mist‰.
	 * 
	 * @throws SailoException jos tallentamisessa h‰ikk‰‰
	 */
	public void tallenna() throws SailoException {
		String virhe = "";
		try {
			ostokset.tallenna();
		} catch (SailoException ex) {
			virhe = ex.getMessage();
		}
		try {
			kategoriat.tallenna();
		} catch (SailoException ex) {
			virhe += ex.getMessage();
		}
		if (!"".equals(virhe))
			throw new SailoException(virhe);
	}
	

	/**
	 * Lukee ostoksien tiedot tiedostosta
	 * 
	 * @param nimi tiedoston nimi josta luetaan
	 * @throws SailoException
	 */
	public void lueTiedostosta(String nimi) throws SailoException {
		ostokset = new Ostokset();
		kategoriat = new Kategoriat();

		setTiedosto(nimi);
		ostokset.lueTiedostosta();
		kategoriat.lueTiedostosta();

	}
	

	/**
	 * Asettaa tiedostojen nimet
	 * 
	 * @param nimi tiedoston haluttu nimi
	 */
	public void setTiedosto(String nimi) {
		File dir = new File(nimi);
		dir.mkdirs();
		String hakemistonNimi = "";
		if (!nimi.isEmpty())
			hakemistonNimi = nimi + "/";
		ostokset.setTiedostonPerusNimi(hakemistonNimi + "ostokset");
		kategoriat.setTiedostonPerusNimi(hakemistonNimi + "kategoriat");
	}
	

	/**
	 * Poistaa ostoksen listasta
	 * 
	 * @param ostosnumero poistetettavan ostosnumero
	 * @return montako ostosta poistettiin
	 */
	public int poista(int ostosnumero) {
		int ret = ostokset.poista(ostosnumero);
		return ret;
	}
	

	/**
	 * P‰‰ohjelma ostosrekisterin testaukseen
	 * 
	 * @param args ei k‰ytˆss‰
	 */
	@SuppressWarnings("unused")
	public static void Main(String args[]) {
		Ostosrekisteri ostosrekisteri = new Ostosrekisteri();

	}

}
