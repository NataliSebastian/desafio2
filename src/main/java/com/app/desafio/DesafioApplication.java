package com.app.desafio;

import com.app.desafio.client.ApiConsumer;
import com.app.desafio.front.Menu;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DesafioApplication {

    public static void main(String[] args) {

        SpringApplication.run(DesafioApplication.class, args);
        Menu menu = new Menu();
        menu.menu();
    }


}
