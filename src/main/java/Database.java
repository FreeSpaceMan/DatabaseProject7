import org.apache.commons.dbcp.BasicDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private BasicDataSource dataSource;

    public Database(String databaseName) {
        dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + databaseName + "?useUnicode=yes&characterEncoding=UTF-8");
        dataSource.setValidationQuery("SELECT 1");

    }

    public void printAllUserData() {
        String query = "SELECT * FROM usersdbp7";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                System.out.println("ID: " + id + ", Name: " + name
                        + ", Surname: " + surname + ", Email: " + email+ ", Username: " + username+", Password: " + password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String name, String surname, String email, String username,String password) {
        String query = "INSERT INTO usersdbp7 (name, surname, email, username, password)"
                + " VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, email);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeName(int id, String name) {
        String query = "UPDATE usersdbp7 SET name=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertMessage(Message message) {
        String query = "INSERT INTO messagesdbp7 (message, userid, username, date, latitude, longitude)"+ " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, message.getMessage());
            statement.setInt(2,message.getUserid());
            statement.setString(3,message.getUsername());
            statement.setLong(4, System.currentTimeMillis());
            statement.setDouble(5, message.getLatitude());
            statement.setDouble(6, message.getLongitude());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getMessages() {
      String query = "SELECT * FROM messagesdbp7";
      JSONArray jsonArray = new JSONArray();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                JSONObject json = new JSONObject();
                json.put("message",resultSet.getString("message"));
                json.put("userid",resultSet.getInt("userid"));
                json.put("username",resultSet.getString("username"));
                json.put("latitude",resultSet.getDouble("latitude"));
                json.put("longitude",resultSet.getDouble("longitude"));
//                json.put("data",resultSet.getLong("data"));
//                json.put("userId",resultSet.getInt("user_id"));
                jsonArray.put(json);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public User userLogin(String username, String password) {
        String query = "SELECT * FROM usersdbp7 WHERE username=? AND password=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1,username);
            statement.setString(2,password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setSurname(resultSet.getString("surname"));
                user.setEmail(resultSet.getString("email"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUserByEmail(String email) {
        String query = "DELETE FROM usersdbp7 WHERE email=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            int numberOfAffectedRows = statement.executeUpdate();
            System.out.println("Number of affected rows: " + numberOfAffectedRows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
