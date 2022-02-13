// OOM-HARJOITUSKERTA 2 - TEKIJ훂: SAMU SALORANTA JA AURELIA ARPONEN


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

// testaa StudentGenerator-luokan toiminnallisuuden p채채kohdat
public class UserGeneratorTest {
    // apuobjekti testaukseen
    StudentGenerator generator = new StudentGenerator();

    // seuraavat luovat testitapauksia testeille

    // mik채 tahansa miesetunimist채
    @Provide
    Arbitrary<String> maleName() {
        return Arbitraries.of(generator.nameLists.mNames);
    }

    // mik채 tahansa naisetunimist채
    @Provide
    Arbitrary<String> femaleName() {
        return Arbitraries.of(generator.nameLists.fNames);
    }

    // mik채 tahansa sukunimist채
    @Provide
    Arbitrary<String> lastName() {
        return Arbitraries.of(generator.nameLists.lastNames);
    }

    // mik채 tahansa etunimist채
    @Provide
    Arbitrary<String> anyName() {
        ArrayList<String> names = new ArrayList<>();
        names.addAll(generator.nameLists.fNames);
        names.addAll(generator.nameLists.mNames);

        return Arbitraries.of(names);
    }

    // mik채 tahansa 1..3 nimen etunimiyhdistelm채, v채lily철nnein eroteltuna
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

    // tarkistaa ett채 miesnimi l철ytyy miesnimilistasta
    @Property
    boolean mNamesContainsmName() {
        return generator.nameLists.mNames.contains(generator.generatemName());
    }

    // tarkistaa ett채 naisnimi l철ytyy naisnimilistasta
    @Property
    boolean fNamesContainsfName() {
        return generator.nameLists.fNames.contains(generator.generatefName());
    }

    // tarkistaa ett채 sukunimi l철ytyy sukunimilistasta
    @Property
    boolean lastNamesContainslastName() {
        return generator.nameLists.lastNames.contains(generator.generateLastName());
    }

    // tarkistaa ett채 nimess채 on joko yksi osa tai v채liviiva ja kaksi osaa
    @Property
    boolean nameHasOneOrTwoPartsWithDash(@ForAll("anyName") String name) {
        return (generator.namePartCount(name) == 1 && !name.contains("-")) ||
                (generator.namePartCount(name) == 2 && name.contains("-"));
    }

    // tarkistaa ett채 etunimien osien m채채r채 on v채hint채채n vaadittu minimim채채r채
    @Property
    boolean firstNameCountAtLeast(@ForAll @IntRange(min = 1, max = 5) int minCount, @ForAll @IntRange(min = 0, max = 5) int plus) {
        List<String> firstNames = generator.generateFirstNames(minCount, minCount + plus);
        int partsSum = firstNames.stream().mapToInt(generator::namePartCount).sum();
        return partsSum >= minCount;
    }

    // tarkistaa ett채 etunimien osien m채채r채 on korkeintaan vaadittu maksimim채채r채
    @Property
    boolean firstNameCountAtMost(@ForAll @IntRange(min = 1, max = 10) int minCount, @ForAll @IntRange(min = 0, max = 10) int plus) {
        List<String> firstNames = generator.generateFirstNames(minCount, minCount + plus);
        int partsSum = firstNames.stream().mapToInt(generator::namePartCount).sum();
        return partsSum <= (minCount + plus);
    }

    // tarkistaa ett채 kaikkien yksi- ja kaksiosaisten etunimien osien yhteism채채r채 on 1..3
    @Property
    boolean firstNameCountBetween1and3() {
        String firstNames = generator.generateFirstNames();
        int partsSum = generator.namePartCount(firstNames);
        return partsSum >= 1 && partsSum <= 3;
    }

    // tarkistaa ett채 k채ytt채j채tunnus pienill채 ascii-kirjaimilla kirjoitettu
    @Property
    boolean userNameChars(@ForAll("firstNames") String firstNames, @ForAll("lastName") String lastName) {
        String username = generator.generateUserName(firstNames, lastName, n -> false);

        String processed = username.replaceAll("-", " ").replaceAll("[^\\x20-\\x7F]", "").toLowerCase();

        return username.equals(processed);
    }

    // tarkistaa ett채 k채ytt채j채tunnus on kelvollinen, eli:
    //  - 6 merkki채 pitk채 (ellei henkil철n nimiss채 ole v채hemm채n ascii-merkkej채
    //  - muuten kaikkien nimien kaikkien ascii-merkkien yhteism채채r채
    @Property
    boolean userNameLength(@ForAll("firstNames") String firstNames, @ForAll("lastName") String lastName) {
        String username = generator.generateUserName(firstNames, lastName, n -> false);

        Function<String, String> processName = name -> name.replaceAll("-", " ").replaceAll("[^\\x20-\\x7F]", "").toLowerCase();
        int length = processName.apply(firstNames + lastName).length();

        return username.length() == 6 || username.length() == length;
    }
    
    
    @Property
    boolean toUpperCase() {
    	String a = "Pertti隘梁領";
    	return a.toUpperCase().equals(generator.toUppercase(a));
    }

    // alkeellinen yksikk철testi. testataan yksitt채inen kaksiosainen nimi
    @Test
    void isDoubleName() {
        Assertions.assertTrue(generator.isDoubleName("Tytti-Annastiina"));
    }
}
