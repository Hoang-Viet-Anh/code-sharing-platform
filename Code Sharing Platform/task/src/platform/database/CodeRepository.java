package platform.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CodeRepository extends CrudRepository<Code, String> {
    Code findById(UUID id);
    boolean existsById(UUID id);
    List<Code> findFirst10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(long time, long views);
}
