/*
 * Copyright 2015 Len Payne <len.payne@lambtoncollege.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package services;

import java.io.StringReader;
import java.sql.*;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
@Path("/blog")
public class Blog {
    @GET
    public String get() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String jdbc = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" +
                System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/practiceblog";
        String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        String pass = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        Connection conn = DriverManager.getConnection(jdbc, user, pass);
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM blog");
        JsonArrayBuilder builder = Json.createArrayBuilder();
        while (rs.next()) {
            builder.add(Json.createObjectBuilder()
            .add("title", rs.getString("title"))
            .add("text", rs.getString("text"))
            .add("time", rs.getString("time"))
            .add("id", rs.getInt("id"))
            .build()
            );                    
        }
        return builder.build().toString();
    }
    
    @POST
    public String post(String str) throws ClassNotFoundException, SQLException {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        String title = json.getString("title");
        String text = json.getString("text");
        
        Class.forName("com.mysql.jdbc.Driver");
        String jdbc = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" +
                System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/practiceblog";
        String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        String pass = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        Connection conn = DriverManager.getConnection(jdbc, user, pass);
        
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO blog (title, text, time) VALUES (?, ?, NOW())");
        pstmt.setString(1, title);
        pstmt.setString(2, text);
        pstmt.executeUpdate();
        
        return get();
    }
            
}
