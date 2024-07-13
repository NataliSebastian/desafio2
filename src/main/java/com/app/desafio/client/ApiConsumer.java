package com.app.desafio.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.app.desafio.db.DataBaseManager;
import com.app.desafio.models.Autores;
import com.app.desafio.models.Libros;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiConsumer {
    private DataBaseManager dataBaseManager = new DataBaseManager();
    public void busqueda() {
        Scanner scanner = new Scanner(System.in);
        Libros libro = new Libros();
        Autores autor = new Autores();
        System.out.print("Ingresa el nombre del libro que desea buscar: ");
        String paramValue = "";

        while (paramValue.isEmpty()) {
            paramValue = scanner.nextLine();
            if (paramValue.isEmpty()) {
                System.out.print("El valor no puede estar vacío. Ingresa el valor para param: ");
            }
        }

        try {
            String paramValueEncoded = URLEncoder.encode(paramValue, StandardCharsets.UTF_8);
            String urlString = "https://gutendex.com/books/?search=" + paramValueEncoded;
            //String urlString = "https://gutendex.com/books/?search=" + paramValue;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String responseString = response.toString();
                System.out.println("Respuesta de la API: " + responseString);
                JSONObject jsonResponse = new JSONObject(responseString);
                if (jsonResponse.has("results")) {
                    JSONArray results = jsonResponse.getJSONArray("results");
                    if (results.length()>0){
                        JSONObject firstBook = results.getJSONObject(0);
                        libro.setTitle(firstBook.getString("title"));
                        libro.setLanguage(firstBook.getJSONArray("languages").getString(0));
                        libro.setDownloadCount(firstBook.getInt("download_count"));
                        autor.setName(firstBook.getJSONArray("authors").getJSONObject(0).getString("name"));
                        autor.setBirthYear(firstBook.getJSONArray("authors").getJSONObject(0).optInt("birth_year",0));
                        autor.setDeathYear(firstBook.getJSONArray("authors").getJSONObject(0).optInt("death_year",0));
                        String nombre = autor.getName();
                        int nacimiento = autor.getBirthYear();
                        int muerte = autor.getDeathYear();
                        Long idAutor = 1L;
                        String titulo = libro.getTitle();
                        String idioma = libro.getLanguage();
                        int descargas = libro.getDownloadCount();
                        System.out.println("----- LIBRO -----");
                        System.out.println("Título: " + titulo);
                        System.out.println("Autor: " + nombre);
                        System.out.println("Idioma: " + idioma);
                        System.out.println("Descargas: " + descargas);
                        System.out.println("-----------------");
                        dataBaseManager.insertarAutorYLibro(1L, nombre, nacimiento, muerte, 2L, 2L, 1L, titulo, idioma, descargas);
                    }else{
                        System.out.println("Libro no encontrado");
                    }
                } else {
                    System.out.println("No se encontraron resultados de la busqueda.");
                }
            } else {
                System.out.println("Error en la solicitud: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

