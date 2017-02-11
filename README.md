# sqlite-crud-repository
Provides an easy to use persistence abstraction on top of SqliteLite and Spring JDBC. No need to write SQL. All schema information is derived from the java classes registered. 

## Getting Started

To get started using Sqlite-Crud-Repository you'll need to configure two classes, SqliteCrudManager and SqliteCrudRepository.

#### 1. SqliteCrudManager  
  * Register all Classes on this object. This has to be done for any classes used by the repository. 
  * Setting updateSchema(true); Will cause SqliteCrudManager to add any tables or columns to update the database schema 
  to match with the registered beans. This is a non destructive operation and will not delete any tables or columns.
```java
	@Bean
	public SqliteCrudManager getSqliteCrudManager() {
		SqliteCrudManager crudManager = new SqliteCrudManager();
		crudManager.registerEntity(User.class);
		crudManager.registerEntity(Event.class);
		crudManager.registerEntity(SignedInLog.class);
		crudManager.updateSchema(true);
		return crudManager;
	}
```
#### 2. SqliteCrudRepository  
  * The interface too all operations on the Sqlite database
  * Operations: create, read, update, and delete.
```java
	@Bean
	public SqliteCrudRepository getSqliteCrudRepo() {
		return new SqliteCrudRepositoryImpl();
	}
```
## Working with SqliteCrudRepository

In general all entity objects used with `SqliteCrudRepository` have to follow [JavaBeans](https://en.wikipedia.org/wiki/JavaBeans) conventions.
In addition to this all entity objects must contain a property `private Integer id`. This must be an `Integer`, not `int`, and must named 'id'.
Table and column names match exactly their java counterpoints. For example an class named 'MainEvents' would correspond to a table named 'main_events'. A property on this class named 'EventName' would correspond to a column named 'even_name' 

##### Retreiving an object
```java
    int userId = 12;
    User user = repository.retrieve(userId, User.class);
```
##### Retreiving all objects in a single table
```java
    List<User> users = repository.retrieveAll(User.class);
```
##### Creating a new object
```java
    User user = new user();
    ...
    // here user.id == null
    repository.save(user);
```  
##### Updating an existing object
```java
    // here user.id matches an existing record in the database and this row is updated
    repository.save(user);
```
##### Deleting an object
```java
    int userId = 12;
    repository.delete(userId, User.class);
```
## Dependencies
```xml
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-beans</artifactId>
    </dependency>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
    </dependency>
```
