# Simple Wallet Back-end Implementation Using Lmax Disruptor And In-memory Data Stores
Handle money transfers between managed accounts with the most efficient and coherent way. Integrate clients with a REST api.

## Requirements Analysis
* Used Kotlin
* Prepared a priority based TODO plan with initial estimates. You can find it [here](TODO.md)
* Made use of these frameworks/libraries/tools :
     * __Gradle__ => build automation
     * __Pippo__ => rest integration
     * __Guice__ => dependency injection
     * __CQEngine__ || __Redis__ (somehow embedded) => In-memory storage alternatives (I was planning to add RocksDB and Hazelcast too, but with a lower priority. I couldn't find time at the end.)
     * __Logback__ => logging
     * __Lmax Disruptor__ => concurrency framework for business logic
     * __MockK__ => mocking library for tests
     * __JUnit5__ => testing platform
     * __Generex__ => regex based random string generator for tests
     * __Ktor__ => http client for testing rest integration 
* Designed endpoint structure is as follows (ApiResponse is a common model for responses holding returnValue or error definition) :
```
(Create Account)        POST    /accounts {AccountRequest}, {Account}
(Get All Accounts)      GET     /accounts ,[Account]
(Get Account)           GET     /accounts/{account_id} ,{Account}
(Get Transfers)         GET     /accounts/{account_id}/transfers?direction={INBOUND, OUTBOUND, ALL}&limit=&after= [Transfer]
(Create a Transfer)     POST    /transfers {TransferRequest}, {TransferResponse}
(Get all Transfers)     GET     /transfers [Transfer]
(Get Transfer status)   GET     /transfers/{transfer_id}/status ,{TransferStatus}
(Cancel a Transfer)     DELETE  /transfers/{transfer_id} {TransferStatus}
```
* I devised 3 types of transfers : deposit, withdraw and internal (more critical one).
* System keeps _Accounts_ and _Transfers_ in separate stores. You can check their corresponding classes.
* An executable fatjar for running whole system by simply copying file and run. 

### Why Pippo?
I wanted a light JVM web framework easily configurable (supporting pluggable web servers). Pippo looks like a fit.

### Why CQEngine?
Provides a fast collection class with field indices and configurable transactions. Good for the purpose.

### Why Redis?
I have an extensive experience with Redis and trust it. Transaction support, not a simple key-value store, has different data structures and transaction support. Looks good for the purpose, not forgetting its single-threaded execution model. A natural fit to Lmax Disruptor

### Why RocksDB?
An embedded key-value store with transactions support and column families. Looks promising for the purpose.   

### Why Lmax Disruptor?
It is a framework that had amazed me, when I had first about it. It has many nice optimization approaches and frees you from hassles of classical jvm concurrency constructs. It looks like a great component of a Event-sourcing based system architecture.  

### What other alternatives came to my mind and go off?
* Should I use Akka? Maybe not, I haven't got extensive experience and not easy to optimize like Disruptor
* JVM concurrency constructs? Too unintuitive and difficult. Still I planned it in my todo list with a low priority
* What about a prod environment? I devised a system of _microservices_ with _Kafka_ as its backbone and _event sourcing_ as main architectural pattern. This service can run in several replicas, only one being master and others being ready to take over (you can check original lmax disruptor use case).  

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

You need to have JRE (or JDK) 1.8+ installed on your machine. You also need internet, so that gradle can download dependencies. Gradle will be installed with gradlew.

## Running The Tests And Building

_build_ task is declared as default, so a simple gradlew, under top project folder, will build and run tests :
```
./gradlew
```
build task will buld a lean jar. But, if you run the shadowJar task, you will have a big jar to execute anywhere :
```
./gradlew shadowJar
```
you can run the jar anywhere with JRE (located under build/libs) :
```
java -jar ewallet-service-all.jar
```

Hopefully, you will get a _build successful_ message as a result of all tests passing.
### Which Tests Are Written?
* __RepositoriesUnitTests__ : Parameterized unit tests for all alternative repositories in one class. Thanks to the common abstraction, a newly introduced datastore, will just require few lines of test configuration.
* __ControllerTests__ : Start a pippo server and hit it with requests for checking status codes, headers and response bodies. Service dependencies of controllers are mocked.
* __FunctionalTests__ : From service classes to repos or disruptor. Complementary tests to controller tests for whole system sanity checking.
* __PerformanceTest__ : End2End tests for measuring performance. Planned but not yet written. Create a multi-threaded perftest client making frequent transfers, comparatively rare account and transfer check

## Footnotes
* Lmax Disruptor solves concurrency issues by a single threaded processing of requests. However, an internal transfer may momentarily keep the system overall account in a non-consistent balance. Redis has some cures for that : transactions, pipelining, multi commands.
* System is not fail-proof to sudden shutdowns, but of course there are solutions fot that. As implied at the beginning, running multiple instances of the service, providing distributed coordination for a leader-follower relationship (throuh xookeeper, etcd, maybe hazelcast, or even redis) may provide a solution.
* Transfers are currently pushed into a collection inside accounts and as such they make fat accounts. This is actually good for checking sanity of each account. Validation snapshots may be a cure for fat accounts and older transfers may be transferred to another archive service.
* If you check my todo list, I have many tasks not accomplished in the given time. I would be very happy to submit the task with them, but I could hardly create time, really. Few examples of what I couldn't submit timely :
    * Multi-module gradle project
    * Docker-compose configurations for tests
    * Performance tests (I have very clear mind on how to do it, but I will only be able to write them later than submission due-date)
    * RocksDB as in-memory store alternative
    * JVM concurrency constructs for the business logic
    * A highly parameterized executable test client
    * API documentation by Swagger
     
