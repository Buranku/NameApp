// OOM-HARJOITUSKERTA 2 - TEKIJƒT: SAMU SALORANTA JA AURELIA ARPONEN


package fi.utu.tech.nameapp;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentGenerator {
    public static final CSVNames nameLists = new CSVNames();

    private final Set<String> generatedUsernames = new HashSet<>();

    private int studentIdCursor = 1000;
    /**
     * Palauttaa satunnaisen nimen miesten nimien tietokannasta.
     * K‰ytt‰‰ metodia generateName.
     */
    public String generatemName() {
        return generateName(nameLists.mNames);
    }
/**
 * Palauttaa satunnaisen nimen naisten nimien tietokannasta.
 * K‰ytt‰‰ metodia generateName.
 */
    public String generatefName() {
        return generateName(nameLists.fNames);
    }
    /**
     * Palauttaa satunnaisen nimen sukunimien tietokannasta.
     * K‰ytt‰‰ metodia generateName.
     */
    public String generateLastName() {
        return generateName(nameLists.lastNames);
    }
    /**
     * Luo uuden Student-olion.
     * K‰ytt‰‰ metodeja generateFirstNames, generateLastName ja generateUserName.
     * 
     */
    public Student generateStudent() {
        String fNames, sName, userName;

        // generateUserName returns null if the username is already reserved
        do {
            fNames = generateFirstNames();
            sName = generateLastName();
            fNames = toUppercase(fNames);
            sName = toUppercase(sName);
            userName = generateUserName(fNames, sName, generatedUsernames::contains);
        } while (userName == null);

        generatedUsernames.add(userName);
        studentIdCursor += new Random().nextInt(10) + 1;

        return new Student(fNames, sName, studentIdCursor,
                "utu:" + studentIdCursor, userName
        );
    }
    
    /**
     * Muuttaa merkkijonon kaikki kirjaimet isoiksi.
     * 
     * @.pre nimi != null
     * @.post RESULT == FORALL(i: nimi;
     * 			i <= 'A' && i <= 'Z' || i == 'ƒ' || i == '≈' || i == '÷')
     */
    public String toUppercase(String nimi) {
    	String mjono = "";
    	
    	for(int i = 0 ; i < nimi.length();i++) {
    		if(97 <= (int) nimi.charAt(i) && (int) nimi.charAt(i) <= 122 ) {
    			int a = (int) nimi.charAt(i) - 32;
    			mjono = mjono + (char) a;
    		}
    		else if( nimi.charAt(i) == 'Â') {
    			mjono = mjono + '≈';
    		}
    		else if( nimi.charAt(i) == '‰') {
    			mjono = mjono + 'ƒ';
    		}
    		else if( nimi.charAt(i) == 'ˆ') {
    			mjono = mjono + '÷';
    		}
    		else if((65<= (int) nimi.charAt(i) && (int) nimi.charAt(i) <= 90) || (45 == (int) nimi.charAt(i) ||
    				nimi.charAt(i) == 'ƒ' || nimi.charAt(i) == '÷' || nimi.charAt(i) == '≈')) {
    			mjono = mjono + nimi.charAt(i);
    		}
    		
    		
    	}
    	return mjono;
    }
    
    // ---

    /**
     * Palauttaa satunnaisen nimen tietokannasta. 
     * 
     *@.pre nameList != null &&
     *		FORALL(i : 0 < i < size.nameList;
     *		nameList[i] != null
     *)
     * @.post true
     */
    public String generateName(List<String> nameList) {
        return nameList.get(new Random().nextInt(nameList.size())).trim();
    }

    /**
     * Tarkistaa onko nimess‰ kaksi osaa.
     * K‰ytt‰‰ metodia namePartCount.
     * 
     * 
     */
    public boolean isDoubleName(String name) {
        return namePartCount(name) == 2;
    }

    /**
     * Laskee montako osaa nimess‰ on ja palauttaa t‰m‰n kokonaisluvun.
     * @.pre name != null
     * 
     * @.post RESULT >= 0
     * 
     */
    public int namePartCount(String name) {
        return name.equals("") ? 0 : name.replaceAll(" +", " ").replaceAll("-", " ").split(" ").length;
    }

    /**
     * Palauttaa listan merkkijono-tyyppisi‰ nimi‰.
     * 
     * 
     * @.pre minNumber >= 0 &&
     * 		maxNumber > minNumber
     * @.post RESULT != null
     */
    public List<String> generateFirstNames(int minNumber, int maxNumber) {
        List<String> names = new ArrayList<>();
        int nameCount = 0;
        Supplier<String> nameGenerator = this::generatefName;

        if (new Random().nextBoolean()) nameGenerator = this::generatemName;

        // 50% -> max
        // 50% -> min .. max
        int targetLength = new Random().nextBoolean() ? maxNumber : new Random().nextInt(maxNumber - minNumber + 1) + minNumber;

        while (nameCount < targetLength) {
            String nextName = nameGenerator.get();
            int nextCount = namePartCount(nextName);
            if (nameCount + nextCount <= targetLength) {
                names.add(nextName);
                nameCount += nextCount;
            }
        }

        return names;
    }

    /**
     * 
     * Palauttaa merkkijonona k‰ytt‰j‰tunnuksen.
     * 
     * @.pre firstNames != null &&
     * 		lastName != null
     * 
     * @.post RESULT != null
     * 
     */
    public String generateUserName(String firstNames, String lastName, Predicate<String> filter) {
        Function<String, String> processName = name -> name.replaceAll("-", " ").replaceAll("[^\\x20-\\x7F]", "").toLowerCase();

        String[] firstNamesASCII = processName.apply(firstNames).split(" ");
        String lastNameASCII = processName.apply(lastName);

        List<String> prefixes = new ArrayList<>();
        prefixes.add("");

        for (String s : firstNamesASCII) {
            List<String> newNames = new ArrayList<>();
            for (String prefix : prefixes) {
                for (int l : new int[]{2, 3, 1, 4, 0})
                    newNames.add(prefix + s.substring(0, Math.min(l, s.length())));
            }
            prefixes = newNames;
        }
        List<String> userNames = prefixes.stream().map(p -> {
            String result = p + lastNameASCII;
            return result.substring(0, Math.min(result.length(), 6));
        }).filter(filter.negate()).collect(Collectors.toList());

        return Stream.concat(
                userNames.stream().filter(n -> n.length()==6),
                userNames.stream().filter(n -> n.length()<6).sorted((m, n) -> n.length() - m.length())
        ).findFirst().orElse(null);
    }
    
    /**
     * Sama kuin toinen generateFirstNames metodi, mutta ilman parametrej‰.
     * 
     */
    public String generateFirstNames() {
        return generateFirstNames(1, 3).stream().collect(Collectors.joining(" "));
    }
}