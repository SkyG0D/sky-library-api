package sky.skygod.skylibrary.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.skygod.skylibrary.dto.author.AuthorPostRequestBody;
import sky.skygod.skylibrary.dto.author.AuthorPutRequestBody;
import sky.skygod.skylibrary.event.ResourceCreatedEvent;
import sky.skygod.skylibrary.exception.NotFoundException;
import sky.skygod.skylibrary.exception.details.ExceptionDetails;
import sky.skygod.skylibrary.model.Author;
import sky.skygod.skylibrary.service.AuthorService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Api(value = "Author")
public class AuthorController {

    private final AuthorService authorService;
    private final ApplicationEventPublisher publisher;

    @ApiOperation(value = "Returns page object of author")
    @GetMapping
    public ResponseEntity<Page<Author>> list(Pageable pageable) {
        return ResponseEntity.ok(authorService.list(pageable));
    }

    @ApiOperation(value = "Returns author given a uuid")
    @GetMapping("/{uuid}")
    public ResponseEntity<Author> findById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(authorService.findByIdOrElseThrowNotFoundException(uuid));
    }

    @ApiOperation(value = "Returns authors list")
    @GetMapping("/find-by")
    public ResponseEntity<List<Author>> findBy(@RequestParam String name) {
        return ResponseEntity.ok(authorService.findBy(name));
    }

    @ApiOperation(value = "Creates new author")
    @PostMapping("/admin")
    public ResponseEntity<Author> save(@RequestBody @Valid AuthorPostRequestBody authorPostRequestBody,
                                       HttpServletResponse response) {

        Author savedAuthor = authorService.save(authorPostRequestBody);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, savedAuthor.getUuid()));
        return new ResponseEntity<>(savedAuthor, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Removes author given uuid")
    @DeleteMapping("/admin/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable UUID uuid) {
        authorService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Updates author")
    @PutMapping("/admin")
    public ResponseEntity<Void> replace(@RequestBody @Valid AuthorPutRequestBody authorPutRequestBody) {
        authorService.replace(authorPutRequestBody);
        return ResponseEntity.noContent().build();
    }

}
