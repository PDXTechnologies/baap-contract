package life.pdx.dapp.sample.db.repository;

import java.util.List;

import javax.inject.Singleton;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import life.pdx.dapp.sample.db.entity.DBData;

@Repository
@Singleton
public interface DBDataRepository extends CrudRepository<DBData, Long> {
	
	@Query(value="select * from db_data where txId = ?1", nativeQuery = true)
	DBData findByTxId(String txId);
	
	@Query(value="select * from db_data where address = ?1", nativeQuery = true)
	List<DBData> findByAddress(String address);
	
}
