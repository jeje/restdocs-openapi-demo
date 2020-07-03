package fr.bpi.mafactu.rest.repositories;

import fr.bpi.mafactu.rest.model.Product;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {
}
