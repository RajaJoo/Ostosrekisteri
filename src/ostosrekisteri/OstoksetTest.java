package ostosrekisteri;
// Generated by ComTest BEGIN
import static org.junit.Assert.*;
import org.junit.*;
import java.io.File;
import java.util.*;
import ostosrekisteri.*;
// Generated by ComTest END

/**
 * Test class made by ComTest
 * @version 2016.08.03 13:51:02 // Generated by ComTest
 *
 */
public class OstoksetTest {



  // Generated by ComTest BEGIN
  /** 
   * testLueTiedostosta99 
   * @throws SailoException when error
   */
  @Test
  public void testLueTiedostosta99() throws SailoException {    // Ostokset: 99
    Ostokset ostokset = new Ostokset(); 
    Ostos ostos1 = new Ostos(), ostos2 = new Ostos(), ostos3 = new Ostos(); 
    ostos1.apuOstos(); 
    ostos2.apuOstos(); 
    ostos3.apuOstos(); 
    String directory = "testi"; 
    String tiedNimi = directory+"/ostokset"; 
    File ftied = new File(tiedNimi +".dat"); 
    File dir = new File(directory); 
    dir.mkdir(); 
    ftied.delete(); 
    try {
    ostokset.lueTiedostosta(tiedNimi); 
    fail("Ostokset: 115 Did not throw SailoException");
    } catch(SailoException _e_){ _e_.getMessage(); }
    ostokset.lisaa(ostos1); 
    ostokset.lisaa(ostos2); 
    ostokset.lisaa(ostos3); 
    ostokset.tallenna(); 
    Ostokset ostokset2 = new Ostokset(); 
    ostokset2.lueTiedostosta(tiedNimi); 
    Iterator<Ostos> i = ostokset2.iterator(); 
    assertEquals("From: Ostokset line: 124", ostos1, i.next()); 
    assertEquals("From: Ostokset line: 125", ostos2, i.next()); 
    assertEquals("From: Ostokset line: 126", ostos3, i.next()); 
    assertEquals("From: Ostokset line: 127", false, i.hasNext()); 
    ostokset2.lisaa(ostos1); 
    ostokset2.lisaa(ostos2); 
    ostokset2.tallenna(); 
    assertEquals("From: Ostokset line: 132", true, ftied.delete()); 
    File bakup = new File(tiedNimi +".bak"); 
    assertEquals("From: Ostokset line: 134", true, bakup.delete()); 
    assertEquals("From: Ostokset line: 135", true, dir.delete()); 
  } // Generated by ComTest END
}