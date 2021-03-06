package sky.skygod.skylibrary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sky.skygod.skylibrary.dto.book.BookGetResumedResponseBody;
import sky.skygod.skylibrary.dto.book.BookPostRequestBody;
import sky.skygod.skylibrary.dto.book.BookPutRequestBody;
import sky.skygod.skylibrary.exception.NotFoundException;
import sky.skygod.skylibrary.mapper.BookMapper;
import sky.skygod.skylibrary.model.Book;
import sky.skygod.skylibrary.repository.book.BookFilter;
import sky.skygod.skylibrary.repository.book.BookRepository;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;
import static sky.skygod.skylibrary.repository.book.BookSpecifications.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookGetResumedResponseBody> resumed(BookFilter bookFilter, Pageable pageable) {
            return search(bookFilter, pageable).map(BookMapper.INSTANCE::toBookGetResumedResponseBody);
    }

    public Page<Book> search(BookFilter bookFilter, Pageable pageable) {
        return bookRepository.findAll(where(
            withName(bookFilter.getName())).and(where(
            withDescription(bookFilter.getDescription()))).and(where(
            withStatus(bookFilter.getStatus()))).and(where(
            withAuthorId(bookFilter.getAuthorId()))).and(where(
            withAuthorName(bookFilter.getAuthorName()))).and(where(
            withGenderId(bookFilter.getGenderId()))).and(where(
            withGenderName(bookFilter.getGenderName()))).and(where(
            withPublishingCompanyId(bookFilter.getPublishingCompanyId()))).and(where(
            withPublishingCompanyName(bookFilter.getPublishingCompanyName()))
        ), pageable);
    }

    public Book findByIdOrElseThrowNotFoundException(UUID uuid) {
        return bookRepository.findById(uuid)
            .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public Book findByIsbn(Long isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book save(BookPostRequestBody bookPostRequestBody) {
        String cover = bookPostRequestBody.getCover();
        bookPostRequestBody.setCover(Optional.ofNullable(cover).orElse("no-cover.png"));
        return bookRepository.save(BookMapper.INSTANCE.toBook(bookPostRequestBody));
    }

    public void delete(UUID uuid) {
        bookRepository.delete(findByIdOrElseThrowNotFoundException(uuid));
    }

    public void replace(BookPutRequestBody bookPutRequestBody) {
        Book savedBook = findByIdOrElseThrowNotFoundException(bookPutRequestBody.getUuid());
        Book book = BookMapper.INSTANCE.toBook(bookPutRequestBody);
        book.setUuid(savedBook.getUuid());
        bookRepository.save(book);
    }

}
