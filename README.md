## PDX BaaP Contract Demo
		baap-contract-simple: 简单合约示例
		baap-contract-db：复杂合约示例
		baap-contract-oobm：oobm示例



### 1. maven工程创建  pom.xml 配置

#### 配置 maven repo
		<repository>
			<id>pdx-release</id>
			<name>biz.pdxtech</name>
			<url>http://daap.pdx.life:8081/nexus/content/repositories/releases</url>
		</repository>

#### 配置PDX BlockChain Driver API  依赖
		<!-- bcdriver api -->
		<dependency>
			<groupId>biz.pdxtech.baap</groupId>
			<artifactId>baap-api</artifactId>
			<version>2.1.0</version>
		</dependency>
		<dependency>
			<groupId>biz.pdxtech.baap</groupId>
			<artifactId>baap-driver-ethereum</artifactId>
			<version>2.1.0</version>
		</dependency>
		<dependency>
			<groupId>biz.pdxtech.baap</groupId>
			<artifactId>baap-setting</artifactId>
			<version>2.1.0</version>
		</dependency>


### 2. BcDriver实例化

	调用BcDriver首先需要实例化Driver.主要是四个参数。1：协议栈类型；2：区块链id；3：区块链节点的baap-url；4：用户私钥。
	缺省情况下，协议栈类型为 ethereum。调用者可以通 过如下方法进行参数设置。 

#### 通过property配置实例化
		Properties properties = new Properties();
		properties.setProperty("baap-chain-type", Constants.BAAP_CHAIN_TYPE_ETHEREUM);
		properties.setProperty("baap-chain-id", Constants.BAAP_CHAIN_ID_DEFAULT);
		properties.setProperty("baap-url", "http://10.0.0.7:8080/");
		properties.setProperty("baap-private-key", PRIVATE_KEY);
		BlockChainDriver driver = BlockChainDriverFactory.get(properties);


### 3. BcDriver调用

	通过BcDriver以下方法调用链上或者远端合约：
		query
		apply
	参见BaapCaller 例子
		通过设置Transaction 中属性指定contract 地址和自定义逻辑等
		dst  -->合约地址
		meta -->元数据，可查询
		body -->自定义数据结构
