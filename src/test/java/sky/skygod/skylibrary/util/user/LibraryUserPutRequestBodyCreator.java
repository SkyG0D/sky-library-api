package sky.skygod.skylibrary.util.user;

import org.springframework.security.core.GrantedAuthority;
import sky.skygod.skylibrary.dto.user.LibraryUserPutRequestBody;

import java.util.Set;
import java.util.stream.Collectors;

public class LibraryUserPutRequestBodyCreator {

    public static LibraryUserPutRequestBody createLibraryUserPutRequestBodyToBeUpdate() {
        Set<String> authorities = LibraryUserCreator.createValidUpdatedLibraryUser().getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        return LibraryUserPutRequestBody
            .builder()
            .uuid(LibraryUserCreator.createValidUpdatedLibraryUser().getUuid())
            .name(LibraryUserCreator.createValidUpdatedLibraryUser().getName())
            .password(LibraryUserCreator.createValidUpdatedLibraryUser().getPassword())
            .email(LibraryUserCreator.createValidUpdatedLibraryUser().getEmail())
            .authorities(authorities)
            .build();
    }

}
