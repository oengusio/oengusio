package app.oengus.dao;

import app.oengus.entity.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    boolean existsByCode(String code);

    Category findByCode(String code);

}
