package ostosrekisteri;

import java.io.OutputStream;
import java.io.PrintStream;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka kategorialle
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 */
public class Kategoria implements Cloneable {
	private int katTunnusnumero;
	private String kategoriaNimi;
	private int ostoksenTunnusnumero;

	/**
	 * "apumuuttuja" seuraavan id:n määrittämiseksi
	 */
	public static int seuraavaNro = 1;
	

	/**
	 * Alustetaan kategoria
	 */
	public Kategoria() {
		//
	}
	

	/**
	 * Alustetaan tietyn ostoksen kategoriat
	 * 
	 * @param ostoksenTunnusnro tietyn ostoksen tunnusnumero, id
	 */
	public Kategoria(int ostoksenTunnusnro) {
		this.ostoksenTunnusnumero = ostoksenTunnusnro;
	}
	

	/**
	 * Asetetaan nimi kategorialle
	 * 
	 * @param nimi
	 */
	public void setNimi(String nimi) {
		this.kategoriaNimi = nimi;
	}

	
	/**
	 * Metodi tietojen täyttämiseksi kategorialle
	 * 
	 * @param ostosNro ostoksen tunnusnumero, johon kategoria kuuluu
	 */
	public void jeesiKategoria(int ostosNro) {
		kategoriaNimi = "siivoustarvike";
		ostoksenTunnusnumero = ostosNro;
	}
	

	/**
	 * Metodi tulostamiselle
	 * 
	 * @param out tietovirta johon tiedot tulostetaan
	 */
	public void tulosta(PrintStream out) {
		out.println(kategoriaNimi);
	}
	

	/**
	 * Metodi tulostamiselle
	 * 
	 * @param os tietovirta johon tiedot tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}

	
	/**
	 * Antaa kategorialle sen tunnusnumeron ja lisää muuttujaan seuraavaNro
	 * yhden (1)
	 * 
	 * @return katTunnusnumero kategorian tunnusnumero
	 */
	public int rekisteroi() {
		katTunnusnumero = seuraavaNro;
		seuraavaNro++;
		return katTunnusnumero;
	}

	
	/**
	 * Getteri kategorian tunnusnumerolle
	 * 
	 * @return katTunnusnumero kategorian tunnusnumero
	 */
	public int getKattunnusnumero() {
		return katTunnusnumero;
	}
	

	/**
	 * Setteri kategorian tunnusnumerolle
	 * 
	 * @param nro numero joka asetetaan
	 */
	public void setKattunnusnumero(int nro) {
		katTunnusnumero = nro;
		if (katTunnusnumero >= seuraavaNro)
			seuraavaNro = katTunnusnumero + 1;
	}
	

	/**
	 * palautetaan mille ostokselle kategoria kuuluu
	 * 
	 * @return ostoksenTunnusnumero ostosNumero Ostos -luokassa
	 */
	public int getOstoksenTunnusnumero() {
		return ostoksenTunnusnumero;
	}
	

	/**
	 * Metodi kategorian tietojen muuttamiseksi merkkijonoksi
	 * 
	 * @return kategorian tiedot Stringinä
	 */
	@Override
	public String toString() {
		return "" + getKattunnusnumero() + "|" + getOstoksenTunnusnumero() + "|" + kategoriaNimi;
	}
	

	/**
	 * Parse -metodi kategorialle, ottaa tiedot ylös riviltä
	 * 
	 * @param rivi rivi jolta tiedot otetaan
	 */
	public void parse(String rivi) {
		StringBuffer sb = new StringBuffer(rivi);
		setKattunnusnumero(Mjonot.erota(sb, '|', getKattunnusnumero()));
		ostoksenTunnusnumero = (Mjonot.erota(sb, '|', getOstoksenTunnusnumero()));
		kategoriaNimi = (Mjonot.erota(sb, '|', kategoriaNimi));
	}
	

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return this.toString().equals(obj.toString());
	}
	

	@Override
	public int hashCode() {
		return katTunnusnumero;
	}

	
	/**
	 * Pääohjelma testailulle jne
	 * 
	 * @param args ei käytössä
	 */
	public static void main(String[] args) {
		Kategoria kategoria = new Kategoria();
		kategoria.rekisteroi();
		kategoria.jeesiKategoria(1);
		kategoria.tulosta(System.out);
	}

}
