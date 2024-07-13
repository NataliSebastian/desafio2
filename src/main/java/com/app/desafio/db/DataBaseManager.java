package com.app.desafio.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DataBaseManager {

    private final String url = "jdbc:postgresql://localhost:5432/libros_reto";
    private final String user = "postgres";
    private final String password = "123";

    public void insertarAutorYLibro(Long idAutor, String autorName, int birthYear, int deathYear, Long libros, Long idLibro, Long autores, String libroTitle, String language, int downloadCount) {
        String autorCheckSql = "SELECT COUNT(*) FROM public.autores WHERE name = ?";
        String autorCheckIdSql = "SELECT id FROM public.autores WHERE name = ?";
        String autorTotal = "SELECT COUNT(*) FROM public.autores";
        String autorInsertSql = "INSERT INTO public.autores(id, name, birth_year, death_year, libros) VALUES (?, ?, ?, ?, ?)";

        String libroCheckSql = "SELECT COUNT(*) FROM public.libros WHERE title = ?";
        String libroTotal = "SELECT COUNT(*) FROM public.libros";
        String libroInsertSql = "INSERT INTO public.libros(id, autores, title, language, download_count) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement autorCheckStmt = conn.prepareStatement(autorCheckSql);
             PreparedStatement autorCheckIdStmt = conn.prepareStatement(autorCheckIdSql);
             PreparedStatement autorInsertStmt = conn.prepareStatement(autorInsertSql);
             PreparedStatement autorTotalStmt = conn.prepareStatement(autorTotal);
             PreparedStatement libroTotalStmt = conn.prepareStatement(libroTotal);
             PreparedStatement libroCheckStmt = conn.prepareStatement(libroCheckSql);
             PreparedStatement libroInsertStmt = conn.prepareStatement(libroInsertSql)) {

            // Verificar si el autor ya existe
            autorCheckStmt.setString(1, autorName);
            ResultSet autorRs = autorCheckStmt.executeQuery();
            autorRs.next();
            //autorIdExist = autorRs.getLong("id");
            autorCheckIdStmt.setString(1, autorName);
            ResultSet autorIdExist = autorCheckIdStmt.executeQuery();
            autorIdExist.next();
            int autorCount = autorRs.getInt(1);


            // Verificar total de autores registrados
            ResultSet autorId = autorTotalStmt.executeQuery();
            autorId.next();
            int autorTotalCount = autorId.getInt(1);

            // Verificar total de libros registrados
            ResultSet libroId = libroTotalStmt.executeQuery();
            libroId.next();
            int libroTotalCount = autorId.getInt(1);


            // Verificar si el libro ya existe
            libroCheckStmt.setString(1, libroTitle);
            ResultSet libroRs = libroCheckStmt.executeQuery();
            libroRs.next();
            int libroCount = libroRs.getInt(1);


            if (autorCount > 0) {
                Long autorIdExistValue = autorIdExist.getLong("id");
                if (libroCount == 0){
                    libroInsertStmt.setLong(1, libroTotalCount+1);
                    libroInsertStmt.setLong(2, autorIdExistValue);
                    libroInsertStmt.setString(3, libroTitle);
                    libroInsertStmt.setString(4, language);
                    libroInsertStmt.setInt(5, downloadCount);
                    libroInsertStmt.executeUpdate();
                    System.out.println("El libro se insertó exitosamente.");
                } else {
                    System.out.println("El libro ya existe y no se insertará.");
                }
            } else {
                System.out.println("El autor ya existe y no se insertará.");
            }
            if ((autorCount == 0) && (libroCount == 0)) {
                autorInsertStmt.setLong(1, autorTotalCount+1);
                autorInsertStmt.setString(2, autorName);
                autorInsertStmt.setInt(3, birthYear);
                autorInsertStmt.setInt(4, deathYear);
                autorInsertStmt.setLong(5, libroTotalCount+1);
                autorInsertStmt.executeUpdate();
                libroInsertStmt.setLong(1, libroTotalCount+1);
                libroInsertStmt.setLong(2, autorTotalCount+1);
                libroInsertStmt.setString(3, libroTitle);
                libroInsertStmt.setString(4, language);
                libroInsertStmt.setInt(5, downloadCount);
                libroInsertStmt.executeUpdate();
                System.out.println("El autor y libro se insertaron exitosamente.");
            } else {
                System.out.println("El libro ya existe y no se insertará.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarAutores() {
        String sql = "SELECT a.id, a.name, a.birth_year, a.death_year, l.title FROM public.autores a JOIN public.libros l ON a.id = l.autores ORDER BY a.id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int currentAuthorId = 0;

            while (rs.next()) {
                int authorId = rs.getInt("id");
                if (authorId != currentAuthorId) {
                    // Nuevo autor encontrado
                    if (currentAuthorId != 0) {
                        // Mostrar un separador entre autores
                        System.out.println("\n-------------\n");
                    }
                    //System.out.println("ID Autor: " + authorId);
                    System.out.println("Autor: " + rs.getString("name"));
                    System.out.println("Año de Nacimiento: " + rs.getInt("birth_year"));
                    System.out.println("Año de Fallecimiento: " + rs.getInt("death_year"));
                    System.out.print("Libros: " );
                    currentAuthorId = authorId;
                }

                // Mostrar título del libro para el autor actual
                System.out.println(" " + rs.getString("title") + " ");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarLibros() {
        String sql = "SELECT l.id, a.name AS autor, l.title, l.language, l.download_count FROM public.libros l JOIN public.autores a ON l.autores = a.id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("---------- LIBRO ----------");
                //System.out.println("ID Libro: " + rs.getInt("id"));
                System.out.println("Título: " + rs.getString("title"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Idioma: " + rs.getString("language"));
                System.out.println("Descargas: " + rs.getInt("download_count"));
                System.out.println("---------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarAutoresVivos(int anio) {
        String sql = "SELECT a.id, a.name, a.birth_year, a.death_year, l.title FROM public.autores a JOIN public.libros l ON a.id = l.autores WHERE ? >= a.birth_year AND (? <= a.death_year OR a.death_year IS NULL) ORDER BY a.id";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, anio);
            pstmt.setInt(2, anio);

            ResultSet rs = pstmt.executeQuery();
            int currentAuthorId = 0;

            while (rs.next()) {
                int authorId = rs.getInt("id");
                if (authorId != currentAuthorId) {
                    // Nuevo autor encontrado
                    if (currentAuthorId != 0) {
                        // Mostrar un separador entre autores
                        System.out.println("---------------------------");
                    }
                    //System.out.println("ID Autor: " + authorId);
                    System.out.println("Autor: " + rs.getString("name"));
                    System.out.println("Año de Nacimiento: " + rs.getInt("birth_year"));
                    System.out.println("Año de Fallecimiento: " + rs.getObject("death_year"));
                    System.out.print("Libros: ");
                    currentAuthorId = authorId;
                }

                // Mostrar título del libro para el autor actual
                System.out.println(" " + rs.getString("title") + " ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarLibrosPorIdioma(String language) {
        String sql = "SELECT l.id, a.name AS autor, l.title, l.language, l.download_count FROM public.libros l JOIN public.autores a ON l.autores = a.id WHERE ? = (l.language)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, language);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID Libro: " + rs.getInt("id"));
                System.out.println("Autor: " + rs.getString("autor"));
                System.out.println("Título: " + rs.getString("title"));
                System.out.println("Idioma: " + rs.getString("language"));
                System.out.println("Descargas: " + rs.getInt("download_count"));
                System.out.println("--------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}