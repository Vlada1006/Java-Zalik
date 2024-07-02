package org.example;

import org.json.JSONObject;

import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import javax.swing.*;

public class Client extends JFrame implements ActionListener{

    String address = "http://localhost:63623/";

    @SuppressWarnings("InjectedReferences")
    String login, password, JWT = "", category_id, category_name, good_id, good_name, good_amount,
            good_price, good_category_id, good_manufacturer, good_description, output;
    static JTextArea login_area, password_area, category_id_area, category_name_area, good_id_area, good_name_area, good_amount_area,
            good_price_area, good_category_id_area, good_manufacturer_area, good_description_area;
    static JButton login_button, add_good_button, delete_good_button, find_good_button, change_good_button, buy_good_button,
            sell_good_button, add_category_button, delete_category_button, get_category_button,
            global_statistics_button, category_statistics_button, global_price_button, category_price_button;

    static JLabel login_label, category_id_label, category_name_label, good_id_label, good_name_label, good_amount_label,
            good_price_label, good_category_id_label, good_manufacturer_label, good_description_label;

    static JTextPane output_field = new JTextPane();
    static String[] array_of_names = {"id", "name", "amount", "price", "category_id", "manufacturer", "description"};
    static JLabel[] array_of_labels = new JLabel[array_of_names.length];
    static JTextArea[] array_of_text_areas = new JTextArea[array_of_names.length];

    int label_and_textArea_size = 120;
    int padding = 10;
    int left_padding = 10;

    void authorization_check(){
        if (JWT.equals("")){
            JOptionPane.showMessageDialog(this,"Authorize first");
            throw new RuntimeException();
        }
    }

