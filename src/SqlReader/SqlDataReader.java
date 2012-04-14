/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlReader;

/**
 *
 * @author bear
 */
public interface SqlDataReader {
    public void openReader();
    public String readNextInstruc();
}
