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
        Book book = given().accept(ContentType.JSON).get(BASE_URL+"books/3").jsonPath().getObject("book", Book.class);
        
        assertEquals("book id should be 3", "3", book.getId().toString());
        System.out.println(book.getDescription());
        System.out.println(book.getAuthor());
        
    }
    @Test
    public void testGetBook(){
        Response response = new BookOperations().getBook(3);
        System.out.println("Status code: " + response.statusCode());
        
        assertEquals("status code should be 200",  200, response.statusCode());
        
        String title = response.body().jsonPath().getString("book.title");
        System.out.println("Title: " + title);
    }
    
    @Test
    public void testCreateNewBook(){
        Book book = new Book();
        book.setDescription("Hello");
        book.setTitle("happy");
        book.setIsbn("123123");
        book.setNbOfPage(2356);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(BASE_URL+"books").prettyPeek();
        assertEquals("status code should be 201",  201, response.statusCode());
        
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("status code should be 204",  204, deleteResponse.statusCode());       
          
    }
    
    @Test
    public void testCreateNewBookWithAuthor(){
        Book Authorfetch = given().accept(ContentType.JSON).get(BASE_URL+"books/9").jsonPath().getObject("book", Book.class);
        Book book = new Book();
        book.setDescription("Hello");
        book.setTitle("happy");
        book.setIsbn("123123");
        book.setNbOfPage(2356);
        book.setAuthor(Authorfetch.getAuthor());
        
        SingleBook singleBook = new SingleBook(book);
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(BASE_URL+"books").prettyPeek();
        System.out.println("Status code: " + response.getStatusCode());  
        
        //Response deleteResponse = new BookOperations().deleteLastBook();
        //assertEquals("status code should be 204",  204, deleteResponse.statusCode());      
    }
    
   // @Test
    public void testAddNewBookWithAuthor(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        
        Response postAuthorResponse = authorOperations.createRandomAuthorWithRandomId();
        // assertEquals("status code should be 201",  201, postAuthorResponse.statusCode());
        String authorTitle = "Margaret Atwood"; //from(authorOperations.getLatestJsonString()).getString("author.name");
        Integer authorId = 22;//from(authorOperations.getLatestJsonString()).getInt("author.id");
        
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
    public void testFetchInvalidBookReturns404(){
        Response response = new BookOperations().getBook(234987);
        System.out.println("Status code: " + response.statusCode());
        assertEquals("status code should be 404",  404, response.statusCode());
        
    }
    
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
    @Test
    public void testDeleteBook(){
        Response postResponse = new BookOperations().createRandomBook();
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        
        Response getResponse = new BookOperations().getAllBooks();
        int fetchedId = getResponse.jsonPath().getInt("books.book[-1].id");
        
        Response deleteResponse = new BookOperations().deleteBook(fetchedId);
        assertEquals("delete method should return 204", 204, deleteResponse.getStatusCode());
        
        Response getDeletedBookResponse = new BookOperations().getBook(fetchedId);
        assertEquals("fetching deleted book should return 404",  404, getDeletedBookResponse.statusCode());
        
    }
    
}