    public boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    Client() throws URISyntaxException {
        super("Client");

        login_label = new JLabel();
        login_label.setText("Login and password");
        login_label.setBounds(10,10,120,20);

        login_area = new JTextArea();
        login_area.setBounds(10,40,150,20);

        password_area = new JTextArea();
        password_area.setBounds(10,70,150,20);

        login_button = new JButton("Login");
        login_button.setBounds(10,100,100,30);

        for(int i = 0; i < array_of_names.length; i++){

            array_of_labels[i] = new JLabel();
            array_of_labels[i].setText(array_of_names[i]);
            array_of_labels[i].setBounds(left_padding + label_and_textArea_size * i + padding*i,170, label_and_textArea_size,20);
            array_of_text_areas[i] = new JTextArea();
            array_of_text_areas[i].setBounds(left_padding + label_and_textArea_size * i + padding*i,200, label_and_textArea_size,20);
        }

        category_id_label = new JLabel();
        category_id_label.setText("category_id");
        category_id_label.setBounds(200,10,150,20);
        category_id_area = new JTextArea();
        category_id_area.setBounds(200,40,150,20);

        category_name_label = new JLabel();
        category_name_label.setText("category_name");
        category_name_label.setBounds(360,10,150,20);
        category_name_area = new JTextArea();
        category_name_area.setBounds(360,40,150,20);


        add_category_button = new JButton("Add category");
        add_category_button.setBounds(200,70,120,30);

        get_category_button = new JButton("Get category");
        get_category_button.setBounds(330,70,120,30);

        delete_category_button = new JButton("Delete category");
        delete_category_button.setBounds(460,70,120,30);

        add_good_button = new JButton("Add good");
        add_good_button.setBounds(10,250,120,30);

        change_good_button = new JButton("Change good");
        change_good_button.setBounds(140,250,120,30);

        delete_good_button = new JButton("Delete good");
        delete_good_button.setBounds(270,250,120,30);

        find_good_button = new JButton("Find good");
        find_good_button.setBounds(400,250,120,30);

        sell_good_button = new JButton("Sell good");
        sell_good_button.setBounds(530,250,120,30);

        buy_good_button = new JButton("Buy good");
        buy_good_button.setBounds(660,250,120,30);

        // global_statistics_button, category_statistics_button, global_price_button, category_price_button

        global_statistics_button = new JButton("Global statistics");
        global_statistics_button.setBounds(10,320,150,30);

        category_statistics_button = new JButton("Category statistics");
        category_statistics_button.setBounds(170,320,150,30);

        global_price_button = new JButton("Global price");
        global_price_button.setBounds(330,320,150,30);

        category_price_button = new JButton("Category price");
        category_price_button.setBounds(490,320,150,30);


        output_field.setBounds(10,370,700,300);
        output_field.setEditable(false);
        output_field.setBackground(null);
        output_field.setBorder(null);
        output_field.setText("Stats will be here");


        login_button.addActionListener(this);
        add_good_button.addActionListener(this);
        change_good_button.addActionListener(this);
        delete_good_button.addActionListener(this);
        find_good_button.addActionListener(this);
        sell_good_button.addActionListener(this);
        buy_good_button.addActionListener(this);
        add_category_button.addActionListener(this);
        delete_category_button.addActionListener(this);
        get_category_button.addActionListener(this);


        for(int i = 0; i < array_of_names.length; i++){
            add(array_of_labels[i]);
            add(array_of_text_areas[i]);
        }

        add(login_button);add(login_area);add(login_label);add(password_area);add(category_id_label);add(category_id_area);
        add(category_name_label);add(category_name_area);add(category_name_label); add(add_good_button);
        add(change_good_button); add(delete_good_button); add(sell_good_button); add(buy_good_button); add(add_category_button);
        add(get_category_button); add(delete_category_button); add(find_good_button); add(output_field);
        add(category_price_button); add(global_price_button); add(category_statistics_button);
        add(global_statistics_button);

        setSize(1000,600);
        setLayout(null);
        setVisible(true);
    }



    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == login_button) {
            MessageDigest md;
            login = login_area.getText();
            password = password_area.getText();

            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }

            byte[] hashBytes = md.digest(login.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            login = sb.toString();

            hashBytes = md.digest(password.getBytes());
            sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            password = sb.toString();

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(address + "login?login=" + login + "&password=" + password))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200){
                    JWT = response.body();
                    JOptionPane.showMessageDialog(this,"Authorized successfully");
                }
                else{
                    //noinspection InjectedReferences
                    JWT = "";
                    JOptionPane.showMessageDialog(this,"Incorrect login/password");
                }

            } catch (URISyntaxException ex) {
                System.out.println(ex);
                throw new RuntimeException(ex);
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        }

        if (e.getSource() == add_good_button) {
            authorization_check();
            JSONObject data_json = new JSONObject();
            for (int i = 1; i < array_of_names.length; i++) {
                if (array_of_text_areas[i].getText().length() == 0) {
                    JOptionPane.showMessageDialog(this, "Fill all forms");
                    throw new RuntimeException();
                }
                if (isNumeric(array_of_text_areas[i].getText())){
                    data_json.put(array_of_names[i], Integer.parseInt(array_of_text_areas[i].getText()));
                }
                else{
                    data_json.put(array_of_names[i], array_of_text_areas[i].getText());
                }
            }

            String data_string = data_json.toString();

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/good"))
                        .PUT(HttpRequest.BodyPublishers.ofString("good_json="+data_string))
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 201) {
                array_of_text_areas[0].setText(response.body());
                JOptionPane.showMessageDialog(this, "Added successfully");
            } else {
                array_of_text_areas[0].setText("");
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }

        if (e.getSource() == change_good_button) {
            authorization_check();
            JSONObject data_json = new JSONObject();
            if (array_of_text_areas[0].getText().length() == 0 || !isNumeric(array_of_text_areas[0].getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }

            int id = Integer.parseInt(array_of_text_areas[0].getText());
            for (int i = 1; i < array_of_names.length; i++) {

                if (array_of_names[i].equals("amount")){
                    array_of_text_areas[i].setText("");
                }

                if (!array_of_names[i].equals("")){
                    if (isNumeric(array_of_text_areas[i].getText())){
                        data_json.put(array_of_names[i], Integer.parseInt(array_of_text_areas[i].getText()));
                    }
                    else{
                        data_json.put(array_of_names[i], array_of_text_areas[i].getText());
                    }
                }
            }

            String data_string = data_json.toString();

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/good/" + id))
                        .POST(HttpRequest.BodyPublishers.ofString("good_json="+data_string))
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 204) {
                JOptionPane.showMessageDialog(this, "Updated successfully");
            }
            else if (response.statusCode() == 404) {
                JOptionPane.showMessageDialog(this, "Data already exists");
            }
            else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }


        if (e.getSource() == delete_good_button) {
            authorization_check();

            if (array_of_text_areas[0].getText().length() == 0 || !isNumeric(array_of_text_areas[0].getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }
            int id = Integer.parseInt(array_of_text_areas[0].getText());

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/good/" + id))
                        .DELETE()
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 204) {
                JOptionPane.showMessageDialog(this, "Deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }

        if (e.getSource() == find_good_button) {
            authorization_check();

            if (array_of_text_areas[0].getText().length() == 0 || !isNumeric(array_of_text_areas[0].getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }
            int id = Integer.parseInt(array_of_text_areas[0].getText());

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/good/" + id))
                        .GET()
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }

            if (response.statusCode() == 200) {
                JSONObject data = new JSONObject(response.body());
                System.out.println(data);
                for(int i = 1; i < array_of_names.length; i++){
                    array_of_text_areas[i].setText((String) data.get(array_of_names[i]));
                }
                JOptionPane.showMessageDialog(this, "Found successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }


        if (e.getSource() == sell_good_button || e.getSource() == buy_good_button) {
            authorization_check();
            JSONObject data_json = new JSONObject();
            if (array_of_text_areas[0].getText().length() == 0 || !isNumeric(array_of_text_areas[0].getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }

            int id = Integer.parseInt(array_of_text_areas[0].getText());
            for (int i = 1; i < array_of_names.length; i++) {

                if (!array_of_names[i].equals("")){
                    if (array_of_names[i].equals("amount")){
                        if (array_of_text_areas[i].getText().length() == 0 || !isNumeric(array_of_text_areas[i].getText())) {
                            JOptionPane.showMessageDialog(this, "Fill \"amount\" field correctly");
                            throw new RuntimeException();
                        }
                        if (Integer.parseInt(array_of_text_areas[i].getText()) > 0){
                            if (e.getSource() == buy_good_button) {
                                data_json.put(array_of_names[i], Integer.parseInt(array_of_text_areas[i].getText()));
                            }
                            else {
                                data_json.put(array_of_names[i], -Integer.parseInt(array_of_text_areas[i].getText()));
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(this, "Fill \"amount\" field with value greater than zero");
                            throw new RuntimeException();
                        }

                    }

                }
            }

            String data_string = data_json.toString();
            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/good/" + id))
                        .POST(HttpRequest.BodyPublishers.ofString("good_json="+data_string))
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 204) {
                JOptionPane.showMessageDialog(this, "Updated successfully");
            }
            else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }


        if (e.getSource() == add_category_button) {
            authorization_check();
            JSONObject data_json = new JSONObject();
            if (category_name_area.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Fill category name");
                throw new RuntimeException();
            }

            data_json.put("category_name", category_name_area.getText());

            String data_string = data_json.toString();

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/category"))
                        .PUT(HttpRequest.BodyPublishers.ofString("category_json="+data_string))
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 201) {
                category_id_label.setText(response.body());
                JOptionPane.showMessageDialog(this, "Added successfully");
            } else {
                category_id_label.setText("");
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }


        if (e.getSource() == delete_category_button) {
            authorization_check();

            if (category_id_area.getText().length() == 0 || !isNumeric(category_id_area.getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }
            int id = Integer.parseInt(category_id_area.getText());

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/category/" + id))
                        .DELETE()
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 204) {
                JOptionPane.showMessageDialog(this, "Deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }

        if (e.getSource() == get_category_button) {
            authorization_check();

            if (category_id_area.getText().length() == 0 || !isNumeric(category_id_area.getText())) {
                JOptionPane.showMessageDialog(this, "Fill \"id\" field correctly");
                throw new RuntimeException();
            }
            int id = Integer.parseInt(category_id_area.getText());

            HttpRequest request;
            HttpResponse<String> response;
            try {
                request = HttpRequest.newBuilder()
                        .uri(new URI(address + "api/category/" + id))
                        .GET()
                        .headers("JWT", JWT)
                        .build();
                response = null;
                response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
            }
            catch (IOException | InterruptedException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                category_name_area.setText((String) json.get("category_name"));
                JOptionPane.showMessageDialog(this, "Got successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Something went wrong");
            }
        }

    }

    public static void main(String[] args) throws URISyntaxException {
        new Client();
    }
}