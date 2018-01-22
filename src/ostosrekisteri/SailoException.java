package ostosrekisteri;

/**
 * Luokka poikkeuksille
 * 
 * @author Lauri,Joonas
 * @version 28.7.2016
 *
 */
public class SailoException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Poikkeuksen muodostaja, parametrina viesti
	 * 
	 * @param viesti
	 *            poikkeusviesti
	 */
	public SailoException(String viesti) {
		super(viesti);
	}
}
