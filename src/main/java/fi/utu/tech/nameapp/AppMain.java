// OOM-HARJOITUSKERTA 2 - TEKIJÄT: SAMU SALORANTA JA AURELIA ARPONEN

package fi.utu.tech.nameapp;

public class AppMain {
    public static void main(String[] args) {
        StudentGenerator generator = new StudentGenerator();
        
        Student student = generator.generateStudent();

        System.out.println(student);
    }
}
