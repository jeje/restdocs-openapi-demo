package fr.bpi.mafactu.rest.repositories;

import fr.bpi.mafactu.rest.model.Subscription;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Long> {
}
