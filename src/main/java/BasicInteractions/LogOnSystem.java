package BasicInteractions;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * <h1>Class responsible for logging in, as well as user creation.</h1>
 * Reference for how to implement PBKDF2 in java learnt from:
 * https://www.baeldung.com/java-password-hashing
 *
 * @author Lorenzo Menegotto
 * @version 1.0
 * @since 22-01-2019
 */

public class LogOnSystem {
    private DatabaseConnectionURL database = null;

    /**
     * Constructor to create the link to the database necessary for table operations.
     *
     * @throws InvalidUrlException in the case of an incorrect URL connection.
     */

    public LogOnSystem() throws InvalidUrlException, FileNotFoundException {
        database = DatabaseConnectionURL.getInstance();
    }

    /**
     * This function takes in the details necessary to create a user, checks if
     * the current user has authority to create a user, then adds the new user
     * to the database.
     *
     * @param user the user name of the new log in.
     * @param pass the password of the new log in.
     * @param role the string representation of a role.
     * @return boolean if the user was successfully added.
     * @throws InvalidUsername in the case that an non-unique user name was passed.
     * @throws InvalidRole     in the case that an incorrect role was passed.
     */

    public boolean addUser(String username, String password, String role) throws InvalidUsername, InvalidRole {
        //Checking user name is valid:
        if (username == null) {
            throw new InvalidUsername("Username cannot be null!");
        }
        //Checking role is valid:
        if (!checkRole(role)) {
            throw new InvalidRole("Role was not a valid input!");
        }
        Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
        Matcher matcher = pattern.matcher(username);
        if (matcher.find()) {
            throw new InvalidUsername("Username cannot have special characters!");
        }
        ArrayList<String> listOfUsernames = database.returnAllUsernames();
        for (int rowBeingChecked = 0; rowBeingChecked < listOfUsernames.size(); rowBeingChecked++) {
            if (listOfUsernames.get(rowBeingChecked).equals(username)) {
                throw new InvalidUsername("Name of username is not unique!");
            }
        }

        //Hashing password, and generating salt:
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 66536, 128);
        SecretKeyFactory factory;
        byte[] hash;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = factory.generateSecret(spec).getEncoded();

            //Pushing to database (with hashed password + salt, instead of password)
            database.addAccount(username, role, byteToString(salt), byteToString(hash));
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function pulls 'salt' and 'hash' from the table and returns it as a concatenated
     * byte of length 32. It does this to prevent the table having to be searched twice.
     *
     * @param user the user name of the new log in.
     * @return byte[] the salt and hash of that user name. First 16 bytes are the salt.
     */

    private byte[] pullSaltAndHashFromDatabase(String username) {
        byte[] saltAndHashCombined = new byte[32];
        byte[] saltTakenFromTable = stringToBytes(database.returnSalt(username));
        byte[] hashTakenFromTable = stringToBytes(database.returnHash(username));
        System.arraycopy(saltTakenFromTable, 0, saltAndHashCombined, 0, 16);
        System.arraycopy(hashTakenFromTable, 0, saltAndHashCombined, 16, 16);
        return saltAndHashCombined;
    }

    /**
     * This function checks the user name exists, and the password is correct.
     *
     * @param username the user name of the account being logged into.
     * @param password the password of the account being logged into.
     * @return boolean whether the password/user name is a correct match.
     */

    public boolean logOn(String username, String password) {
        ArrayList<String> listOfUsernames = database.returnAllUsernames();
        for (int rowBeingChecked = 0; rowBeingChecked < listOfUsernames.size(); rowBeingChecked++) {
            if (listOfUsernames.get(rowBeingChecked).equals(username)) {
                byte[] saltAndHash = pullSaltAndHashFromDatabase(username);
                byte[] saltPulled = new byte[16];
                byte[] hashPulled = new byte[16];

                //Splitting 'saltAndHash' into separate bytes:
                for (int byteCounter = 0; byteCounter < 16; byteCounter++) {
                    saltPulled[byteCounter] = saltAndHash[byteCounter];
                }
                for (int byteCounter = 0; byteCounter < 16; byteCounter++) {
                    hashPulled[byteCounter] = saltAndHash[byteCounter + 16];
                }

                //Creating the hash again, and returning boolean if they're equal.
                KeySpec spec = new PBEKeySpec(password.toCharArray(), saltPulled, 66536, 128);
                try {
                    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                    byte[] hashGenerated = factory.generateSecret(spec).getEncoded();
                    return Arrays.equals(hashGenerated, hashPulled);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return false;
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        //If user name not in table:
        return false;
    }

    /**
     * This function checks the user name and it's matching password.
     * Then the old password is changed into the new password if successful.
     * The user would already be logged on, however
     *
     * @param username    the user name of the account being changed.
     * @param password    the current password, to be replaced.
     * @param newPassword the password to replace the current one.
     * @return boolean true if the password was successfully changed.
     */

    public boolean changePassword(String username, String password, String newPassword) {
        if (!logOn(username, password)) {
            return false;
        }
        String currentRole = database.returnRole(username);
        database.deleteUser(username);
        try {
            return addUser(username, newPassword, currentRole);
        } catch (InvalidRole e) {
            e.printStackTrace();
            return false;
        } catch (InvalidUsername e) {
            //The role and user name have already passed checks so throwing it here is unnecessary.
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This function takes a byte[] and converts it into a String to be saved in the database.
     *
     * @param bytes the byte variable to be converted into the String type
     * @return String the byte variable in type String, with flags "," in between each byte.
     */

    private String byteToString(byte[] bytes) {
        String bytesInStringForm = "";
        for (int byteChecked = 0; byteChecked < bytes.length; byteChecked++) {
            bytesInStringForm += (Integer.toString(bytes[byteChecked]));
            bytesInStringForm += (",");
        }
        return bytesInStringForm;
    }

    /**
     * This function takes a String and converts it into a byte[16] to be saved in the database.
     * The flag between each element passed should be a ",".
     *
     * @param bytesInStringForm the String variable, with flags "," in between each byte.
     * @return byte[] the corresponding byte variable.
     */

    private byte[] stringToBytes(String bytesInStringForm) {
        byte[] bytes = new byte[16];
        String[] byteValues = bytesInStringForm.split(",");
        for (int byteIndex = 0; byteIndex < 16; byteIndex++) {
            bytes[byteIndex] = (byte) Integer.parseInt(byteValues[byteIndex]);
        }
        return bytes;

    }

    /**
     * This function takes a string input and returns the correct Role enum that it
     * corresponds to (assuming the string is correctly formatted).
     *
     * @param roleAsString the string variable that should contain a role name.
     * @return boolean true if the Role exists (including the 'Void' option).
     */

    private static boolean checkRole(String roleAsString) {
        for (Roles roleChecked : Roles.values()) {
            if (roleChecked.toString().equals(roleAsString)) {
                return true;
            }
        }
        return false;
    }
}
