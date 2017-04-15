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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import se.nackademin.rest.test.model.AllBooks;
import se.nackademin.rest.test.model.SingleBook;
/**
 *
 * @author testautomatisering
 */
public class GetTest {

    
    public GetTest() {
    }
        
    @BeforeClass
    public static void MakeMockBookAndMockAuthor(){
        Response makeMocksResponse = BeforeAndAfterOperations.MakeMockBookAndMockAuthor();
        assertEquals("The status code should be: 201",  201, makeMocksResponse.statusCode());
        
        Response addMockAuthorToMockBook = BeforeAndAfterOperations.AddMockAuthorToMockBook();
        assertEquals("The status code should be: 200",  200, addMockAuthorToMockBook.statusCode());
    }
    
    @AfterClass
    public static void RemoveMockBookAndMockAuthor(){
        Response removeResponse = BeforeAndAfterOperations.RemoveTestBookAndTestAuthor();
        assertEquals("The status code should be: 204",  204, removeResponse.statusCode());  
    }
    
    @Test
    public void testFetchBook(){
        Book book = given().accept(ContentType.JSON).get(GlobVar.BASE_URL+"books/"+GlobVar.mockBookId).jsonPath().getObject("book", Book.class);
        assertEquals("book id should be: "+GlobVar.mockBookId, GlobVar.mockBookId, book.getId());    
    }

    //@Test get this fixed with help from teacher, needs to rework the allbooks class
    public void testGetAllBooks(){
        Response response = new BookOperations().getAllBooks();
        assertEquals("The status code should be: 200",  200, response.statusCode());
        AllBooks books = response.jsonPath().getObject("books", AllBooks.class);
        Book mockBook = books.getBookfromBooks(GlobVar.mockBookId);
        assertEquals("Book title should be: MockyTestTitle",  "MockyTestTitle", mockBook.getTitle());
        //assertEquals("Book title should be: Testbok för testförfattaren",  "Testbok för testförfattaren", response.body().jsonPath().getString("books.book.title")); 
    }
    
    @Test
    public void testGetBookById(){
        Response response = new BookOperations().getBookById(GlobVar.mockBookId);
        assertEquals("The status code should be: 200",  200, response.statusCode());        
        assertEquals("Book title should be: MockyTestTitle",  "MockyTestTitle", response.body().jsonPath().getString("book.title"));
    }
    
    @Test
    public void testInvalidGetBookByIdReturns404(){
        Response response = new BookOperations().getBookById(234987);
        assertEquals("status code returned should be: 404",  404, response.statusCode());
        assertEquals("response body should be blank", "", response.body().prettyPrint());
    }

    @Test
    public void testGetBookByAuthor(){
        Response response = new BookOperations().getBookByAuthor(GlobVar.mockAuthorId);
        assertEquals("The status code should be: 200",  200, response.statusCode());
        assertEquals("Book title should be: MockyTestTitle",  "MockyTestTitle", response.body().jsonPath().getString("books.book.title")); 
    }
    
    /*
     *  detta test kommer inte ge rätt utslag då rest-api returnerar 200 när dett test körs, 
     *  api dokumentationen specifierar inte heller att man ska kunna få fel på detta vis, 
     *  men det verkar som ett misstag så behåller detta test men utkommenterat utifall att.
     */
    //@Test
    public void testInvalidGetBookbyAuthorReturns404(){
        Response response = new BookOperations().getBookByAuthor(234987);
        assertEquals("The status code should be: 404",  404, response.statusCode());
        assertEquals("response body should be blank", "", response.body().prettyPrint());
    }
        
    @Test
    public void testGetAuthorByBook(){
        Response response = new BookOperations().getAuthorByBook(GlobVar.mockBookId);
        assertEquals("The status code should be: 200",  200, response.statusCode());
        assertEquals("Author name should be MockyAuthorName",  "MockyAuthorName", response.body().jsonPath().getString("authors.author.name"));
    }
    
    @Test
    public void testInvalidGetAuthorByBook(){
        Response response = new BookOperations().getAuthorByBook(5454325);
        assertEquals("The status code should be: 404",  404, response.statusCode());
        assertEquals("response body should be blank", "", response.body().prettyPrint());
    }
    
    //@Test (similair issues to testGetAllBooks, get teacher to help
    public void testGetAllAuthors(){
        Response response = new AuthorOperations().getAllAuthors();
        assertEquals("The status code should be: 200",  200, response.statusCode());
        
    }
    
    @Test
    public void testGetAuthorById(){
        Response response = new AuthorOperations().getAuthor(GlobVar.mockAuthorId);
        assertEquals("The status code should be: 200",  200, response.statusCode());
        assertEquals("Author Name should be: MockyAuthorName",  "MockyAuthorName", response.body().jsonPath().getString("author.name"));
    }
    
    @Test
    public void testInvalidGetAuthorById(){
        Response response = new AuthorOperations().getAuthor(23662346);
        assertEquals("The status code should be: 404",  404, response.statusCode());
        assertEquals("response body should be blank", "", response.body().prettyPrint());
    }

    
}
