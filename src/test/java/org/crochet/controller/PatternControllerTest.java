package org.crochet.controller;

import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.crochet.security.TokenAuthenticationFilter;
import org.crochet.service.abstraction.FirebaseService;
import org.crochet.service.abstraction.PatternService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(PatternController.class)
public class PatternControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PatternService patternService;

  @MockBean
  private FirebaseService firebaseService;

  @MockBean
  private TokenAuthenticationFilter tokenAuthenticationFilter;

  @Test
  public void testGetPatterns() throws Exception {
    // Mock data for testing
    PatternResponse response = PatternResponse.builder()
        .id(1L)
        .name("test")
        .image("test")
        .description("test")
        .price(10)
        .build();
    List<PatternResponse> mockPatterns = Collections.singletonList(response);
    PatternPaginationResponse mockResponse = PatternPaginationResponse.builder()
        .contents(mockPatterns)
        .pageNo(1)
        .pageSize(10)
        .build();

    // Mock the service method to return the mock response
    when(patternService.getPatterns(anyInt(), anyInt(), anyString(), anyString(), anyString()))
        .thenReturn(mockResponse);

    // Perform the request and verify the response
    var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/pattern/pagination")
            .param("pageNo", "1")
            .param("pageSize", "10")
            .param("sortBy", "name")
            .param("sortDir", "asc")
            .param("text", "test")
            .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    // Check the HTTP status code (302 for redirect)
    int statusCode = mvcResult.getResponse().getStatus();
    assertEquals(302, statusCode);

    // Extract the redirected URL
    String redirectedUrl = mvcResult.getResponse().getHeader("Location");
    assertEquals("http://localhost:8080/oauth2/authorization/google", redirectedUrl);
  }
}
