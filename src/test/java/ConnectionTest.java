import junit.framework.TestCase;
import org.junit.Test;
import org.teo.net.PostgressConnection;

public class ConnectionTest {

    @Test
    public void testConnection(){
        final String url = "jdbc:postgresql://localhost:5432/teodb";
        final String user = "postgres";
        final String password = "qwe123";

        try {
            PostgressConnection connection = new PostgressConnection(url, user, password);
        }
        catch(Exception e){
            TestCase.fail("Connection error: " + e.getMessage());
        }
    }
}
