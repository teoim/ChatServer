import junit.framework.TestCase;
import org.junit.Test;
import org.mtr.net.PostgresConnection;

public class ConnectionTest {

    PostgresConnection connection;
    final String url = "jdbc:postgresql://localhost:5432/chatdb";
    final String dbUser = "postgres";
    final String dbUserPassword = "qwe123";

    private String expectedResponse = "", result = "";


    @Test
    public void testConnection(){

        try {
            connection = new PostgresConnection(url, dbUser, dbUserPassword);
        }
        catch(Exception e){
            TestCase.fail("Connection error: " + e.getMessage());
        }
    }

    @Test
    public void testConnectionInvalidPassword(){
        try {
            connection = new PostgresConnection(url, dbUser, dbUserPassword+",");
        }
        catch(Exception e){
            //TestCase.fail("Connection error: " + e.getMessage());
            TestCase.assertEquals( "FATAL: password authentication failed for user \""+dbUser+"\"", e.getMessage());
        }
    }

    @Test
    public void testLogin(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = "200\tJohnath\tJelkes";
        result = connection.login("jjelkes0@elpais.com", "5cEnsm0pPdik");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginEmptyParameterEmail(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login("", "5cEnsm0pPdik");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginBlankParameterEmail(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login("    ", "5cEnsm0pPdik");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginNullParameterEmail(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login(null, "5cEnsm0pPdik");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginEmptyParameterPassword(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login("jjelkes0@elpais.com", "");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginBlankParameterPassword(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login("jjelkes0@elpais.com", "    ");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginNullParameterPassword(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login("jjelkes0@elpais.com", null);
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginBadPassword(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login( "jjelkes0@elpais.com", "5cEnsm0pPdik,");
        TestCase.assertEquals( expectedResponse, result);
    }

    @Test
    public void testLoginBadEmail(){
        expectedResponse = "";
        result = "";
        this.testConnection();  // setup DB connection

        expectedResponse = invalidCredentials();
        result = connection.login( "jjelkes0@elpais.com,", "5cEnsm0pPdik");
        TestCase.assertEquals(  expectedResponse, result);
    }

    private String invalidCredentials(){
        return "401 Unauthorized - Invalid email/password.";
    }
}
