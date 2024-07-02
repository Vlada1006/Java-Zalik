package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.*;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Відсутність негативних значень в БД прописані при її створенні

public class Server {
    public Server() {

    }

    public static void main(String[] args) throws Exception {

        login_data[0] = new String[]{"admin", "admin"};
        login_data[1] = new String[]{"User1", "PasswOrd"};

        String Jwt_header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        MessageDigest md = MessageDigest.getInstance("MD5");

        for (int i = 0; i < login_data.length; i++) {
            for (int j = 0; j < login_data[i].length; j++) {
                byte[] hashBytes = md.digest(login_data[i][j].getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                login_data[i][j] = sb.toString();
            }
        }

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(63623), 0);

        HttpContext login_context = server.createContext("/login", new LoginHandler(login_data, Jwt_header));
        HttpContext good_context = server.createContext("/api/good", new GoodHandler());
        HttpContext category_context = server.createContext("/api/category", new CategoryHandler());
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(20));
        server.start();

    }

    static int n_users = 2;
    static String[][] login_data = new String[n_users][2];
    static DatabaseProcessor database;

    static {
        try {
            database = new DatabaseProcessor("jdbc:mysql://localhost:3306/zalik_database", "AE1165et");
        } catch (SQLException e) {
            System.out.println("DB ERROR");
            throw new RuntimeException(e);
        }
    }

    private static String secret_key = "Very-very strong key";

    public static String base64Encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String base64Decode(byte[] bytes) {
        return new String(Base64.getUrlDecoder().decode(bytes));
    }

    private static String hmacSha256(String data, String password) {
        try {
            byte[] hash = password.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return base64Encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean checkJwt(String body, String password){

        StringBuilder builder = new StringBuilder();
        String[] parts = body.split("\\.");
        builder.append(parts[0]).append(".");
        JSONObject user_data = new JSONObject();

        Pattern pattern = Pattern.compile("\\{\"login\":\".+\"\\}");
        String login_name = base64Decode(parts[1].getBytes());
        Matcher matcher = pattern.matcher(login_name);
        if (!matcher.find()){
            return false;
        }
        login_name = matcher.group().substring(10, login_name.length() - 2);
        for (String[] login_datum : login_data) {
            if (Objects.equals(login_datum[0], login_name)) {
                user_data.put("login", login_name);
                user_data.put("password", login_datum[1]);
                builder.append(base64Encode(user_data.toString().getBytes()));
                return Objects.equals(hmacSha256(builder.toString(), password), parts[2]);
            }
        }
        return false;
    }

    private static String readJson(String body, String query, String form) {
        Pattern pattern = Pattern.compile(query);
        Matcher matcher = pattern.matcher(body);
        if(matcher.find()){
            return matcher.group().substring(form.length());
        }
        else{
            return null;
        }
    }

    private static Good_POJO jsonToGood_POJO(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Good_POJO.class);
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    private static Category_POJO jsonToCategory_POJO(String json){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Category_POJO.class);
        }
        catch (JsonProcessingException e){
            return null;
        }
    }

    static class GoodHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            Pattern pattern_id = Pattern.compile("/api/good/\\d+");
            Matcher matcher_id = pattern_id.matcher(exchange.getRequestURI().toString());
            String good_id = "";
            if(matcher_id.find()){
                good_id = matcher_id.group().substring(10);
            }

            InputStream body_stream = exchange.getRequestBody();
            String body = URLDecoder.decode(new String(body_stream.readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            String JWT_header = exchange.getRequestHeaders().getFirst("JWT");

            if(!checkJwt(JWT_header, secret_key)){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            Good_POJO json = new Good_POJO();

            if (!Objects.equals(exchange.getRequestMethod(), "GET") && !Objects.equals(exchange.getRequestMethod(), "DELETE")){     // Скіпаємо перевірку, бо в
                // GET та DELETE немає json
                if ((json = jsonToGood_POJO(readJson(body, "good_json=\\{.+\\}", "good_json="))) == null){
                    exchange.sendResponseHeaders(409, -1);
                    throw new IOException();
                }
            }

            if (Objects.equals(exchange.getRequestMethod(), "GET")){
                json.setGood_id(Integer.parseInt(good_id));
                try {
                    String info = database.run_query(json, 0);
                    byte[] bytes = info.getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(404, -1);
                    throw new RuntimeException(e);
                }
            }

            else if (Objects.equals(exchange.getRequestMethod(), "PUT")){
                try {
                    String info = database.run_query(json, 1);
                    byte[] bytes = info.getBytes();
                    exchange.sendResponseHeaders(201, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(409, -1);
                    throw new RuntimeException(e);
                }
            }


            else if (Objects.equals(exchange.getRequestMethod(), "POST")){
                json.setGood_id(Integer.parseInt(good_id));
                try {
                    database.run_query(json, 2);
                    exchange.sendResponseHeaders(204, -1);
                }
                catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(404, -1);
                    throw new RuntimeException(e);
                }
                catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(409, -1);
                    throw new RuntimeException(e);
                }

            }

            else if (Objects.equals(exchange.getRequestMethod(), "DELETE")){
                json.setGood_id(Integer.parseInt(good_id));
                try {
                    database.run_query(json, 3);
                    exchange.sendResponseHeaders(204, -1);
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(404, -1);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class CategoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            Pattern pattern_id = Pattern.compile("/api/category/\\d+");
            Matcher matcher_id = pattern_id.matcher(exchange.getRequestURI().toString());
            String category_id = "";
            if(matcher_id.find()){
                category_id = matcher_id.group().substring("/api/category/".length());
            }

            InputStream body_stream = exchange.getRequestBody();
            String body = URLDecoder.decode(new String(body_stream.readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            System.out.println(body);

            String JWT_header = exchange.getRequestHeaders().getFirst("JWT");

            if(!checkJwt(JWT_header, secret_key)){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            Category_POJO json = new Category_POJO();



            if (!Objects.equals(exchange.getRequestMethod(), "GET") && !Objects.equals(exchange.getRequestMethod(), "DELETE")){     // Скіпаємо перевірку, бо в
                // GET та DELETE немає json
                if ((json = jsonToCategory_POJO(readJson(body, "category_json=\\{.+\\}", "category_json="))) == null){
                    exchange.sendResponseHeaders(409, -1);
                    System.out.println("A");
                    throw new IOException();
                }
            }

            if (Objects.equals(exchange.getRequestMethod(), "GET")){
                json.setCategory_id(Integer.parseInt(category_id));
                try {
                    String info = database.run_query(json, 0);
                    byte[] bytes = info.getBytes();
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(404, -1);
                    throw new RuntimeException(e);
                }
            }

            else if (Objects.equals(exchange.getRequestMethod(), "PUT")){
                try {
                    String info = database.run_query(json, 1);
                    byte[] bytes = info.getBytes();
                    exchange.sendResponseHeaders(201, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(409, -1);
                    throw new RuntimeException(e);
                }
            }


            else if (Objects.equals(exchange.getRequestMethod(), "DELETE")){
                json.setCategory_id(Integer.parseInt(category_id));
                try {
                    database.run_query(json, 2);
                    exchange.sendResponseHeaders(204, -1);
                } catch (SQLException e) {
                    System.out.println(e);
                    exchange.sendResponseHeaders(404, -1);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class LoginHandler implements HttpHandler {

        private static String JWT_HEADER = null;
        private final String[][] login_data;

        LoginHandler(String[][] login_data, String Jwt_header) {
            this.login_data = login_data;
            JWT_HEADER = Jwt_header;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String params = exchange.getRequestURI().getQuery();

            if (!Objects.equals(exchange.getRequestMethod(), "POST")){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            Pattern login_pattern = Pattern.compile("login=.+&");
            Pattern password_pattern = Pattern.compile("password=.+");

            Matcher login_matcher = login_pattern.matcher(params);
            Matcher password_matcher = password_pattern.matcher(params);

            if (!login_matcher.find()){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            if (!password_matcher.find()){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            String login = login_matcher.group();
            login = login.substring(6, login.length() - 1);
            String password = password_matcher.group();
            password = password.substring(9);

            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            boolean passed = false;
            for (int i = 0; i < login_data.length; i++){
                if (login.equals(login_data[i][0]) && password.equals(login_data[i][1])){
                    passed = true;
                    break;
                }
            }

            if(!passed){
                exchange.sendResponseHeaders(401, -1);
                throw new IOException();
            }

            StringBuilder builder = new StringBuilder();
            StringBuilder password_builder = new StringBuilder();

            builder.append(base64Encode(JWT_HEADER.getBytes())).append(".");

            JSONObject user_data = new JSONObject();
            user_data.put("login", login);

            builder.append(base64Encode(user_data.toString().getBytes()));

            user_data.put("password", password);

            password_builder.append(base64Encode(JWT_HEADER.getBytes())).append(".").append(base64Encode(user_data.toString().getBytes()));

            String signature = hmacSha256(password_builder.toString(), secret_key);

            builder.append(".").append(signature);

            byte[] bytes = builder.toString().getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }



}


