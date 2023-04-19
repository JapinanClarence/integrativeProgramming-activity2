
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
public class Post {

    /**
     * Database connection
     *
     * @return
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
     * Create post method
     *
     * @param title
     * @param content
     * @param role
     * @return true or error
     */
    public Map<String, Object> create(String title, String content, int role) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        //insert to posts table or return error
        try {
            String sql = "INSERT INTO app.posts (title, content, created_by) VALUES (?,?,?)";
            PreparedStatement pstmt;
            pstmt = connect().prepareStatement(sql);

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, role);

            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                result.put("success", true);
            }
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());

        }
        //return result 
        return result;
    }

    /**
     * Fetch posts
     *
     * @return posts
     */
    public Map<String, Object> read() {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            String sql = "SELECT * FROM app.posts";
            PreparedStatement psmt = connect().prepareStatement(sql);
            java.sql.ResultSet postResult = psmt.executeQuery();

            if (!postResult.equals(0)) {
                while (postResult.next()) {
                    //get data from result
                    int id = postResult.getInt("post_id");
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
        //return result 
        return result;
    }

    /**
     * Update post
     *@param title
     * @param content
     * @param postId
     * @return message
     */
    public Map<String, Object> update(int postId, String title, String content) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            String sql = "UPDATE app.posts SET title = ?, content = ? WHERE post_id = ?";
            PreparedStatement psmt = connect().prepareStatement(sql);

            // Set the values for the placeholders in the query
            psmt.setString(1, title);
            psmt.setString(2, content);
            psmt.setInt(3, postId);

            int rowsUpdated = psmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                result.put("success", true);
                result.put("message", "Post updated successfuly");
            } else {
                result.put("success", false);
                result.put("message", "Post update failed!");
            }

        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        //return result 
        return result;
    }

    /**
     * Delete post method
     *
     * @param postId
     * @return success status and message
     *
     */
    public Map<String, Object> delete(int postId) {
        //instantiate result object
        Map<String, Object> result = new LinkedHashMap<>();

        try {
            String sql = "DELETE FROM app.posts WHERE post_id = ?";
            PreparedStatement pstmt = connect().prepareStatement(sql);
            pstmt.setInt(1, postId);

            int numRowsDeleted = pstmt.executeUpdate();
            if (numRowsDeleted == 0) {
                String message = "No post found with ID " + postId;
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
        //return result 
        return result;
    }
}
