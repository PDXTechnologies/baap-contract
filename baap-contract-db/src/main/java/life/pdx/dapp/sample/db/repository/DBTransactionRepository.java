package life.pdx.dapp.sample.db.repository;

import java.util.List;

import javax.inject.Singleton;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import life.pdx.dapp.sample.db.entity.DBTransaction;

@Repository
@Singleton
public interface DBTransactionRepository extends CrudRepository<DBTransaction, Long> {
	
	@Query(value="select * from db_transaction where  txId = ?1", nativeQuery = true)
	DBTransaction findByTxId( String txId);
	
}
