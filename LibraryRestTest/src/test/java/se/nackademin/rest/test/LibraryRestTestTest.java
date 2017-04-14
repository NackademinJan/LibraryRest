/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nackademin.rest.test;

import org.junit.Test;
import static com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import java.util.UUID;
import static org.junit.Assert.*;
import static com.jayway.restassured.path.json.JsonPath.*;
import se.nackademin.rest.test.model.Book;
import static com.jayway.restassured.RestAssured.*;
import se.nackademin.rest.test.model.SingleBook;
/**
 *
 * @author testautomatisering
 */
public class LibraryRestTestTest {
    private static final String BASE_URL = "http://localhost:8080/librarytest/rest/";
    
    public LibraryRestTestTest() {
    }
    
    @Test
    public void testFetchBook(){
        Book book = given().accept(ContentType.JSON).get(BASE_URL+"books/148").jsonPath().getObject("book", Book.class);
        
        assertEquals("book id should be 3", "148", book.getId().toString());
        System.out.println(book.getDescription());
        System.out.println(book.getTitle());
        System.out.println(book.getAuthor());
        
    }

    @Test
    public void testGetAllBooks(){
        Response response = new BookOperations().getAllBooks();
        System.out.println("Status code: " + response.statusCode());
        
        assertEquals("The status code should be: 200",  200, response.statusCode());
        
    }
    
    @Test
    public void testGetBookById(){
        Response response = new BookOperations().getBookById(148);
        System.out.println("Status code: " + response.statusCode());
        assertEquals("The status code should be: 200",  200, response.statusCode());
        
        assertEquals("Book title should be: Testbok för testförfattaren",  "Testbok för testförfattaren", response.body().jsonPath().getString("book.title"));
    }
    
    @Test
    public void testInvalidGetBookByIdReturns404(){
        Response response = new BookOperations().getBookById(234987);
        System.out.println("Status code: " + response.statusCode());
        assertEquals("status code returned should be: 404",  404, response.statusCode());
        
    }

    @Test
    public void testGetBookByAuthor(){
        Response response = new BookOperations().getBookByAuthor(42);
        
        assertEquals("The status code should be: 200",  200, response.statusCode());
        
        assertEquals("Book title should be: Testbok för testförfattaren",  "Testbok för testförfattaren", response.body().jsonPath().getString("books.book.title")); 
    }
    
    /*
     *  detta test kommer inte ge rätt utslag då rest-api returnerar 200 när dett test körs, 
     *  api dokumentationen specifierar inte heller att man ska kunna få fel på detta vis, 
     *  men det verkar som ett misstag så behåller detta test utkommenterat utifall att.
     */
    //@Test
    public void testInvalidGetBookbyAuthorReturns404(){
        Response response = new BookOperations().getBookByAuthor(234987);
        assertEquals("The status code should be: 404",  404, response.statusCode());
        
    }
        
