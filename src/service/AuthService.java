
package service;

import java.io.*;
import java.nio.file.*;

public class AuthService {

    private static final String USER_FILE = "data/users.txt";

    public AuthService() {
        initializeFile();
    }

    private void initializeFile() {
        try {
            Files.createDirectories(Paths.get("data"));

            File file = new File(USER_FILE);

            if (!file.exists()) {
                try (BufferedWriter writer =
                         new BufferedWriter(new FileWriter(file))) {
                    writer.write("admin,admin123");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLogin(String username, String password) {
        try (BufferedReader reader =
                 new BufferedReader(new FileReader(USER_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",", 2);

                if (details.length == 2
                        && details[0].trim().equals(username)
                        && details[1].trim().equals(password)) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean registerUser(String username, String password) {

        if (username == null || password == null
                || username.trim().isEmpty()
                || password.trim().isEmpty()
                || username.contains(",")) {
            return false;
        }

        try {
            Files.createDirectories(Paths.get("data"));

            try (BufferedReader reader =
                     new BufferedReader(new FileReader(USER_FILE))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(",", 2);

                    if (details.length == 2
                            && details[0].trim().equalsIgnoreCase(username.trim())) {
                        return false;
                    }
                }
            }

            try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(USER_FILE, true))) {

                writer.write(username.trim() + "," + password.trim());
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
