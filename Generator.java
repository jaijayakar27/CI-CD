import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Generator {

    public static void main(String[] args) {
        String rootFolderPath ="C:\\Users\\91738\\Desktop\\VIT 7th SEM\\Project\\sql_files";
        File rootFolder = new File(rootFolderPath);

        if (!rootFolder.isDirectory()) {
            System.out.println("The provided path is not a valid directory.");
            return;
        }

        generateChangeLog(rootFolder);
    }

    public static void generateChangeLog(File folder) {
        if (!folder.isDirectory()) {
            return;
        }

        // Create or overwrite changelog.xml in the current folder
        File changelogFile = new File(folder, "changelog.xml");
        try (FileWriter writer = new FileWriter(changelogFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\n");
            writer.write("                  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            writer.write("                  xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog\n");
            writer.write("                                      http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.8.xsd\">\n");

            // First, include all the SQL files in the current folder
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".sql")) {
                        writer.write("    <changeSet id=\"" + file.getName() + "\" author=\"auto-generator\">\n");
                        writer.write("        <sqlFile path=\"" + file.getName() + "\" />\n");
                        writer.write("    </changeSet>\n");
                    }
                }
            }

            // Now, recursively traverse subfolders and include their changelogs
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        generateChangeLog(file); // Recursively generate changelog in subdirectories
                        writer.write("    <include file=\"" + file.getName() + "/changelog.xml\" relativeToChangelogFile=\"true\" />\n");
                    }
                }
            }

            writer.write("</databaseChangeLog>\n");
            System.out.println("Generated: " + changelogFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Error generating changelog for folder: " + folder.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
