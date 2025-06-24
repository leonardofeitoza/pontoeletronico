package com.PontoEletronico.pontoeletronico.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabaseConfigLoader {

    private static final String CONFIG_FILE_PATH = "src/main/resources/config-db.txt"; // Pode ser um diretório externo

    public static Map<String, String> loadDatabaseConfig() {
        Map<String, String> config = new HashMap<>();
        try (Stream<String> lines = Files.lines(Paths.get(CONFIG_FILE_PATH))) {
            lines.filter(line -> line.contains("=")) // Filtra apenas linhas com '='
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        config.put(parts[0].trim(), parts[1].trim());
                    });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar as configurações do banco de dados", e);
        }
        return config;
    }
}
