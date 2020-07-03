package fr.bpi.mafactu.rest;

import com.epages.restdocs.apispec.ConstrainedFields;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bpi.mafactu.rest.model.Subscription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class SubscriptionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    private ConstrainedFields fields = new ConstrainedFields(Subscription.class);

    @Test
    public void should_get_subscriptions() throws Exception {
        givenSubscription();
        givenSubscription(Subscription.builder()
                .customerID("MAF-9875")
                .productID(123)
                .priceID(123)
                .build());
        givenSubscription(Subscription.builder()
                .customerID("MAF-4566")
                .productID(123)
                .priceID(123)
                .build());

        whenSubscriptionsAreRetrieved();

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(2)))
                .andDo(document("subscriptions-get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("Get subscriptions")
                                        .description("Get subscriptions with optional paging and sorting")
                                        .requestHeaders(headerWithName("accept").description("accept header"))
                                        .requestParameters(
                                                parameterWithName("page").description("Page number").optional(),
                                                parameterWithName("size").description("Size of the page").optional(),
                                                parameterWithName("sort").description("Sort query").optional()
                                        )
                                        .responseFields(Stream.of(Arrays.asList(
                                                fieldWithPath("content").description("List of subscriptions"),
                                                fieldWithPath("content[].id").description("ID of the subscription"),
                                                fieldWithPath("content[].customerID").description("ID of the customer for this subscription"),
                                                fieldWithPath("content[].productID").description("ID of the product subscribed"),
                                                fieldWithPath("content[].priceID").description("ID of the price of this subscription")
                                        ), pageableFieldDocumentation()).flatMap(Collection::stream).collect(Collectors.toList()))
                                .responseSchema(Schema.schema("subscriptions-results"))
                                .build()
                        )
                ));
    }

    @Test
    public void should_create_new_subscription() throws Exception {
        Subscription subscription = Subscription.builder()
                .customerID("TEST-123456")
                .productID(123)
                .priceID(123)
                .build();

        whenSubscriptionIsCreated(subscription)
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("subscriptions-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("Create a new subscription")
                                        .description("Create a new subscription")
                                        .requestHeaders(headerWithName("accept").description("accept header"))
                                        .requestFields(
                                                fieldWithPath("customerID").description("ID of the customer for this subscription"),
                                                fieldWithPath("productID").description("ID of the product subscribed"),
                                                fieldWithPath("priceID").description("ID of the price of this subscription")
                                        )
                                        .responseHeaders(
                                                headerWithName(HttpHeaders.LOCATION).description("Location of the created subscription")
                                        )
                                        .requestSchema(Schema.schema("subscription-creation"))
                                        .build()
                        )
                ));
    }

    @Test
    public void should_not_create_a_new_subscription_from_invalid_content() throws Exception {
        Subscription subscription = Subscription.builder()
                .productID(123)
                .priceID(123)
                .build();
        whenSubscriptionIsCreated(subscription)
                .andExpect(status().isBadRequest())
                .andDo(document("subscriptions-create-with-missing-customerID",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        resource(
                                ResourceSnippetParameters.builder()
                                        .summary("Create a new subscription")
                                        .description("Create a new subscription")
                                        .requestHeaders(headerWithName("accept").description("accept header"))
                                        .requestSchema(Schema.schema("subscription-creation"))
                                        .responseFields(
                                                fieldWithPath("timestamp").description("Date of the error"),
                                                fieldWithPath("message").description("Short description of the error"),
                                                fieldWithPath("details").description("Details of the error"),
                                                fieldWithPath("status").description("HTTP Status Code of the error")
                                        )
                                        .responseSchema(Schema.schema("error"))
                                        .build()
                        )
                ));
    }

    private void whenSubscriptionsAreRetrieved() throws Exception {
        resultActions = mockMvc.perform(get("/subscriptions")
                        .header("accept", MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "2")
                //.param("sort", "customerID asc")
        )
                .andDo(print());
    }
}
