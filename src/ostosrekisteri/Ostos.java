package ostosrekisteri;

import java.io.OutputStream;
import java.io.PrintStream;


import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka yksitt‰iselle ostokselle
 * 
 * @author Lauri, Joonas
 * @version 28.7.2016
 *
 */
public class Ostos implements Cloneable {
	private int ostosNumero;
	private String tuote = "";
	private double hinta = 0;
	private int kappalemaara = 0;
	private double yhteisHinta = 0;
	private int ostoPv = 0; // T‰h‰n voisi tehd‰ fiksumman jutun, mutta ei
							// tarvetta.
	private int ostoKk = 0; //
	private int ostoVv = 0;
	// private LocalDate ostosPaivamaara;
	private static int seuraavaNumero = 1;

	/**
	 * Setteri
	 * 
	 * @param k eli keissi
	 * @param jono
	 * @return null jos onnistui
	 */
	public String aseta(int k, String jono) {
		String tjono = jono.trim();
		StringBuffer sb = new StringBuffer(tjono);
		switch (k) {
		case 0:
			ostosNumero = Mjonot.erota(sb, 'ß', ostosNumero);
			return null;
		case 1:
			tuote = tjono;
			return null;
		case 2:
			hinta = Mjonot.erota(sb, 'ß', hinta);
			return null;
		case 3:
			kappalemaara = Mjonot.erota(sb, 'ß', kappalemaara);
			return null;
		default:
			return "ƒƒliˆ";
		}
	}

	/**
	 * Metodi tietyn kent‰n merkkijonon palauttamiseksi
	 * 
	 * @param k
	 *            monennen kent‰n merkkijono halutaan
	 * @return kent‰n merkkijono
	 */
	public String anna(int k) {
		switch (k) {
		case 0:
			return "" + ostosNumero;
		case 1:
			return "" + tuote;
		case 2:
			return "" + hinta;
		case 3:
			return "" + kappalemaara;
		case 4:
			return "" + yhteisHinta;
		case 5:
			// return "" + ostosPaivamaara;
		default:
			return "Ketuiks man!";
		}
	}
	

	/**
	 * Apumuodostaja testailuun jne
	 * 
	 */
	public void apuOstos() {
		ostosNumero = 0;
		tuote = "Olut";
		hinta = 1.20;
		kappalemaara = 4;
		yhteisHinta = getYhteisHinta();
		ostoPv = 15;
		ostoKk = 3;
		ostoVv = 2016;
	}
	

	/**
	 * Getteri tuotenimelle
	 * 
	 * @return tuote Tuotteen nimi
	 */
	public String getTuote() {
		return tuote;
	}
	

	/**
	 * Getteri hinnalle
	 * 
	 * @return hinta
	 */
	public double getHinta() {
		return hinta;
	}
	

	/**
	 * Metodi kokonaishinnan laskemiselle
	 * 
	 * @return yhteisHinta ostosten kappalem‰‰r‰n ja hinnan tulo.
	 */
	public double getYhteisHinta() {
		return (kappalemaara * hinta );
	}
	

	/**
	 * Getteri m‰‰r‰lle
	 * @return kappalemaara
	 */
	public int getMaara() {
		return kappalemaara;
	}
	

	/**
	 * Muuttaa ostoksen tiedot merkkijonoksi, joka voidaan sitten esimerkiksi
	 * kirjoittaa tiedostoon
	 * 
	 * @return ostoksen tiedot eroteltuna '|' -merkill‰.
	 * @example
	 * <pre name="test">
	 *     Ostos testiostos = new Ostos();
	 *     testiostos.parse("  4 |  Pirkka Olut |  1.0  | 12 | 12.00 | 3 | 8 | 2011");
	 *     testiostos.getOstosnumero() === 4;
	 *     testiostos.toString().startsWith("4|Pirkka Olut|1.0|12|") === true;
	 *
	 *     Ostos testiostos2 = new Ostos();
	 *     testiostos2.parse(" 19 | Porkgalia | 2.0 | 5 | 10.00 | 5 | 2 | 2009");
	 *     testiostos2.getOstosnumero() === 19;
	 *     testiostos2.toString().startsWith("19|Porkgalia|2.0|5|") === true;
	 * </pre>
	 */
	@Override
	public String toString() {
		return "" + getOstosnumero() + '|' + tuote + '|' + getHinta() + '|' + getMaara() + '|' + getYhteisHinta() + '|'
				+ ostoPv + '|' + ostoKk + '|' + ostoVv;

	}
	

