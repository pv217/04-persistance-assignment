# 04 - Async, Persistence

## SmallRye Mutiny

Since we will be using a reactive approach, it is a good thing to know something about Mutiny.

Mutiny is a reactive programming library for Java. It is based on Reactive Streams and MicroProfile Reactive Streams Operators. So when you see `Uni` or `Multi` in the code, it means that the method returns a reactive type, and the method is asynchronous.

**What are `Uni` and `Multi`?**

`Uni` is a type that emits either a single item or an error. `Multi` is a type that emits a stream of items or an error.

**Pros of asynchronous programming:**
- Non-blocking
- Better resource utilization
- Better scalability

**Cons of asynchronous programming:**
- Complexity of callbacks, exceptions
- Harder to debug
- Harder to test

For more in depth discussion about asynchronous programming and the difference with multithreading, see [this article](https://www.baeldung.com/cs/async-vs-multi-threading).

### Mutiny's Fluent API

Mutiny provides a fluent API for building reactive streams. It's a chain of operations that is readable and expressive. This approach simplifies the complexity of asynchronous programming.

The design of the API is designed to be easy to read and understand what the code does. It's a chain of operations that are executed when the previous operation is done.

### `onItem` and `transform` methods

- `onItem` and `transform` methods are used to process the result of the asynchronous operation. It's event driven reaction to the result of the operation when it's done.

Example:
```java
Uni<String> processedUni = Uni.createFrom().item("Hello") // Creates an async uni that emits a single item
        .onItem() // Reacts to the item
        .transform(item -> item + " World"); // Transforms the item to antoher item that is synchronous


        Uni<String> chainedUni = Uni.createFrom().item(123) // Creates an async uni that emits a single item
        .onItem() // Reacts to the item
        .transformToUni(number -> Uni.createFrom().item(number * 10)); // Transforms the item to another item
```

## What is Object Relational Mapping (ORM)?

ORM is a technique that lets you query and manipulate data in a database using an object-oriented paradigm. Thus, instead of writing SQL queries, you can write Java code to perform the same operations.

It introduces abstraction between the database and the application. You can change the database more easily without changing the application code.

### Persistence with Panache

Panache is ORM layer for Quarkus. It is based on Hibernate ORM and Hibernate Reactive.

For both PanacheEntity and PanacheRepository, you can see examples below.

## Active record vs repository

Both active record and repository are patterns for accessing data in a database.

### Active record

The Active record pattern is an approach where the data access logic is part of the entity itself. Each entity (or record) is responsible for its own persistence and encapsulates both the data and the behavior that operates on the data.

Pros:
- It's easy to set up for simple operations
- Less boilerplate

Cons:
- Logic not separated from data
- Tight coupling between schema and code

#### PanacheEntity

PanacheEntity is a base class for entities. It provides basic operations for entities such as persist, delete, find, etc. It's used for active record pattern.

##### What gives you PanacheEntity?

Attributes:
- `id` - Automatically adds the primary key of the entity.

Methods:
- `persist()` - Persists the entity to the database.
- `delete()` - Deletes the entity from the database.
- `isPersistent()` - Checks if the entity is persistent.
- `findById()` - Finds an entity by its primary key.
- `listAll()` - Returns a list of all entities.
- `count()` - Returns the number of entities.
- `find()` - Finds entities by a query.
- ... and more

#### Example

```java
import io.quarkus.hibernate.reactive.panache.PanacheEntity; // note the reactive package. There is also a non-reactive variant.

@Entity
public class Person extends PanacheEntity {
    public String name;
    public LocalDate birth;
    public Status status;

    public static Uni<Person> findByName(String name){
        return find("name", name).firstResult();
    }
}
```
Basic usage
```java    
// persist it
Uni<Void> persistOperation = person.persist();

// check if it is persistent
if(person.isPersistent()){
    // delete it
    Uni<Void> deleteOperation = person.delete();
}

// getting a list of all Person entities
Uni<List<Person>> allPersons = Person.listAll();

// finding a specific person by ID
Uni<Person> personById = Person.findById(23L);
```

### Repository

Instead of having both schema and logic in the same class, the repository pattern separates schema from data access logic in a separate class.

Pros:
- Logic separated from data
- Cleaner and more testable code
- DAL (Data Access Layer) is decoupled from the rest of the application

Cons:
- More boilerplate for simple operations
- Takes more time to set up

### PanacheRepository

PanacheRepository is a base class for repositories. It provides similar logic as PanacheEntity, but it's used for repository pattern.

But you need to define the entity more explicitly with id, getters, setters, etc. Then you will create a repository class that extends `PanacheRepository<Entity>`.

#### Example

```java
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDate birth;
    private Status status;
    
    // getters and setters
}

@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> {

    // put your custom logic here as instance methods

    public Uni<Person> findByName(String name){
        return find("name", name).firstResult();
    }

    public Uni<List<Person>> findAlive(){
        return list("status", Status.Alive);
    }

    public Uni<Long> deleteStefs(){
        return delete("name", "Stef");
    }
}
```

#### `@WithTransaction` annotation

- `@WithTransaction` annotation is used to mark a method as transactional. It means that the method will be executed in a transactional context. If the method fails, the transaction will be rolled back. This annotation is used when accessing the database. Altering the database should be done in a transactional context, but also reading from the database. Example bellow.
- Transactions follow a unit of work pattern. It means that all operations in a transaction are treated as a single unit of work. If any operation fails, the whole transaction is rolled back.

Illustrative example of SQL query that should run in a transactional context:
```sql
SELECT AVG(price) as mean_price FROM price_table; 
-- between these two queries, another transaction can delete the rows from price_table, thus altering the result of the second query --> consistency problem
SELECT * FROM product_table WHERE price > mean_price;
```

## Entities with relations

If you have entities with relations, you can use `@OneToMany`, `@ManyToOne`, `@OneToOne`, `@ManyToMany` annotations to define the relation between entities. Then, with `@JoinColumn`, you can specify the column that will be used for the join.

#### Example

```java
@Entity // JPA (Java Persistence API) entity 
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @OneToMany(
            mappedBy = "post", // The name of the field in PostComment that references back to this Post entity, indicating PostComment's ownership of the relationship.
            cascade = CascadeType.ALL, // All operations on the child will be cascaded to the parent
            orphanRemoval = true // If the child is removed from the collection, it will be removed from the database
    )
    @JoinColumn(name = "postId") // The name of the column that will be used for the join
    private List<PostComment> comments = new ArrayList<>();

    //Constructors, getters and setters removed for brevity

    public void addComment(PostComment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(PostComment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}

@Entity
public class PostComment {

    @Id
    @GeneratedValue
    private Long id;

    private String review;

    @ManyToOne
    @JoinColumn(name = "postId") // postId is foreign key to the Post entity
    private Post post; // This field references the Post entity

    //Constructors, getters and setters removed for brevity
}
```



## How do dev services help us in development?

Dev services give us a way to make development easier.

What are the benefits of dev services?
- Automatic startup -- check configuration, download dependencies, and start the service
- Continuous testing -- continuous testing of the application
- Configuration management -- automatic configuration of the service and database connection

For this exercise, the main benefit is a way to run a database in a Docker container without any configuration from the developer side. Of course, it's only for development purposes. But during the initial development phase, it's very useful. Before we create a configuration for the dockerized database,

## State of the project

- The `flight-service` has been updated with the repository pattern with Panache.
- REST APIs and services are now asynchronous.
- Objects DTOs are created where needed. Eg. `NotificationDto` in `passenger-service`.
- Panache extension has been added to the `passenger-service`.

## Tasks

### 0. Running Docker

Install [Docker desktop](https://docs.docker.com/engine/install/) or another Docker client. Our test database will run in a Docker container.

### 1. Make `Notification` active record entity

In `passenger-service`, make the Notification entity as active record using PanacheEntity.

Implement `deleteAll` in `NotificationService` with the usage of  `Notification` active record. The `listAll` method will be implemented in the next task.

Check if the tests for Notification entity deletion are passing. Use continuous testing or run in the passenger-service directory:

```bash
./mvnw clean test
```

### 2. Make `Passenger` entity

In the `Passenger` entity add correct annotations with getters and setters to make it a valid JPA entity that will be used in `PassengerRepository`.

Hmm, but what about the relationship with notifications? Passengers can have multiple notifications. Add the relation between `Passenger` and `Notification` entities.

### 3. Implement `PassengerRepository`

Implement methods in `PassengerRepository` to make it a repository for `Passenger` entity.

Don't forget to implement `NotificationService#listAll` method using `PassengerRepository`.

#### 3.1. How do you test if everything is working?

- Tests are passing

Test scenario
- Create a flight using Swagger UI
- Create a passenger using Swagger UI with appropriate flight id
- Call cancel flight endpoint
- Check if the GET notification endpoint returns the notification for the passenger with his email.

### 4. Submit the solution

1. Finish the tasks
2. Push the changes to the main branch
3. GitHub Classroom automatically prepared a feedback pull request for you
4. GitHub Actions will run basic checks for your submission on push
5. Teacher will evaluate the submission as well and give you feedback

Resubmit the solution if the checks fail:
1. Make changes
2. Push again

## Hints

- In `flight-service`, you can find implemented repository pattern with Panache.
- If something is not working, and it should (Developers aren't making mistakes, right?), run maven clean and compile commands.

## Troubleshooting

- Check if your Docker engine is running by running `docker ps` in the terminal.

## Further reading

- https://quarkus.io
- https://quarkus.io/guides/hibernate-reactive-panache
- https://medium.com/@shiiyan/active-record-pattern-vs-repository-pattern-making-the-right-choice-f36d8deece94
- https://quarkus.io/guides/mutiny-primer
- https://medium.com/@rajibrath20/the-best-way-to-map-a-onetomany-relationship-with-jpa-and-hibernate-dbbf6dba00d3
- https://quarkus.io/guides/dev-services