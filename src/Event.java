
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author japin
 */
public class Event {

    /**
     * Database connection
     *
     * @return connection
     * @throws SQLException
     */
    private static Connection connect() throws SQLException {
        //url
        String url = "jdbc:derby://localhost:1527/integrativeDb";

        //Database connection
        Connection conn = DriverManager.getConnection(url, "root", "root");
        return conn;
    }

    /**
     * Create event method
     *
     * @param title
     * @param content
     * @param userId
     * @return true or error
     */
    public Map<String, Object> create(String title, String content, int userId) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();
        //verify if admin
        boolean verification = verifyUser(userId);

        if (verification == false) {
            String message = "User not admin";
            result.put("success", false);
            result.put("message", message);
        } else {//insert to posts table or return error
            try {
                String sql = "INSERT INTO app.events (title, content, created_by) VALUES (?,?,?)";
                PreparedStatement pstmt;
                pstmt = connect().prepareStatement(sql);

                pstmt.setString(1, title);
                pstmt.setString(2, content);
                pstmt.setInt(3, userId);

                int rowsInserted = pstmt.executeUpdate();

                if (rowsInserted > 0) {
                    result.put("success", true);
                }
            } catch (SQLException e) {
                result.put("success", false);
                result.put("error", e.getMessage());

            }
        }
        //return result 
        return result;
    }

    /**
     * Fetch events
     *
     * @param userId
     * @return posts
     */
    public Map<String, Object> read(int userId) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();
        //verify if admin
        boolean verification = verifyUser(userId);

        if (verification == false) {
            String message = "User not admin";
            result.put("success", false);
            result.put("message", message);
        } else {
            try {
                String sql = "SELECT * FROM app.events";
                PreparedStatement psmt = connect().prepareStatement(sql);
                java.sql.ResultSet postResult = psmt.executeQuery();

                if (!postResult.equals(0)) {
                    while (postResult.next()) {
                        //get data from result
                        int id = postResult.getInt("event_id");
                        String title = postResult.getString("title");
                        String content = postResult.getString("content");
                        String created_by = postResult.getString("created_by");

                        result.put("success", true);
                        result.put("id", id);
                        result.put("title", title);
                        result.put("content", content);
                        result.put("created_by", created_by);
                    }
                } else {
                    String message = "No post found!";
                    result.put("success", true);
                    result.put("message", message);
                }
            } catch (SQLException e) {
                result.put("success", false);
                result.put("error", e.getMessage());
            }
        }
        //return result 
        return result;
    }

    /**
     * Update event
     *
     * @param title
     * @param content
     * @param eventId
     * @param userId
     * @return message
     */
    public Map<String, Object> update(int eventId, String title, String content, int userId) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        //verify if admin
        boolean verification = verifyUser(userId);

        if (verification == false) {
            String message = "User not admin";
            result.put("success", false);
            result.put("message", message);
        } else {
            try {
                String sql = "UPDATE app.events SET title = ?, content = ? WHERE event_id = ?";
                PreparedStatement psmt = connect().prepareStatement(sql);

                // Set the values for the placeholders in the query
                psmt.setString(1, title);
                psmt.setString(2, content);
                psmt.setInt(3, eventId);

                int rowsUpdated = psmt.executeUpdate();

                if (rowsUpdated > 0) {
                    result.put("success", true);
                    result.put("message", "Event updated successfuly");
                } else {
                    result.put("success", false);
                    result.put("message", "Event update failed!");
                }

            } catch (SQLException e) {
                result.put("success", false);
                result.put("error", e.getMessage());
            }
        }
        //return result 
        return result;
    }

    /**
     * Delete post method
     *
     * @param eventId
     * @param userId
     * @return success status and message
     *
     */
    public Map<String, Object> delete(int eventId, int userId) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        //verify if admin
        boolean verification = verifyUser(userId);

        if (verification == false) {
            String message = "User not admin";
            result.put("success", false);
            result.put("message", message);
        } else {
            try {
                String sql = "DELETE FROM app.events WHERE event_id = ?";
                PreparedStatement pstmt = connect().prepareStatement(sql);
                pstmt.setInt(1, eventId);

                int numRowsDeleted = pstmt.executeUpdate();
                if (numRowsDeleted == 0) {
                    String message = "No post found with ID " + eventId;
                    result.put("success", true);
                    result.put("message", message);
                } else {
                    String message = numRowsDeleted + " post(s) deleted successfully";
                    result.put("success", true);
                    result.put("message", message);
                }
            } catch (SQLException e) {
                result.put("success", false);
                result.put("error", e.getMessage());
            }
        }
        //return result 
        return result;
    }

    private static boolean verifyUser(int userId) {
        final int ADMIN = 1;
        boolean result = true;
        if (userId != ADMIN) {
            result = false;
        }
        //return result 
        return result;
    }
}
