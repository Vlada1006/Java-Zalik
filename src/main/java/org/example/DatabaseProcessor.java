package org.example;

import org.json.JSONObject;

import java.sql.*;
import java.util.Objects;

public class DatabaseProcessor {

    Connection con;

    public DatabaseProcessor(String address, String password) throws SQLException {
        this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/zalik_database", "root", "AE1165et");
        System.out.println("Connected");
    }

    public DatabaseProcessor() {

    }

    public String run_query(Good_POJO data, int command) throws SQLException {
        if(command == 0){
            PreparedStatement stmt_1 = this.con.prepareStatement("SELECT * FROM goods WHERE good_id = ?");
            stmt_1.setInt(1, data.getGood_id());
            JSONObject data_json = new JSONObject();
            ResultSet resultSet = stmt_1.executeQuery();
            resultSet.next();
            data_json.put("good_id", resultSet.getInt("good_id"));
            data_json.put("name", resultSet.getString("name"));
            data_json.put("amount", String.valueOf(resultSet.getInt("amount")));
            data_json.put("price", String.valueOf(resultSet.getInt("price")));
            data_json.put("category_id", String.valueOf(resultSet.getInt("category_id")));
            data_json.put("manufacturer", resultSet.getString("manufacturer"));
            data_json.put("description", resultSet.getString("description"));
            return data_json.toString();
        }
        else if (command == 1){

            PreparedStatement stmt_1 = this.con.prepareStatement("INSERT INTO goods (name, amount, price, category_id, manufacturer, description) VALUES (?, ?, ?, ?, ?, ?)");
            stmt_1.setString(1, data.getName());
            stmt_1.setInt(2, data.getAmount());
            stmt_1.setInt(3, data.getPrice());
            stmt_1.setInt(4, data.getCategory_id());
            stmt_1.setString(5, data.getManufacturer());
            stmt_1.setString(6, data.getDescription());
            stmt_1.executeUpdate();

            PreparedStatement stmt_2 = this.con.prepareStatement("SELECT good_id FROM goods WHERE name = ? AND amount = ? AND price = ? AND manufacturer = ? AND description = ?");
            stmt_2.setString(1, data.getName());
            stmt_2.setInt(2, data.getAmount());
            stmt_2.setInt(3, data.getPrice());
            stmt_2.setString(4, data.getManufacturer());
            stmt_2.setString(5, data.getDescription());
            ResultSet resultSet = stmt_2.executeQuery();
            resultSet.next();
            return String.valueOf(resultSet.getInt(1));
        }
        else if(command == 2){

            PreparedStatement stmt_1 = this.con.prepareStatement("SELECT * FROM goods WHERE good_id = ?");
            stmt_1.setInt(1, data.getGood_id());
            ResultSet resultSet = stmt_1.executeQuery();
            resultSet.next();
            if (Objects.equals(data.getName(), "")){
                data.setName(resultSet.getString("name"));
            }
            if (data.getPrice() == 0){
                data.setPrice(resultSet.getInt("price"));
            }
            if (Objects.equals(data.getManufacturer(), "")){
                data.setManufacturer(resultSet.getString("manufacturer"));
            }
            if (Objects.equals(data.getDescription(), "")){
                data.setDescription(resultSet.getString("description"));
            }
            if (data.getCategory_id() == 0){
                data.setCategory_id(resultSet.getInt("category_id"));
            }
            PreparedStatement stmt_2 = this.con.prepareStatement("UPDATE goods SET name = ?, amount = amount + ?, price = ?, category_id = ?, manufacturer = ?, description = ? WHERE good_id = ?");
            stmt_2.setString(1, data.getName());
            stmt_2.setInt(2, data.getAmount());
            stmt_2.setInt(3, data.getPrice());
            stmt_2.setInt(4, data.getCategory_id());
            stmt_2.setString(5, data.getManufacturer());
            stmt_2.setString(6, data.getDescription());
            stmt_2.setInt(7, data.getGood_id());
            JSONObject data_json = new JSONObject();
            System.out.println(stmt_1);
            System.out.println(stmt_2);
            stmt_2.executeUpdate();
            return null;
        }
        else if(command == 3){
            PreparedStatement stmt_1 = this.con.prepareStatement("DELETE FROM goods WHERE good_id = ?");
            stmt_1.setInt(1, data.getGood_id());
            JSONObject data_json = new JSONObject();
            stmt_1.executeUpdate();
            return null;
        }
        return null;
    }

    public String run_query(Category_POJO data, int command) throws SQLException {
        if(command == 0){
            PreparedStatement stmt_1 = this.con.prepareStatement("SELECT * FROM good_categories WHERE category_id = ?");
            stmt_1.setInt(1, data.getCategory_id());

            JSONObject data_json = new JSONObject();
            ResultSet resultSet = stmt_1.executeQuery();

            resultSet.next();

            data_json.put("category_id", resultSet.getInt("category_id"));
            data_json.put("category_name", resultSet.getString("category_name"));
            System.out.println(data_json);
            return data_json.toString();
        }
        else if (command == 1){

            PreparedStatement stmt_1 = this.con.prepareStatement("INSERT INTO good_categories (category_name) VALUES (?)");
            stmt_1.setString(1, data.getCategory_name());
            stmt_1.executeUpdate();

            PreparedStatement stmt_2 = this.con.prepareStatement("SELECT category_id FROM good_categories WHERE category_name = ?");
            stmt_2.setString(1, data.getCategory_name());
            ResultSet resultSet = stmt_2.executeQuery();
            resultSet.next();
            return String.valueOf(resultSet.getInt(1));
        }
        else if(command == 2){
            PreparedStatement stmt_1 = this.con.prepareStatement("DELETE FROM good_categories WHERE category_id = ?");
            stmt_1.setInt(1, data.getCategory_id());
            JSONObject data_json = new JSONObject();
            stmt_1.executeUpdate();
            return null;
        }
        return null;
    }

}
