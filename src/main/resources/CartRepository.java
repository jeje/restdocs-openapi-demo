package fr.bpi.mafactu.rest.repositories;

import fr.bpi.mafactu.rest.model.Cart;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CartRepository extends PagingAndSortingRepository<Cart, Long> {

}
