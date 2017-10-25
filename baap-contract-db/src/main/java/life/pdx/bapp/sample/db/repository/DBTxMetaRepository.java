package life.pdx.bapp.sample.db.repository;

import java.util.List;

import javax.inject.Singleton;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import life.pdx.bapp.sample.db.entity.DBTxMeta;

@Repository
@Singleton
public interface DBTxMetaRepository extends CrudRepository<DBTxMeta, Long> {
	
	@Query(value="select * from db_txmeta where  metaKey = ?1  order by createTime", nativeQuery = true)
	List<DBTxMeta> findByMetaKey( String metaKey);
}
