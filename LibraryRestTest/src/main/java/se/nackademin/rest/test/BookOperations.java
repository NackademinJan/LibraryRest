/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nackademin.rest.test;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.given;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import java.util.Random;
import java.util.UUID;
import se.nackademin.rest.test.model.Book;
import se.nackademin.rest.test.model.SingleBook;

/**
 *
 * @author testautomatisering
 */
public class BookOperations {
    private String jsonString = "";
    
    
    public Response getAllBooks(){
        String resourceName = "books";
        Response getResponse = given().accept(ContentType.JSON).get(GlobVar.BASE_URL + resourceName);
        return getResponse;
    }
    public Response getBookById(int id){
        String resourceName = "books/"+id;
        Response response = given().accept(ContentType.JSON).get(GlobVar.BASE_URL+resourceName);
        return response;
    }
    public Response getBookByAuthor(int id){
        String resourceName = "books/byauthor/"+id;
        Response response = given().accept(ContentType.JSON).get(GlobVar.BASE_URL+resourceName);
        return response;
    }
    public Response getAuthorByBook(int id){
        String resourceName = "books/"+id+"/authors";
        Response response = given().accept(ContentType.JSON).get(GlobVar.BASE_URL+resourceName);
        return response;
    }
    
    public Response addAuthorToBook(String authorName, Integer authorId, Integer bookId){
        String resourceName = "books/"+bookId+"/authors";
        
        
        
        String postBodyTemplate = 
                        "{\n" 
                    +   "\"author\":\n" 
                    +   "  {\n" 
                    +   "    \"name\":\"%s\",\n" 
                    +   "    \"id\":%s\n"
                    +   "  }\n" 
                    +   "}";
        String postBody= String.format(postBodyTemplate, authorName, authorId);
        jsonString = postBody;
        Response postResponse = given().contentType(ContentType.JSON).body(postBody).post(GlobVar.BASE_URL + resourceName);
        return postResponse;
    }
    
    public Response makeBookWithInput(String description, String isbn, Integer nbOfPage, String title){
        Book book = new Book();
        book.setDescription(description);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setNbOfPage(nbOfPage);
        SingleBook singleBook = new SingleBook(book);
        Response postBookResponse = given().contentType(ContentType.JSON).body(singleBook).post(GlobVar.BASE_URL+"books");
        return postBookResponse;
    }
    public Response createBookWithInput(String description, String isbn, Integer nbOfPage, String title){
        String resourceName = "books";
        String postBodyTemplate = 
                        "{\n" 
                    +   "\"book\":\n" 
                    +   "  {\n" 
                    +   "    \"description\":\"%s\",\n" 
                    +   "    \"isbn\":\"%s\",\n" 
                    +   "    \"nbOfPage\":\"%s\",\n" 
                    +   "    \"title\":\"%s\"\n" 
                    +   "  }\n" 
                    +   "}";
        String postBody= String.format(postBodyTemplate, description, isbn, nbOfPage, title);
        jsonString = postBody;
        Response postResponse = given().contentType(ContentType.JSON).body(postBody).post(GlobVar.BASE_URL + resourceName);
        return postResponse;
    }
    
    public Response createRandomBook(){
        String resourceName = "books";
        String title = UUID.randomUUID().toString();
        String description = UUID.randomUUID().toString();
        String isbn = UUID.randomUUID().toString();
        
        String postBodyTemplate = 
                        "{\n" 
                    +   "\"book\":\n" 
                    +   "  {\n" 
                    +   "    \"description\":\"%s\",\n" 
                    +   "    \"isbn\":\"%s\",\n" 
                    +   "    \"nbOfPage\":\"%s\",\n" 
                    +   "    \"title\":\"%s\"\n" 
                    +   "  }\n" 
                    +   "}";
        String postBody= String.format(postBodyTemplate, description, isbn, new Random().nextInt(500), title);
        jsonString = postBody;
        Response postResponse = given().contentType(ContentType.JSON).body(postBody).post(GlobVar.BASE_URL + resourceName);
        return postResponse;
    }
    
    public Response createRandomBookWithAuthor(String authorName, Integer authorId){
        String resourceName = "books";
        String title = UUID.randomUUID().toString();
        String description = UUID.randomUUID().toString();
        String isbn = UUID.randomUUID().toString();
        String name = authorName;
        Integer id = (int)authorId;
        
        String postBodyTemplate = 
                        "{\n" 
                    +   "\"book\":\n" 
                    +   "  {\n" 
                    +   "    \"author\":\n" 
                    +   "    {\n" 
                    +   "      \"name\":\"%s\",\n" 
                    +   "      \"id\":%s\n" 
                    +   "    }\n" 
                    +   "    \"description\":\"%s\",\n" 
                    +   "    \"isbn\":\"%s\",\n" 
                    +   "    \"nbOfPage\":\"%s\",\n"
                    +   "    \"title\":\"%s\"\n" 
                    +   "  }\n" 
                    +   "}";
                
        String postBody= String.format(postBodyTemplate, description, isbn, new Random().nextInt(500), title, authorName, authorId);
        jsonString = postBody;
        Response postResponse = given().contentType(ContentType.JSON).body(postBody).post(GlobVar.BASE_URL + resourceName);
        return postResponse;        
    }
    
    
    public String getLatestJsonString(){
        return jsonString;
    }
    
    public Book fetchLastBook(){
        Response getResponse = new BookOperations().getAllBooks();
        int fetchedId = getResponse.jsonPath().getInt("books.book[-1].id");
        Book fetchBook = given().accept(ContentType.JSON).get(GlobVar.BASE_URL+"books/"+fetchedId).jsonPath().getObject("book", Book.class);
        return  fetchBook;
    }
    
    public Response GetLastBook(){
        Response getResponse = new BookOperations().getAllBooks();
        int fetchedId = getResponse.jsonPath().getInt("books.book[-1].id");
        Response fetchResponse = new BookOperations().getBookById(fetchedId);
        return  fetchResponse;
    }
    public Response deleteLastBook(){   
        Response getResponse = new BookOperations().getAllBooks();
        int fetchedId = getResponse.jsonPath().getInt("books.book[-1].id");
        Response deleteResponse = new BookOperations().deleteBook(fetchedId);
        return deleteResponse;
    }
    
    public Response deleteBook(int id){
        String deleteResourceName = "books/"+id;
        
        Response deleteResponse = delete(GlobVar.BASE_URL + deleteResourceName);
        return deleteResponse;
    }
}
