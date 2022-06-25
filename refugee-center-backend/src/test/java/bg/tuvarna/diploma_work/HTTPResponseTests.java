package bg.tuvarna.diploma_work;


import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.models.VerificationCode;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HTTPResponseTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getUserById() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/get-user/" + 1,
                User.class)).hasFieldOrProperty("id");
    }


    @Test
    public void getUserByIdUnsuccessful() throws Exception {
        assertThat(restTemplate.getForObject("http://localhost:" + port + "/get-user/" + -312312,
                User.class)).isNull();
    }

    @Test
    public void authenticateUser() throws Exception {

        final String baseUrl = "http://localhost:"+port+"/authenticate-user";
        URI uri = new URI(baseUrl);
        User user = new User();
        user.setEmail("iliyan.stanchevv@gmail.com");
        user.setPassword("sach2password");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<User> result = this.restTemplate.postForEntity(uri, request, User.class);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void verifyVerificationCode() throws Exception {

        final String baseUrl = "http://localhost:"+port+"/verify-verification-code";
        URI uri = new URI(baseUrl);

        User user = new User();
        user.setId( 1L );

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser( user );
        verificationCode.setCode("Sdadasdas@#!das");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<VerificationCode> request = new HttpEntity<>(verificationCode, headers);
        ResponseEntity<Void> result = this.restTemplate.postForEntity(uri, request, Void.class);
        Assert.assertEquals(433, result.getStatusCodeValue());
    }

    @Test
    public void validateEmployeeCreation() throws Exception {

        final String baseUrl = "http://localhost:"+port+"/create-employee";
        URI uri = new URI(baseUrl);

        User user = new User();
        user.setEmail("iliyan.stanchev@gmail.com");
        user.setIdentifier("9902265760");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<Void> result = this.restTemplate.postForEntity(uri, request, Void.class);
        Assert.assertEquals(433, result.getStatusCodeValue());
    }

    @Test
    public void createEmployee() throws Exception {

        final String baseUrl = "http://localhost:"+port+"/create-employee";
        URI uri = new URI(baseUrl);

        User user = new User();
        user.setEmail("testCreatopm@gmail.com");
        user.setIdentifier("testCreation");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<Void> result = this.restTemplate.postForEntity(uri, request, Void.class);
        Assert.assertEquals(433, result.getStatusCodeValue());
    }
}