/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package insertindb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static String pathToExmaple = "app/src/main/resources/russian_english_adjectives.csv";
    private static String pathToExmaple2 = "app/src/main/resources/russian_english_nouns.csv";
    private String url = "jdbc:postgresql://localhost:5432/english_russian_translation";
    private String username = "postgres";
    private String password = "postgres";

    private String sqlQuerry;

    private static Map<String, String> libRu = new HashMap<>();
    private static Map<String, String> libEn = new HashMap<>();


    private String insertRuWord = """
                insert into ru_en_translations(ru_word, en_word)
                values(?, ?)
            """;

    private String checkInsertRuWord = """
            select
                ru_word,
                en_word
            from ru_en_translations
            where ru_word = ?
            """;

    private String insertEnWord = """
            insert into en_ru_translations(en_word, ru_word)
            values(?, ?)
            """;

    private String checkInsertEnWord = """
                select
                    en_word,
                    ru_word
                from en_ru_translations
                where en_word = ?
            """;

    private String updateRuWord = """
            update ru_en_translations
            set
                en_word = ?
            where ru_word = ?
            """;

    private String updateEnWord = """
           update en_ru_tranlations
           set ru_word = ?
           where en_word = ?
            """;

    public static void main(String[] args) throws IOException, SQLException {
        long start = System.currentTimeMillis();
        App app = new App();
        app.startParsing(pathToExmaple);
        app.startParsing(pathToExmaple2);
        // app.getMaxLengthRu();
        // app.getMaxLengthEn();
        Connection ruConnection = app.getConnection();
        Connection enConnection = app.getConnection();
        app.insertInRuDB(ruConnection);
        app.insertInEnDB(enConnection);
        long end = System.currentTimeMillis();
        System.out.println("Work time is : " + (end - start));
    }

    private void getMaxLengthRu() {
        int maxLength = 0;
        String key = "";
        String value = "";
        for(Map.Entry<String, String> entry : libRu.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().length() > maxLength) {
                    maxLength = entry.getValue().length();
                    key = entry.getKey();
                    value = entry.getValue();
                }
            }
        }
        System.out.println(maxLength + key + " - " + value);
    }

    private void getMaxLengthEn() {
        int maxLength = 0;
        String key = "";
        String value = "";
        for(Map.Entry<String, String> entry : libEn.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().length() > maxLength) {
                    maxLength = entry.getValue().length();
                    key = entry.getKey();
                    value = entry.getValue();
                }
            }
        }
        System.out.println(maxLength + key + " - " + value);
    }

    private void insertInRuDB(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertRuWord)) {
            for (Map.Entry<String, String> entry : libRu.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setString(2, entry.getValue());
                statement.executeUpdate();
            }
            connection.close();
        }
    }
    private void insertInEnDB(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(insertEnWord)) {
            for (Map.Entry<String, String> entry : libEn.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setString(2, entry.getValue());
                statement.executeUpdate();
            }
            connection.close();
        }
    }

    private void startParsing(String exmaple) throws IOException {
        List<String> buffer = new ArrayList<>();
        buffer = Files.readAllLines(Path.of(exmaple));
        for (int i = 0; i < buffer.size(); i++) {
            parseLineAndInputInMap(buffer.get(i));
        }
    }

    private void parseLineAndInputInMap(String string) {
        String[] buf = string.split(",");
        if (buf[1].startsWith("Adjective")) return;
        checkInMap(buf);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private void checkInMap(String[] tr) {
        if (libRu.containsKey(tr[0])) {
            String s = libRu.get(tr[0]);
            s = s + ", " + tr[1];
            libRu.put(tr[0], s);
        } else {
            libRu.put(tr[0], tr[1]);
        }

        if (libEn.containsKey(tr[1])) {
            String s = libEn.get(tr[1]);
            s += ", " + tr[0];
            libEn.put(tr[1], s);
        } else {
            libEn.put(tr[1], tr[0]);
        }
    }

//   &quot;СЧЁТЫ&quot;</ar>
// <ar><k>abacuses</k>
// ABACUSES

}
