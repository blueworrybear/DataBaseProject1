/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject;

/**
 *
 * @author bear
 */
public interface SqlDataReader {
    public void openReader();
    public String readNextInstruc();
}
