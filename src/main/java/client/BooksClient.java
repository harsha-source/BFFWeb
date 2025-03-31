package client;

import configuration.FeignConfig;
import models.Books;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "A1", url = "http://internal-bookstore-dev-InternalALB-1706046400.us-east-1.elb.amazonaws.com/books:3000", configuration = FeignConfig.class)
public interface BooksClient {
//        @GetMapping
//        Object getAllBooks();

    @PostMapping
    Object addBook(Books book);

    @GetMapping("/{isbn}")
    Object getBookByIsbn(@PathVariable("isbn") String isbn);

    @GetMapping("/isbn/{isbn}")
    Object getBookByIsbnAlt(@PathVariable("isbn") String isbn);

    @GetMapping("/status")
    Object getStatus();
}
