package ru.job4j.cinema.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private HttpServletRequest request;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        userController = new UserController(userService);
    }

    @Test
    void whenGetRegistrationPageThenReturnRegisterView() {
        String view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenRegisterSuccessThenRedirectIndex() {
        Model model = new ConcurrentModel();
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.save(user)).thenReturn(Optional.of(user));

        String view = userController.register(model, user);

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(model.getAttribute("message")).isNull();
    }

    @Test
    void whenRegisterDuplicateEmailThenReturnErrorView() {
        Model model = new ConcurrentModel();
        User user = new User();
        user.setEmail("test@example.com");
        when(userService.save(user)).thenReturn(Optional.empty());

        String view = userController.register(model, user);

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Пользователь с такой почтой уже существует");
    }

    @Test
    void whenGetLoginPageThenReturnLoginView() {
        String view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenLoginSuccessThenRedirectIndex() {
        Model model = new ConcurrentModel();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setEmail(user.getEmail());

        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(savedUser));
        when(request.getSession()).thenReturn(session);

        String view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/index");
        verify(session).setAttribute("user", savedUser);
        assertThat(model.getAttribute("error")).isNull();
    }

    @Test
    void whenLoginFailThenReturnLoginViewWithError() {
        Model model = new ConcurrentModel();
        User user = new User();
        user.setEmail("wrong@example.com");
        user.setPassword("wrongpass");

        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        String view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("users/login");
        assertThat(model.getAttribute("error")).isEqualTo("Почта или пароль введены неверно");
        verify(request, never()).getSession();
    }

    @Test
    void whenLogoutThenInvalidateSessionAndRedirectLogin() {
        String view = userController.logout(session);
        verify(session).invalidate();
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}
