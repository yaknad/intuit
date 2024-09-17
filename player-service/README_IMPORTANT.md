# Read Me First - Assumptions, Explanations etc.

## Personal Note
* Please forgive my rusty Java ;) I haven't written much Java during the last 5 years, besides a handful of automation tests.
* I used Spring Boot as a platform for the REST service, but as I don't use Spring occasionally, I guess there are quite a few 
  Spring attributes and syntactic sugars I forgot / don't know about and wrote instead some longer, redundant code.

### General Approaches towards the assignment
### Approach 1:
* This is a very simple task. for each api request, open the file and serve either all of it or search for the specific required id.
* Indeed this is not efficient since IO is expensive (and file system is the most expensive IO), but still lets keep it simple.
* This approach has a great advantage handling changes on the file, since for each request it serves the up-to-date file version
* I can improve performance for the GET by id endpoint since the file is sorted by the players ids. It allows me to stop searching when the current line's 
playerID is greater (lexicographically) then the requested playerID. 
* yet another theoretical performance improvement (which I haven't implemented): since the file is sorted, I can implement a binary search on the file IDs, 
while going forth and back on the Stream position (reset, skip) until reaching the requested line (or finding it doesn't exist). 
Indeed, since I don't know how long is each line, I'll have to traverse the whole file once and keeping in memory the length of each line.

##### Approach 1 PROS:
* Simple
* You get the up-to-date version on every read.
##### Approach 1 CONS:
* disk IO heavy
* If the file is exclusively locked by another user, the microservice will not be able to read its contents

*********************************************
## What I've implemented ##
**I've implemented Approach 1, including:**
**unit-tests, exception handling, a few edge cases, dockerfile / docker-compose.**
**If I had more time, I would implement some integration tests to verify the system behavior end to end, and I would fix the playerDto serialization (handle nulls better)**
**The time didn't allow me to continue to approach 2, which has better performance but many more complexities (as described below)**
*********************************************


### Approach 2:
* to improve performance, load the file to a cache (see below what are the options) and serve it from there.
* also the GET by id endpoint will perform better if we search on a DB / cache instead of on the file system.

#### Database / Cache - why and which?
* We don't want to serve the clients requests directly from the file as IO is very costly.
* We should also not store the whole file's content in the service's memory since:
* * The player.csv file may grow very large
* * The microservice should be scalable. we may need to deploy several instances in order to handle large numbers of requests.
Microservice may reside in kubernetes and its memory capacity may be not very high. 
So the Conclusion is: We don't want to hold the whole file in memory.
* We'll read the file contents to a DB and serve from there to the clients.
* When serving database objects to the client, we will separate the DB entities from the DTO models we send to the client, as their constraints may differ.
* **Database options are:**
* most basic (which I'll choose as the time is short) - RDBMS (postgres) to store the file contents in a reliable place (ACID).
Although RDBMS is eventually also file-system-backed, but it's much cheaper to access then the actual file system
due to better caching
* next stage, will be small in memory local cache in each microservice instance to cache the latest /api/players/{playerID}
requests that may re-occur in a small period of time
* next level is to store the contents in redis - a fast distributed cache. The services will be able to retrieve the data from redis
and only in case of redis failure (service is down etc.), they will use the DB as a backup, while redis is being re-uploaded the data from 
the RDBMS.
* Besides caching, add also pagination, so the client will be able to get a subset of players

#### File Storage
* The file may reside in various file systems. I'll assume a simple NTFS as I don't have the time to use S3 or similar cloud products.
We should build it using interfaces in a way that will easily let us use various file systems "behind" the interface in a simple unified way.
* I'll start with a simple scenario of a file that does not change, and we only need to upload it to a DB / cache (see later)
later on we'll discuss how to handle possible file changes

#### Initial file read
* although looking simple enough - just read the file on service init, this is actually also challenging.
* when we have multiple instances of this microservice, we don't want each instance to read the file to the DB when the service initializes.
* the solution is to trigger file reads by a message queue (kafka, rabbit etc.). Only a single instance will handle the message and therefore 
the other instances will not automatically read the file when they init. (consider using Outbox pattern if required to make sure "Exactly Once" handling).
* Who will set the initial read message? It should probably be the deployment process's responsibility.
* I don't believe I will have the time to implement a full deployment process in this scope. I hope I'll at least have the time to build a reliable messaging system for file read trigger.
* both kafka and rabbit has their pros and cons, and maybe for a small scale like here (if we handle a single file) rabbit is the better choice, but as I find Kafka more
familiar and quicker to use (for me), I'll choose kafka.

#### File Changes
* Next stage - if I'll have time for it, is to handle file changes to update the DB / cache.
* I'll use a simple file system watcher, as I don't have the time for handling more sophisticated tools.
* There are quite a few challenges regarding file changes:
* * detecting file changes
* * detecting what exactly has changed - was it a new added row or was an exiting row updated? and how to do it in an efficient way -
    comparing the old content to the new content...
* * make sure only a single service will handle these changes (see next paragraph that there may be multiple instances to this service),
    as we don't want to corrupt the data we save in the DB (next paragraph) by multiple handlers writing duplicated data to the DB.
    to solve this challenge we can leverage the same message queue from the former paragraph (used for initial file read) also to send
    update messages which will be handled by a single consumer.
* * But even before this message queue, we need to make sure that only a single service instance is running the file system watcher, so we get a single event
for each change. To achieve that, we need to use a distributed lock that prevents the rest of the services from entering the watcher code. When the active watcher 
exits the code block (shutting down or other reason), another service instance will take its place

#### File Validation
* I assume there's no need to validate the file contents (e.g. a month is only a number between 1-12) since this is the client's input and that's what we need to expose.

#### Service Security
* Since wasn't explicitly asked for, and the time shortage I've chosen not to implement api security of any kind. 
* In a real world we should make sure the api clients have the permission to access this service and read the file's contents.

#### Data integrity
* From a quick overview of the player.csv file, I've seen that most of the fields are nullable (besides the playerId and the bbrefID).
* The dates fields are in a mixture of formats and therefore in order to make things simpler, I've stored them as Strings.
If there's a requirement, we'll have to implement a parsing mechanism that handles multiple string formats conversion to Date. 