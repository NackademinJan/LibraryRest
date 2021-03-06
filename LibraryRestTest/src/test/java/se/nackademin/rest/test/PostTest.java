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
public class PostTest {

    
    public PostTest() {
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
    public void testPostBook(){
        Book book = new Book();
        book.setDescription(GlobVar.secondMockBookDescription);
        book.setTitle(GlobVar.secondMockBookTitle);
        book.setIsbn(GlobVar.secondMockBookIsbn);
        book.setNbOfPage(GlobVar.secondMockBookNbOfPage);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(GlobVar.BASE_URL+"books");
        assertEquals("The status code should be: 201",  201, response.statusCode());
        assertEquals("response body should be blank", "", response.body().print());
        
        Book verifyBook = new BookOperations().fetchLastBook();
        assertEquals("The books description should be: " + GlobVar.secondMockBookDescription,  book.getDescription(), verifyBook.getDescription());       
        assertEquals("The books title should be: " + GlobVar.secondMockBookTitle,  book.getTitle(), verifyBook.getTitle());       
        assertEquals("The books isbn should be: " + GlobVar.secondMockBookIsbn,  book.getIsbn(), verifyBook.getIsbn());       
        assertEquals("The books page count should be: " + GlobVar.secondMockBookNbOfPage,  book.getNbOfPage(), verifyBook.getNbOfPage());       
        
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());
        assertEquals("response body should be blank", "", deleteResponse.body().print());
    }
    
    public void testInvalidPostBook(){
        Book book = new Book();
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(GlobVar.BASE_URL+"books");
        assertEquals("The status code should be: 400",  400, response.statusCode());
        assertEquals("response body should be blank", "", response.body().print());
        
    }
    