	/**
	 * Parseaa tiedot rivilt‰
	 * 
	 * @param rivi kyseinen rivi mit‰ parsetaan
	 * @example
	 * <pre name="test">
	 *     String testirivi1 = "5|Kalja|1.00|1|1.00|7|7|2015";
	 *     String testirivi2 = "6|Porkkana|0.70|2|1.40|6|6|2006";
	 *     Ostos testiostos1 = new Ostos();
	 *     Ostos testiostos2 = new Ostos();
	 *     
	 *     testiostos1.parse(testirivi1);
	 *     testiostos1.getOstosnumero() === 5;
	 *     testiostos1.toString().startsWith("5|Kalja|1.00|1|1.00|7|7|2015");
	 *     
	 *     testiostos2.parse(testirivi2);
	 *     testiostos2.getOstosnumero() === 6;
	 *     testiostos2.toString().startsWith("6|Porkkana|0.70|2|1.40|6|6|2006");
     *</pre>
     */
	public void parse(String rivi) {
		StringBuffer sb = new StringBuffer(rivi);
		setOstosnumero(Mjonot.erota(sb, '|', getOstosnumero()));
		tuote = (Mjonot.erota(sb, '|', tuote));
		hinta = (Mjonot.erota(sb, '|', hinta));
		kappalemaara = (Mjonot.erota(sb, '|', kappalemaara));
		yhteisHinta = (Mjonot.erota(sb, '|', yhteisHinta));
		ostoPv = (Mjonot.erota(sb, '|', ostoPv));
		ostoKk = (Mjonot.erota(sb, '|', ostoKk));
		ostoVv = (Mjonot.erota(sb, '|', ostoVv));

	}
	

	/**
	 * Tulostaa ostoksen tiedot
	 * 
	 * @param out mihin tietovirtaan tulostetaan
	 */
	public void tulosta(PrintStream out) {
		out.println(String.format("Ostosnumero:" + "%04d", getOstosnumero()) + " Tuote: " + tuote); // kategoria
																									// muusta
																									// tiedostosta
		out.println(getMaara() + " kappaletta ");
		out.println("Kappalehinta: " + getHinta() + "e" + "   Yhteishinta:" + " " + getYhteisHinta() + "e");
		out.println("Ostop‰iv‰m‰‰r‰: " + ostoPv + "." + ostoKk + "." + ostoVv);
	}
	

	/**
	 * Tulostetaan ostoksen tiedot
	 * @param os tietovirta johon tulostetaan
	 */
	public void tulosta(OutputStream os) {
		tulosta(new PrintStream(os));
	}
	

	/**
	 * annetaan ostokselle ostosnumero ja lisataan ostosnumeroon 1
	 * 
	 * @return ostosnumero
	 */
	public int rekisteroi() {
		ostosNumero = seuraavaNumero;
		seuraavaNumero++;
		return ostosNumero;
	}
	

	/**
	 * Asettaa ostosnumeron ja tarkistaa, ett‰ luku on isompi kuin edellinen
	 * 
	 * @param nro asetettava numero
	 * 
	 */
	public void setOstosnumero(int nro) {
		ostosNumero = nro;
		if (nro >= seuraavaNumero)
			seuraavaNumero = ostosNumero + 1;
	}
	

	/**
	 * Getteri ostosnumerolle
	 * 
	 * @return ostosNumero
	 */
	public int getOstosnumero() {
		return ostosNumero;
	}
	

	/**
	 * Equals -metodi Ostokselle
	 */
	@Override
	public boolean equals(Object ostos) {
		if (ostos == null)
			return false;
		return this.toString().equals(ostos.toString());
	}
	

	@Override
	public int hashCode() {
		return ostosNumero;
	}
	

	/**
	 * P‰‰ohjelma testaukseen jne
	 * 
	 * @param args ei k‰ytˆss‰
	 */
	public static void main(String args[]) {
		Ostos kalja = new Ostos();
		Ostos porkkana = new Ostos();
		Ostos testi1 = new Ostos();
		kalja.rekisteroi();
		kalja.apuOstos();
		porkkana.rekisteroi();
		porkkana.apuOstos();
		testi1.rekisteroi();
		testi1.apuOstos();
		kalja.tulosta(System.out);
		porkkana.tulosta(System.out);
		testi1.tulosta(System.out);
		Ostos testi2 = new Ostos();
		testi2.parse("4  |  Kalia | 1.20 | 1 | 1.20 |");
		testi2.tulosta(System.out);
		System.out.println("-------------------------------------------------------------------------------------------------");
		 String testi2string = testi2.toString();
		 System.out.println(testi2string);

	}
	

	/**
	 * Getteri kuukauvelle
	 * 
	 * @return kuukausi
	 */
	public int getKK() {
		return ostoKk;
	}
	
	/**
	 * Palauttaa kenttien lukum‰‰r‰n
	 * @return kenttien lukum‰‰r‰
	 */
	public int getKenttia() {
		return 4;
	}
	
	/**
	 * Palauttaa ekan kent‰n indeksin(?)
	 * @return ekan kent‰n indeksi
	 */
	public int ekaKentta() {
		return 1;
	}
	

	/**
	 * Uuden ostoksen dialogin labeleita varten.
	 * 
	 * @param k "mones" atribuutti halutaan
	 * @return haluttu atribuutti
	 */
	public String getKysymys(int k) {
		switch (k) {
		case 0:
			return "ostosnumero";
		case 1:
			return "tuote";
		case 2:
			return "hinta";
		case 3:
			return "kappalem‰‰r‰";
		case 4:
			return "ostop‰iv‰m‰‰r‰";
		default:
			return "hmm";
		}
	}
	

	/**
	 * Asettaa p‰iv‰m‰‰r‰n ostokselle.
	 * 
	 * @param ostoPv2
	 * @param ostoKk2
	 * @param ostoVv2
	 */
	public void setDate(int ostoPv2, int ostoKk2, int ostoVv2) {
		ostoPv = ostoPv2;
		ostoKk = ostoKk2;
		ostoVv = ostoVv2;
	}
	

}
