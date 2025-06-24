package com.PontoEletronico.pontoeletronico;

import java.sql.Connection;
import java.sql.DriverManager;

public class TesteConexao {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://LEONARDO:1433;databaseName=PontoEletronico;encrypt=true;trustServerCertificate=true;integratedSecurity=true";
        try {
            // Carregar o driver JDBC
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(url);
            System.out.println("Conexão bem-sucedida!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao banco de dados:");
            e.printStackTrace();
        }
    }
}
