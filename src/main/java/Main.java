import org.json.JSONObject;
import spark.Spark;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("project7db");
//        db.deleteUserByEmail("estas@vienas.lt");
//        db.insertUser("Norvegas","Suomis","estas@vienas.lt","latvis","lenkija");
//        db.changeName(2,"Nebe");
        db.printAllUserData();

        //Address of the server is http://localhost:4567/home
        Spark.port(80);

        Spark.get("/home", (request, response) -> getTextFromFile("Home.html"));

        Spark.get("/getMessages", (request,response) -> db.getMessages());


        // login spark code
        Spark.post("/login", (request, response) -> {
            String username = request.queryParams("username"); // This is received from android
            String password = request.queryParams("password");// This is received from android
            System.out.println("Username: " + username + ", Password: " + password);

            User user = db.userLogin(username, password); // We assign database method to user object, which receives it's parameters from android

            if (user != null) {
                JSONObject json = new JSONObject();
                json.put("id", user.getId());
                json.put("name", user.getName());
                json.put("surname", user.getSurname());
                json.put("email", user.getEmail());
                json.put("username", user.getUsername());
                json.put("password", user.getPassword());
                return json.toString();
            } else {
                response.status(400);
                return "Unsuccessful login";
            }
        });

        //register spark code. Currently in testing stage
        Spark.post("/register", (request, response) -> {

            String name = request.queryParams("name");
            String surname = request.queryParams("surname");
            String email = request.queryParams("email");
            String username = request.queryParams("username");
            String password = request.queryParams("password");
//            User user = new User();
//            Zinutes zinutes = new Zinutes(-1,zinutesTekstas,-1,userId);
            db.insertUser(
                    ""+name,
                    ""+surname,
                    ""+email,
                    ""+username,
                    ""+password);
            System.out.println("Added user with Name :"+ name+", Username: "+ username+", Password: "+password);
            return "Success";

        });

        //Inserting message backend code
        Spark.post("/messageIn", (request, response) -> {
            String message = request.queryParams("message");
            int userId = Integer.parseInt(request.queryParams("userId"));
            String username = request.queryParams("username");
            double latitude = Double.valueOf(request.queryParams("latitude"));
            double longitude = Double.valueOf(request.queryParams("longitude"));
            Message messageObj = new Message(-1,message,userId,username,-1,latitude,longitude);
            db.insertMessage(messageObj);
            System.out.println("New message data, Username:"
                    +messageObj.getUsername()
                    + ", Message:"
                    + messageObj.getMessage()+", latitude: "+messageObj.getLatitude()
                    +", longitude: "+messageObj.getLongitude());
            return "Success";
        });


    }
    /**
     * Teksto nuskaitymas iš failo resursų kataloge
     */
    private static String getTextFromFile(String path) {
        try {
            URI fullPath = Main.class.getClassLoader().getResource(path).toURI();
            return Files.readString(Paths.get(fullPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Loading error";
    }
}
