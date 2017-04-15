/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nackademin.rest.test;

import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import se.nackademin.rest.test.model.Book;
import se.nackademin.rest.test.model.SingleBook;

/**
 *
 * @author testautomatisering
 */
public class BeforeAndAfterOperations {

    
   public static Response MakeMockBookAndMockAuthor(){
        Response postBookResponse = new BookOperations().createBookWithInput("MockyTestDescription", "MockyIsbn", 7357, "MockyTestTitle");
        //if(!( 201 == postBookResponse.getStatusCode() )) return postBookResponse;
        Response lastBookResponse = new BookOperations().getAllBooks();
        GlobVar.mockBookId = lastBookResponse.jsonPath().getInt("books.book[-1].id");
        
        Response postAuthorResponse = new AuthorOperations().createAuthor("MockyAuthorName");
        //if(!( 201 == postAuthorResponse.getStatusCode() )) return postAuthorResponse;
        Response authorResponse = new AuthorOperations().getAllAuthors();
        GlobVar.mockAuthorId = authorResponse.jsonPath().getInt("authors.author[-1].id");
        return postAuthorResponse;
        
    } 
   public static Response AddMockAuthorToMockBook(){
        Response authorOfBookResponse = new BookOperations().addAuthorToBook("MockyAuthorName", GlobVar.mockAuthorId, GlobVar.mockBookId);  
        return authorOfBookResponse; 
   }
   
   public static Response RemoveTestBookAndTestAuthor(){
        Response deleteBookResponse = new BookOperations().deleteBook(GlobVar.mockBookId);
        if(!( 204 == deleteBookResponse.getStatusCode() )) return deleteBookResponse;
        
        Response deleteAuthorResponse = new AuthorOperations().deleteAuthor(GlobVar.mockAuthorId);
        return deleteAuthorResponse;
    }
}
