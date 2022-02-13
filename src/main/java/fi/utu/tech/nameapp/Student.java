// OOM-HARJOITUSKERTA 2 - TEKIJÄT: SAMU SALORANTA JA AURELIA ARPONEN


package fi.utu.tech.nameapp;

/**
 * A single student entity.
 */
public class Student {
    public final String firstNames;
    public final String familyName;
    public final int id;
    public final String idString;
    public final String username;

    public Student(Student s) {
        this.firstNames = s.firstNames;
        this.familyName = s.familyName;
        this.id = s.id;
        this.idString = s.idString;
        this.username = s.username;
    }

    public Student(String firstNames, String familyName, int id, String idString, String username) {
        this.firstNames = firstNames;
        this.familyName = familyName;
        this.id = id;
        this.idString = idString;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstNames='" + firstNames + '\'' +
                ", familyName='" + familyName + '\'' +
                ", id=" + id +
                ", idString='" + idString + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String wholeName() {
        return firstNames + " " + familyName;
    }

    public String wholeNameId() {
        return wholeName() + " (" + idString + " / " + username + ")";
    }
}