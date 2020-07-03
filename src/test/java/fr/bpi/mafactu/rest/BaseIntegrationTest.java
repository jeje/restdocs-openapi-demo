package fr.bpi.mafactu.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.bpi.mafactu.rest.model.Subscription;
import fr.bpi.mafactu.rest.repositories.SubscriptionRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

class BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    EntityLinks entityLinks;

    ResultActions resultActions;

    String json;

    String subscriptionId;

    @Before
    public void setUp() {
        subscriptionRepository.deleteAll();
    }

    protected void givenSubscription() throws Exception {
        givenSubscriptionPayload();
        whenSubscriptionIsCreated();
    }

    protected void givenSubscription(Subscription subscription) throws Exception {
        givenSubscriptionPayload(subscription);
        whenSubscriptionIsCreated();
    }

    protected void givenSubscriptionPayload() throws JsonProcessingException {
        givenSubscriptionPayload(Subscription.builder()
                .customerID("MAF-123456")
                .productID(123)
                .priceID(123)
                .build());
    }

    protected void givenSubscriptionPayload(Subscription subscription) throws JsonProcessingException {
        json = subscriptionToJson(subscription);
    }

    protected void whenSubscriptionIsCreated() throws Exception {
        resultActions = mockMvc.perform(post("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        String location = resultActions.andReturn().getResponse().getHeader(LOCATION);
        subscriptionId = location.substring(location.lastIndexOf("/") + 1);
    }

    protected ResultActions whenSubscriptionIsCreated(Subscription subscription) throws Exception {
        return mockMvc.perform(post("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("accept", MediaType.APPLICATION_JSON)
                .content(subscriptionToJson(subscription)));
    }

    private String subscriptionToJson(Subscription subscription) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        //objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writeValueAsString(subscription);
    }

    protected List<FieldDescriptor> pageableFieldDocumentation() {
        return Arrays.asList(
                subsectionWithPath("pageable").description("Pagination data"),
                fieldWithPath("totalPages").description("Total of pages"),
                fieldWithPath("totalElements").description("Total of subscriptions"),
                fieldWithPath("last").description("Last page?"),
                fieldWithPath("size").description("Size of the page"),
                subsectionWithPath("sort").description("Sort data"),
                fieldWithPath("number").description("??"),
                fieldWithPath("numberOfElements").description("??"),
                fieldWithPath("first").description("??"),
                fieldWithPath("empty").description("??")
        );
    }
}
