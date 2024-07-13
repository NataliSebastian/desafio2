package com.app.desafio.front;

import com.app.desafio.client.ApiConsumer;
import com.app.desafio.db.DataBaseManager;
import java.util.Scanner;

public class Menu {
    private DataBaseManager dataBaseManager = new DataBaseManager();
    public void menu(){
        Scanner scanner = new Scanner(System.in);
        boolean salirOperaciones = false;

        while (!salirOperaciones) {
            System.out.println("\nElija la opción a través de su número:");
            System.out.println("1. Buscar libro por título");
            System.out.println("2. Listar libros registrados");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores vivos en un determinado año");
            System.out.println("5. Listar libros por idioma");
            System.out.println("6. Salir");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    ApiConsumer apiConsumer = new ApiConsumer();
                    apiConsumer.busqueda();
                    break;
                case 2:
                    dataBaseManager.listarLibros();
                    break;
                case 3:
                    dataBaseManager.listarAutores();
                    break;
                case 4:
                    System.out.println("\nInserte el año vivo de autor(es) que desea buscar:");
                    int anio = scanner.nextInt();
                    dataBaseManager.listarAutoresVivos(anio);
                    break;
                case 5:
                    System.out.println("\nIngrese el idioma para buscar los libros:");
                    System.out.println("es - Español");
                    System.out.println("en - Inglés");
                    System.out.println("fr - Francés");
                    System.out.println("pt - Portugués");
                    String idioma = scanner.next();
                    scanner.nextLine();
                    dataBaseManager.listarLibrosPorIdioma(idioma);
                    break;
                case 6:
                    salirOperaciones = true;
                    break;
                default:
                    System.out.println("Opción no válida");
                    break;
            }
        }
        scanner.close();
    }
}
