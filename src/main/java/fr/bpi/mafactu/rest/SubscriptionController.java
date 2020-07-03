package fr.bpi.mafactu.rest;

import fr.bpi.mafactu.rest.model.Subscription;
import fr.bpi.mafactu.rest.repositories.SubscriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;
    private final UriTemplate subscriptionLocationTemplate = new UriTemplate("/subscriptions/${id}");

    public SubscriptionController(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @GetMapping
    public Page<Subscription> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable);
    }

    @PostMapping
    public ResponseEntity<Subscription> createSubscription(@Valid @RequestBody Subscription subscription) {
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        URI subscriptionURL = subscriptionLocationTemplate.expand(savedSubscription.getId());
        return ResponseEntity
                .created(subscriptionURL)
                .build();
    }
}
