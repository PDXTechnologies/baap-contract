package life.pdx.dapp.sample.db.conf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@ComponentScan(basePackages = {"life.pdx.dapp.sample.db"})
@EnableJpaRepositories(basePackages = {"life.pdx.dapp.sample.db.repository"})
@EnableTransactionManagement
@EnableScheduling
public class SpringConfiguration extends Application {

	private static final String PERSISTENCE_UNIT_NAME = "dbcontract";
	
	public static Properties properties;
	static {
		properties = new Properties();
		try {
			InputStream is = SpringConfiguration.class.getResourceAsStream("/db.properties");
			if(is != null){
				properties.load(is);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Bean
	public javax.sql.DataSource dataSource() {
		DruidDataSource dataSource= new DruidDataSource();
	    dataSource.setDriverClassName(properties.getProperty("jdbc.driverClassName"));
	    dataSource.setUrl(properties.getProperty("jdbc.url"));
	    dataSource.setUsername(properties.getProperty("jdbc.username"));
	    dataSource.setPassword(properties.getProperty("jdbc.password"));  
	    dataSource.setTestOnBorrow(true);
	    dataSource.setValidationQuery("SELECT 1");
	    return dataSource;
	}

	@Bean
	public JpaTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		emfBean.setDataSource(dataSource());
		emfBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		Map<String, String> jpaProperties = new HashMap<String, String>();
		jpaProperties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=true)");
		emfBean.setJpaPropertyMap(jpaProperties);
		return emfBean;
	}
	
}
