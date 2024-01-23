package ma.isga.inventoryservice.repository;

import ma.isga.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author mohamedyoussfi
 **/
public interface ProductRepository extends JpaRepository<Product,String> {
}
