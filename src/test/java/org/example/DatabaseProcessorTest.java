package org.example;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseProcessorTest {

    @Test
    public void testRunQueryInsertGood() throws SQLException {
        DatabaseProcessor dbProcessor = new DatabaseProcessor("jdbc:mysql://localhost:3306/zalik_database", "AE1165et");
        Good_POJO good = new Good_POJO();
        good.setName("Test Good");
        good.setAmount(10);
        good.setPrice(100);
        good.setCategory_id(1);
        good.setManufacturer("Test Manufacturer");
        good.setDescription("Test Description");

        String result = dbProcessor.run_query(good, 1);
        assertNotNull(result);
    }

    @Test
    public void testRunQueryDeleteGood() throws SQLException {
        DatabaseProcessor dbProcessor = new DatabaseProcessor("jdbc:mysql://localhost:3306/zalik_database", "AE1165et");
        Good_POJO good = new Good_POJO();
        good.setGood_id(1);
        String result = dbProcessor.run_query(good, 3);
        assertNull(result);
    }

    @Test
    public void testRunQuerySelectCategory() throws SQLException {
        DatabaseProcessor dbProcessor = new DatabaseProcessor("jdbc:mysql://localhost:3306/zalik_database", "AE1165et");
        Category_POJO category = new Category_POJO();
        category.setCategory_id(1);
        String result = dbProcessor.run_query(category, 0);

        JSONObject json = new JSONObject(result);
        assertEquals(1, json.getInt("category_id"));
        // Перевірте інші поля відповідно до вашої бази даних
    }

    @Test
    public void testRunQueryInsertCategory() throws SQLException {
        DatabaseProcessor dbProcessor = new DatabaseProcessor("jdbc:mysql://localhost:3306/zalik_database", "AE1165et");
        Category_POJO category = new Category_POJO();
        category.setCategory_name("Test Category");

        String result = dbProcessor.run_query(category, 1);
        assertNotNull(result);
    }

}
