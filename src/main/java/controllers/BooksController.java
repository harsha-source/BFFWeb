package controllers;

import client.BooksClient;
import errors.CustomFeignException;
import models.Books;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Controller for Books endpoints
@RestController
@RequestMapping("/books")
public class BooksController {
    private final BooksClient booksClient;

    public BooksController(BooksClient booksClient) {
        this.booksClient = booksClient;
    }

//        @GetMapping
//        public ResponseEntity<Object> getAllBooks() {
//            return ResponseEntity.ok(booksClient.getAllBooks());
//        }

    @PostMapping
    public ResponseEntity<Object> addBook(@RequestBody Books book) {
        try {
            Object result = booksClient.addBook(book);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                // Convert the response body to the appropriate format
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                // Fallback if we can't read the response body
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }


    //        @GetMapping("/{isbn}")
//        public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn) {
//            return ResponseEntity.ok(booksClient.getBookByIsbn(isbn));
//        }
//
//        @GetMapping("/isbn/{isbn}")
//        public ResponseEntity<Object> getBookByIsbnAlt(@PathVariable String isbn) {
//            return ResponseEntity.ok(booksClient.getBookByIsbnAlt(isbn));
//        }
    @GetMapping("/{isbn}")
    public ResponseEntity<Object> getBookByIsbn(@PathVariable String isbn) {
        try {
            Object result = booksClient.getBookByIsbn(isbn);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Object> getBookByIsbnAlt(@PathVariable String isbn) {
        try {
            Object result = booksClient.getBookByIsbnAlt(isbn);
            return ResponseEntity.ok(result);
        } catch (CustomFeignException e) {
            try {
                String responseBody = StreamUtils.copyToString(e.getBody().asInputStream(), StandardCharsets.UTF_8);
                return ResponseEntity.status(e.getStatus()).body(responseBody);
            } catch (IOException ioException) {
                return ResponseEntity.status(e.getStatus()).build();
            }
        }
    }
}