    @Test
    public void testGetAuthorByBook(){
        Response response = new BookOperations().getAuthorByBook(148);
        
        assertEquals("The status code should be: 200",  200, response.statusCode());
        
        assertEquals("Author name should be Testförfattare RaderaEj",  "Testförfattare RaderaEj", response.body().jsonPath().getString("authors.author.name"));
        String authorName = response.body().jsonPath().getString("authors.author.name");
        System.out.println("The author name: " + authorName);
    }
    
    
    @Test
    public void testCreateNewBook(){
        Book book = new Book();
        book.setDescription("Hello");
        book.setTitle("Happy");
        book.setIsbn("123123");
        book.setNbOfPage(2356);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(BASE_URL+"books").prettyPeek();
        assertEquals("The status code should be: 201",  201, response.statusCode());
        
        Book verifyBook = new BookOperations().fetchLastBook();
        assertEquals("The books description should be: Hello",  book.getDescription(), verifyBook.getDescription());       
        assertEquals("The books title should be: Happy",  book.getTitle(), verifyBook.getTitle());       
        assertEquals("The books isbn should be: 123123",  book.getIsbn(), verifyBook.getIsbn());       
        assertEquals("The books page count should be: 2356",  book.getNbOfPage(), verifyBook.getNbOfPage());       
        
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());
    }
       
    @Test
    public void testDeleteBook(){
        Response postResponse = new BookOperations().createRandomBook();
        assertEquals("The status code should be: 201",  201, postResponse.statusCode());
        
        Response getResponse = new BookOperations().getAllBooks();
        int fetchedId = getResponse.jsonPath().getInt("books.book[-1].id");
        
        Response deleteResponse = new BookOperations().deleteBook(fetchedId);
        assertEquals("delete method should return status code: 204", 204, deleteResponse.getStatusCode());
        
        Response getDeletedBookResponse = new BookOperations().getBookById(fetchedId);
        assertEquals("fetching deleted book should return status code: 404",  404, getDeletedBookResponse.statusCode());
        
    }
    
    @Test
    public void testmakeauthor(){
        Response response = new AuthorOperations().createAuthor("Andratestförfattare SkaRaderasAutomatiskt", 44);
        assertEquals("The status code should be: 201",  201, response.statusCode());
        
        Response deleteResponse = new AuthorOperations().deleteLastAuthor();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());  
    }   
    
    /*
    //@Test
    public void testCreateNewBookWithAuthor(){
        Book Authorfetch = given().accept(ContentType.JSON).get(BASE_URL+"books/1").jsonPath().getObject("book", Book.class);
        Book book = new Book();
        book.setDescription("Hello");
        book.setTitle("happy");
        book.setIsbn("123123");
        book.setNbOfPage(2356);
        book.setAuthor(Authorfetch.getAuthor());
        
        SingleBook singleBook = new SingleBook(book);
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(BASE_URL+"books").prettyPeek();
        assertEquals("status code should be 201",  201, response.statusCode());
        System.out.println("Status code: " + response.getStatusCode());  
        
        //Response deleteResponse = new BookOperations().deleteLastBook();
        //assertEquals("status code should be 204",  204, deleteResponse.statusCode());      
    }
    
    //@Test
    public void testAddNewBookWithAuthor(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        
        //Response postAuthorResponse = authorOperations.createRandomAuthorWithRandomId();
        // assertEquals("status code should be 201",  201, postAuthorResponse.statusCode());
        String authorTitle = "Margaret Atwood"; //from(authorOperations.getLatestJsonString()).getString("author.name");
        Integer authorId = 1.0;//from(authorOperations.getLatestJsonString()).getInt("author.id");
        
        Response postResponse = bookOperations.createRandomBookWithAuthor(authorTitle, authorId);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        
    
        String expectedTitle = from(bookOperations.getLatestJsonString()).getString("book.title");
        String expectedDescription = from(bookOperations.getLatestJsonString()).getString("book.description");
        String expectedIsbn = from(bookOperations.getLatestJsonString()).getString("book.isbn");
        
        Response getResponse = new BookOperations().getAllBooks();
        String fetchedTitle = getResponse.jsonPath().getString("books.book[-1].title");
        String fetchedDescription = getResponse.jsonPath().getString("books.book[-1].description");
        String fetchedIsbn = getResponse.jsonPath().getString("books.book[-1].isbn");
        
    
        assertEquals(expectedTitle, fetchedTitle);
        assertEquals(expectedDescription, fetchedDescription);
        assertEquals(expectedIsbn, fetchedIsbn);
        
    }
    /*
    
    @Test
    public void testAddNewBook(){
        BookOperations bookOperations = new BookOperations();
        Response postResponse = bookOperations.createRandomBook();
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        
    
        String expectedTitle = from(bookOperations.getLatestJsonString()).getString("book.title");
        String expectedDescription = from(bookOperations.getLatestJsonString()).getString("book.description");
        String expectedIsbn = from(bookOperations.getLatestJsonString()).getString("book.isbn");
        
        Response getResponse = new BookOperations().getAllBooks();
        String fetchedTitle = getResponse.jsonPath().getString("books.book[-1].title");
        String fetchedDescription = getResponse.jsonPath().getString("books.book[-1].description");
        String fetchedIsbn = getResponse.jsonPath().getString("books.book[-1].isbn");
        
    
        assertEquals(expectedTitle, fetchedTitle);
        assertEquals(expectedDescription, fetchedDescription);
        assertEquals(expectedIsbn, fetchedIsbn);
        
    }
    */
    /*       //String title = response.body().jsonPath().getString("books.book.title");
        String title = response.body().jsonPath().getString("books.book.title");
        String authorName = response.body().jsonPath().getString("books.book.author.name");
        System.out.println("The author name: " + authorName);
    */
    
}
