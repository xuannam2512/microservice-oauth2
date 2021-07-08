package com.microservice.oauth2.controller;

import com.microservice.oauth2.config.oauth2.CustomAccessTokenConverter;
import com.microservice.oauth2.domain.bean.UserDto;
import com.microservice.oauth2.service.RefreshTokenService;
import com.microservice.oauth2.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Log4j2
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final ResourceServerTokenServices resourceServerTokenServices;
    private final CustomAccessTokenConverter customAccessTokenConverter;
    private final WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.create(userDto));
    }

    @PutMapping("user/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Integer userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.update(userId, userDto));
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @DeleteMapping("refreshToken/{token}")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable String token) {
        refreshTokenService.deleteByToken(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("token_new")
    public ResponseEntity<Map<String, Object>> getNewAccessToken(@RequestParam("token") String value,
                                                                 @RequestParam("path") String path,
                                                                 @RequestParam("method") String method) {
        var accessToken = resourceServerTokenServices.readAccessToken(value);
        if (accessToken == null) {
            throw new InvalidTokenException("Token was not recognised");
        } else if (accessToken.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        } else {
            OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(accessToken.getValue());
            var permissionList = userService.getPermissionByUserId(Integer.valueOf(accessToken.getAdditionalInformation().get("id").toString()));

            // check path and method
            var isMatched = permissionList.stream()
                    .anyMatch(permission -> path.equals(permission.getPath()) && method.equals(permission.getMethod()));

            if (isMatched) {
                var newAccessToken = new DefaultOAuth2AccessToken(accessToken);
                newAccessToken.setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000L));
                var newAccessTokenValue = customAccessTokenConverter.encode(newAccessToken, authentication);
                return ResponseEntity.ok(Map.of("accessToken", newAccessTokenValue));
            }

            throw new InvalidTokenException("No permission");
        }
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        log.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        InvalidTokenException e400 = new InvalidTokenException(e.getMessage()) {
            public int getHttpErrorCode() {
                return 400;
            }
        };
        return this.exceptionTranslator.translate(e400);
    }
}
