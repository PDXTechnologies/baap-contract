package life.pdx.bapp.sample.db.repository;

import javax.inject.Singleton;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import life.pdx.bapp.sample.db.entity.DBTransaction;

@Repository
@Singleton
public interface DBTransactionRepository extends CrudRepository<DBTransaction, Long> {
	
//	@Query(value="select * from db_transaction where  txId = ?1", nativeQuery = true)
	DBTransaction findByTxId(String txId);
	
}
