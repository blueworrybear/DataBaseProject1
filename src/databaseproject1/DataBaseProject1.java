/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject1;

/**
 *
 * @author bear
 */
public class DataBaseProject1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Start");
        SqlDataReader reader = new SqlDataReader("src/Resource/data.in");
        reader.openReader();
        System.out.println(reader.readNextInstruc());
        System.out.println(reader.readNextInstruc());
    }
}
