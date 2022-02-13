// OOM-HARJOITUSKERTA 2 - TEKIJ�T: SAMU SALORANTA JA AURELIA ARPONEN


package fi.utu.tech.nameapp;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// testaa StudentGenerator-luokan toiminnallisuuden pääkohdat
public class UserGeneratorTest {
    // apuobjekti testaukseen
    StudentGenerator generator = new StudentGenerator();

    // seuraavat luovat testitapauksia testeille

    // mikä tahansa miesetunimistä
    @Provide
    Arbitrary<String> maleName() {
        return Arbitraries.of(generator.nameLists.mNames);
    }

    // mikä tahansa naisetunimistä
    @Provide
    Arbitrary<String> femaleName() {
        return Arbitraries.of(generator.nameLists.fNames);
    }

    // mikä tahansa sukunimistä
    @Provide
    Arbitrary<String> lastName() {
        return Arbitraries.of(generator.nameLists.lastNames);
    }

    // mikä tahansa etunimistä
    @Provide
    Arbitrary<String> anyName() {
        ArrayList<String> names = new ArrayList<>();
        names.addAll(generator.nameLists.fNames);
        names.addAll(generator.nameLists.mNames);

        return Arbitraries.of(names);
    }

    // mikä tahansa 1..3 nimen etunimiyhdistelmä, välilyönnein eroteltuna
    @Provide
    Arbitrary<String> firstNames() {
        return Arbitraries.create(() -> generator.generateFirstNames());
    }


    @BeforeAll
    public static void init() {
        System.out.println("Ok, starting all tests!");
    }

    @AfterAll
    public static void done() {
        System.out.println("Tests done!");
    }

    // tarkistaa että miesnimi löytyy miesnimilistasta
    @Property
    boolean mNamesContainsmName() {
        return generator.nameLists.mNames.contains(generator.generatemName());
    }

    // tarkistaa että naisnimi löytyy naisnimilistasta
    @Property
    boolean fNamesContainsfName() {
        return generator.nameLists.fNames.contains(generator.generatefName());
    }

    // tarkistaa että sukunimi löytyy sukunimilistasta
    @Property
    boolean lastNamesContainslastName() {
        return generator.nameLists.lastNames.contains(generator.generateLastName());
    }

    // tarkistaa että nimessä on joko yksi osa tai väliviiva ja kaksi osaa
    @Property
    boolean nameHasOneOrTwoPartsWithDash(@ForAll("anyName") String name) {
        return (generator.namePartCount(name) == 1 && !name.contains("-")) ||
                (generator.namePartCount(name) == 2 && name.contains("-"));
    }

    // tarkistaa että etunimien osien määrä on vähintään vaadittu minimimäärä
    @Property
    boolean firstNameCountAtLeast(@ForAll @IntRange(min = 1, max = 5) int minCount, @ForAll @IntRange(min = 0, max = 5) int plus) {
        List<String> firstNames = generator.generateFirstNames(minCount, minCount + plus);
        int partsSum = firstNames.stream().mapToInt(generator::namePartCount).sum();
        return partsSum >= minCount;
    }

    // tarkistaa että etunimien osien määrä on korkeintaan vaadittu maksimimäärä
    @Property
    boolean firstNameCountAtMost(@ForAll @IntRange(min = 1, max = 10) int minCount, @ForAll @IntRange(min = 0, max = 10) int plus) {
        List<String> firstNames = generator.generateFirstNames(minCount, minCount + plus);
        int partsSum = firstNames.stream().mapToInt(generator::namePartCount).sum();
        return partsSum <= (minCount + plus);
    }

    // tarkistaa että kaikkien yksi- ja kaksiosaisten etunimien osien yhteismäärä on 1..3
    @Property
    boolean firstNameCountBetween1and3() {
        String firstNames = generator.generateFirstNames();
        int partsSum = generator.namePartCount(firstNames);
        return partsSum >= 1 && partsSum <= 3;
    }

    // tarkistaa että käyttäjätunnus pienillä ascii-kirjaimilla kirjoitettu
    @Property
    boolean userNameChars(@ForAll("firstNames") String firstNames, @ForAll("lastName") String lastName) {
        String username = generator.generateUserName(firstNames, lastName, n -> false);

        String processed = username.replaceAll("-", " ").replaceAll("[^\\x20-\\x7F]", "").toLowerCase();

        return username.equals(processed);
    }

    // tarkistaa että käyttäjätunnus on kelvollinen, eli:
    //  - 6 merkkiä pitkä (ellei henkilön nimissä ole vähemmän ascii-merkkejä
    //  - muuten kaikkien nimien kaikkien ascii-merkkien yhteismäärä
    @Property
    boolean userNameLength(@ForAll("firstNames") String firstNames, @ForAll("lastName") String lastName) {
        String username = generator.generateUserName(firstNames, lastName, n -> false);

        Function<String, String> processName = name -> name.replaceAll("-", " ").replaceAll("[^\\x20-\\x7F]", "").toLowerCase();
        int length = processName.apply(firstNames + lastName).length();

        return username.length() == 6 || username.length() == length;
    }
    
    
    @Property
    boolean toUpperCase() {
    	String a = "Pertti������";
    	return a.toUpperCase().equals(generator.toUppercase(a));
    }

    // alkeellinen yksikkötesti. testataan yksittäinen kaksiosainen nimi
    @Test
    void isDoubleName() {
        Assertions.assertTrue(generator.isDoubleName("Tytti-Annastiina"));
    }
}