    @Test
    public void testPostBookwithSpecificNewID(){
        Response getResponse = new BookOperations().getAllBooks();
        int newId = getResponse.jsonPath().getInt("books.book[-1].id") + 1;
        Book book = new Book();
        book.setDescription(GlobVar.secondMockBookDescription);
        book.setTitle(GlobVar.secondMockBookTitle);
        book.setIsbn(GlobVar.secondMockBookIsbn);
        book.setNbOfPage(GlobVar.secondMockBookNbOfPage);
        book.setId(newId);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(GlobVar.BASE_URL+"books");
        assertEquals("The status code should be: 201",  201, response.statusCode());
        assertEquals("response body should be blank", "", response.body().print());
        
        Book verifyBook = new BookOperations().fetchLastBook();
        assertEquals("The books description should be: " + GlobVar.secondMockBookDescription,  book.getDescription(), verifyBook.getDescription());       
        assertEquals("The books title should be: " + GlobVar.secondMockBookTitle,  book.getTitle(), verifyBook.getTitle());       
        assertEquals("The books isbn should be: " + GlobVar.secondMockBookIsbn,  book.getIsbn(), verifyBook.getIsbn());       
        assertEquals("The books page count should be: " + GlobVar.secondMockBookNbOfPage,  book.getNbOfPage(), verifyBook.getNbOfPage());       
        assertEquals("The books page count should be: " + newId,  book.getId(), verifyBook.getId()); 
        
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());
    }
    
    @Test
    public void testInvalidPostBookwithExistingID(){
        Response getResponse = new BookOperations().getAllBooks();
        int lastId = getResponse.jsonPath().getInt("books.book[-1].id");
        Book book = new Book();
        book.setDescription(GlobVar.secondMockBookDescription);
        book.setTitle(GlobVar.secondMockBookTitle);
        book.setIsbn(GlobVar.secondMockBookIsbn);
        book.setNbOfPage(GlobVar.secondMockBookNbOfPage);
        book.setId(lastId);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(GlobVar.BASE_URL+"books");
        assertEquals("The status code should be: 400",  400, response.statusCode());
    }
    
    @Test 
    public void testInvalidPostBookWithNonExistingAuthor(){
        Response authorResponse = new AuthorOperations().getAuthor(GlobVar.mockAuthorId); 
        String authorName = "Bad Author";
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id") +1;

        Response postResponse = new BookOperations().createBookWithAuthor(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle, authorName, authorId);
        assertEquals("The status code should be: 400",  400, postResponse.statusCode());

    }
    
    @Test 
    public void testInvalidPostBookWithoutAuthorId(){
        Response authorResponse = new AuthorOperations().getAuthor(GlobVar.mockAuthorId); 
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Response postResponse = new BookOperations().createBookWithAuthorNoAuthorId(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle, authorName);
        assertEquals("The status code should be: 400",  400, postResponse.statusCode());        
    }
    
    /*@Test this method will not work currently due to the api not supporting posting a new book with an author wether that author exists previously or not
    public void testPostBookWithAuthor(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        
        Response authorResponse = authorOperations.getAuthor(GlobVar.mockAuthorId); //from(authorOperations.getLatestJsonString()).getString("author.name");
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id");
        
        Response postResponse = bookOperations.createBookWithAuthor(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle, authorName, authorId);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().prettyPrint());
        
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
        
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());
    }
    */
    
    /*@Test //This test doesnt work because im not sure how to work with a book class author-object
    public void testPostBookWithAuthorUsingBookClass(){
        Response getResponse = new BookOperations().getAllBooks();
        int lastId = getResponse.jsonPath().getInt("books.book[-1].id");
        Response authorResponse = new AuthorOperations().getAuthor(lastId);
        
        Book book = new Book();
        book.setDescription(GlobVar.secondMockBookDescription);
        book.setTitle(GlobVar.secondMockBookTitle);
        book.setIsbn(GlobVar.secondMockBookIsbn);
        book.setNbOfPage(GlobVar.secondMockBookNbOfPage);
        book.setAuthor(authorResponse);
        SingleBook singleBook = new SingleBook(book);
        
        Response response = given().contentType(ContentType.JSON).body(singleBook).log().all().post(GlobVar.BASE_URL+"books").prettyPeek();
        assertEquals("The status code should be: 201",  201, response.statusCode());
        assertEquals("response body should be blank", "", response.body().prettyPrint());
    
        Book verifyBook = new BookOperations().fetchLastBook();
         assertEquals("The books description should be: " + GlobVar.secondMockBookDescription,  book.getDescription(), verifyBook.getDescription());       
        assertEquals("The books title should be: " + GlobVar.secondMockBookTitle,  book.getTitle(), verifyBook.getTitle());       
        assertEquals("The books isbn should be: " + GlobVar.secondMockBookIsbn,  book.getIsbn(), verifyBook.getIsbn());       
        assertEquals("The books page count should be: " + GlobVar.secondMockBookNbOfPage,  book.getNbOfPage(), verifyBook.getNbOfPage());       
        // this one needs work! assertEquals("The books author should be: " + book.getAuthor(),  book.getAuthor(), verifyBook.getAuthor());       
    
        assertEquals("The books description should be: " + GlobVar.secondMockBookDescription,  book.getDescription(), verifyBook.getDescription());       
        assertEquals("The books title should be: " + GlobVar.secondMockBookTitle,  book.getTitle(), verifyBook.getTitle());       
        assertEquals("The books isbn should be: " + GlobVar.secondMockBookIsbn,  book.getIsbn(), verifyBook.getIsbn());       
        assertEquals("The books page count should be: " + GlobVar.secondMockBookNbOfPage,  book.getNbOfPage(), verifyBook.getNbOfPage());       
    
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());
    }
    */
    
    
    @Test
    public void testForbiddenPostToBooksId(){
        String resourceName = "books/"+GlobVar.mockBookId;
        Response response = given().accept(ContentType.JSON).post(GlobVar.BASE_URL+resourceName);
        assertEquals("The status code should be: 405, method not allowed",  405, response.statusCode());        
    }
    
    @Test
    public void testForbiddenPostToBooksByAuthorAuthorId(){
        String resourceName = "books/byauthor"+GlobVar.mockAuthorId;
        Response response = given().accept(ContentType.JSON).post(GlobVar.BASE_URL+resourceName);
        assertEquals("The status code should be: 405, method not allowed",  405, response.statusCode());        
    }
    
    
    @Test
    public void testPostAuthorToBook(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        //this part makes a new book and verifies that it was created)
        Response postResponse = bookOperations.createBookWithInput(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().print());
        
        String expectedTitle = from(bookOperations.getLatestJsonString()).getString("book.title");
        String expectedDescription = from(bookOperations.getLatestJsonString()).getString("book.description");
        String expectedIsbn = from(bookOperations.getLatestJsonString()).getString("book.isbn");
        
        Response getResponse = bookOperations.getAllBooks();
        String fetchedTitle = getResponse.jsonPath().getString("books.book[-1].title");
        String fetchedDescription = getResponse.jsonPath().getString("books.book[-1].description");
        String fetchedIsbn = getResponse.jsonPath().getString("books.book[-1].isbn");
        
        assertEquals(expectedTitle, fetchedTitle);
        assertEquals(expectedDescription, fetchedDescription);
        assertEquals(expectedIsbn, fetchedIsbn);      
        
        //this part adds an existing author to the new book and verifies that he has been added to said book
        Response authorResponse = authorOperations.getAuthor(GlobVar.mockAuthorId); 
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id");
        Integer newBookId = new BookOperations().GetLastBook().jsonPath().getInt("book.id");
        
        Response postAuthorToBookResponse = new BookOperations().addAuthorToBook(authorName, authorId, newBookId);
        assertEquals("The status code should be: 200",  200, postAuthorToBookResponse.statusCode());
        assertEquals("response body should be blank", "", postAuthorToBookResponse.body().print());  
        
        Response getAuthorResponse = bookOperations.getBookById(newBookId);
        String verifyAuthorName = getAuthorResponse.jsonPath().getString("book.author.name");
        Integer verifyAuthorId = getAuthorResponse.jsonPath().getInt("book.author.id");
        
        assertEquals(authorName, verifyAuthorName);
        assertEquals(authorId, verifyAuthorId);
        
        // this bit cleans up the mess!
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());  
    }
    
    @Test
    public void testInvalidPostAuthorToBookTwice(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        //this part makes a new book and verifies that it was created)
        Response postResponse = bookOperations.createBookWithInput(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().print());
        
        String expectedTitle = from(bookOperations.getLatestJsonString()).getString("book.title");
        String expectedDescription = from(bookOperations.getLatestJsonString()).getString("book.description");
        String expectedIsbn = from(bookOperations.getLatestJsonString()).getString("book.isbn");
        
        Response getResponse = bookOperations.getAllBooks();
        String fetchedTitle = getResponse.jsonPath().getString("books.book[-1].title");
        String fetchedDescription = getResponse.jsonPath().getString("books.book[-1].description");
        String fetchedIsbn = getResponse.jsonPath().getString("books.book[-1].isbn");
        
        assertEquals(expectedTitle, fetchedTitle);
        assertEquals(expectedDescription, fetchedDescription);
        assertEquals(expectedIsbn, fetchedIsbn);      
        
        //this part adds an existing author to the new book and verifies that he has been added to said book
        Response authorResponse = authorOperations.getAuthor(GlobVar.mockAuthorId); 
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id");
        Integer newBookId = new BookOperations().GetLastBook().jsonPath().getInt("book.id");
        
        Response postAuthorToBookResponse = new BookOperations().addAuthorToBook(authorName, authorId, newBookId);
        assertEquals("The status code should be: 200",  200, postAuthorToBookResponse.statusCode());
        assertEquals("response body should be blank", "", postAuthorToBookResponse.body().print());  
        
        Response getAuthorResponse = bookOperations.getBookById(newBookId);
        String verifyAuthorName = getAuthorResponse.jsonPath().getString("book.author.name");
        Integer verifyAuthorId = getAuthorResponse.jsonPath().getInt("book.author.id");
        
        assertEquals(authorName, verifyAuthorName);
        assertEquals(authorId, verifyAuthorId);
        //this part tries to post the same author to the book again which should not work
        Response postAuthorToBookTwiceResponse = new BookOperations().addAuthorToBook(authorName, authorId, newBookId);
        assertEquals("The status code should be: 400",  400, postAuthorToBookTwiceResponse.statusCode());
        
        // this bit cleans up the mess!
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());  
    }
    
    @Test
    public void testInvalidPostAuthorToBookWithNoAuthorId(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();
        //this part makes a new book and verifies that it was created)
        Response postResponse = bookOperations.createBookWithInput(GlobVar.secondMockBookDescription, GlobVar.secondMockBookIsbn, GlobVar.secondMockBookNbOfPage, GlobVar.secondMockBookTitle);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().print());
        
        String expectedTitle = from(bookOperations.getLatestJsonString()).getString("book.title");
        String expectedDescription = from(bookOperations.getLatestJsonString()).getString("book.description");
        String expectedIsbn = from(bookOperations.getLatestJsonString()).getString("book.isbn");
        
        Response getResponse = bookOperations.getAllBooks();
        String fetchedTitle = getResponse.jsonPath().getString("books.book[-1].title");
        String fetchedDescription = getResponse.jsonPath().getString("books.book[-1].description");
        String fetchedIsbn = getResponse.jsonPath().getString("books.book[-1].isbn");
        
        assertEquals(expectedTitle, fetchedTitle);
        assertEquals(expectedDescription, fetchedDescription);
        assertEquals(expectedIsbn, fetchedIsbn);      
        
        //this part tries adding an author without his authorId to the new book which should not work
        Response authorResponse = authorOperations.getAuthor(GlobVar.mockAuthorId); 
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id");
        Integer newBookId = new BookOperations().GetLastBook().jsonPath().getInt("book.id");
        
        Response postAuthorToBookResponse = new BookOperations().addAuthorToBookWithoutAuthorId(authorName, newBookId);
        assertEquals("The status code should be: 400",  400, postAuthorToBookResponse.statusCode());
        
        // this bit cleans up the mess!
        Response deleteResponse = new BookOperations().deleteLastBook();
        assertEquals("The status code should be: 204",  204, deleteResponse.statusCode());  
    }
    
    @Test
    public void testInvalidPostAuthorToBookWithNonxistingBook(){
        BookOperations bookOperations = new BookOperations();
        AuthorOperations authorOperations = new AuthorOperations();    
        
        //this part adds an existing author to a nonexisting book which should not work
        Response authorResponse = authorOperations.getAuthor(GlobVar.mockAuthorId); 
        String authorName = authorResponse.body().jsonPath().getString("author.name");
        Integer authorId = authorResponse.body().jsonPath().getInt("author.id");
        Integer newBookId = new BookOperations().GetLastBook().jsonPath().getInt("book.id") + 1;
        
        Response postAuthorToBookResponse = new BookOperations().addAuthorToBook(authorName, authorId, newBookId);
        assertEquals("The status code should be: 404",  404, postAuthorToBookResponse.statusCode());
    }
    
    
    @Test
    public void testPostAuthor(){
        AuthorOperations authorOperations = new AuthorOperations();
        
        Response postResponse = authorOperations.createAuthor("Another MockAuthor");
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().print());
        
        String expectedName = from(authorOperations.getLatestJsonString()).getString("author.name");
        
        Response getResponse = authorOperations.getAllAuthors();
        String fetchedName = getResponse.body().jsonPath().getString("authors.author[-1].name");
        assertEquals(expectedName, fetchedName);
        
        Response delResponse = authorOperations.deleteLastAuthor();
        assertEquals("status code should be 204",  204, delResponse.statusCode());
        assertEquals("response body should be blank", "", delResponse.body().print());
    }
    
    @Test
    public void testPostAuthorWithSpecificNewAuthorId(){
        AuthorOperations authorOperations = new AuthorOperations();
        
        Response postResponse = authorOperations.createAuthorWithId("Another MockAuthor", GlobVar.mockAuthorId + 1);
        assertEquals("status code should be 201",  201, postResponse.statusCode());
        assertEquals("response body should be blank", "", postResponse.body().print());
        
        String expectedName = from(authorOperations.getLatestJsonString()).getString("author.name");
        Integer expectedId = from(authorOperations.getLatestJsonString()).getInt("author.id");
        
        Response getResponse = authorOperations.getAllAuthors();
        String fetchedAuthorName = getResponse.body().jsonPath().getString("authors.author[-1].name");
        Integer fetchedAuthorId = getResponse.jsonPath().getInt("authors.author[-1].id");
        
        assertEquals(expectedName, fetchedAuthorName);
        assertEquals(expectedId, fetchedAuthorId);
        
        Response delResponse = authorOperations.deleteLastAuthor();
        assertEquals("status code should be 204",  204, delResponse.statusCode());
        assertEquals("response body should be blank", "", delResponse.body().print());
    }
    
    @Test
    public void testPostAuthorWithExistingAuthorId(){
        Response postResponse = new AuthorOperations().createAuthorWithId("Another MockAuthor", GlobVar.mockAuthorId);
        assertEquals("status code should be 400",  400, postResponse.statusCode());
       
    }
    
    
    @Test
    public void testForbiddenPostToAuthorsId(){
        String resourceName = "books/"+GlobVar.mockAuthorId;
        Response response = given().accept(ContentType.JSON).post(GlobVar.BASE_URL+resourceName);
        assertEquals("The status code should be: 405, method not allowed",  405, response.statusCode());        
    }
    
    
}
