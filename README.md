# campsite-reservation


An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was
decided to open it up for the general public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it
was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST
API service that will manage the campsite reservations.
#### To streamline the reservations a few constraints need to be in place -
* The campsite will be free for all.
* The campsite can be reserved for max 3 days.
* The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
* Reservations can be cancelled anytime.
* For sake of simplicity assume the check-in & check-out time is 12:00 AM
#### System Requirements
* The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
* Provide an end point for reserving the campsite. The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
* The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation
* Due to the popularity of the campsite, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
* Provide appropriate error messages to the caller to indicate the error cases.
* The system should be able to handle large volume of requests for getting the campsite availability.
* There are no restrictions on how reservations are stored as as long as system constraints are not violated.

### URL to access Campsite Booking service: **http://localhost:9090/api/bookings/**

### To access project:
```git clone https://github.com/ayham1haddad/campsite-reservation.git
cd campsite-reservation
mvn package -DskipTests
java -jar target/campsite-0.0.1-SNAPSHOT.jar
```

#### Swagger UI
URL to access Swagger UI: **http://localhost:9090/swagger-ui.html**

### Test Results and Coverage

![Alt text](/image/coverage.png "Test Coverage")
![Alt text](/image/test_results.png "Test Results")

#### Simultaneous Bookings Test
To simulate Simultaneous bookings for same booking dates, I created three json with same booking dates and run it:

```Bash
$ curl -H "Content-Type: application/json" -d @booking-1.json http://localhost:9090/api/bookings & \
  curl -H "Content-Type: application/json" -d @booking-2.json http://localhost:9090/api/bookings & \
  curl -H "Content-Type: application/json" -d @booking-3.json http://localhost:9090/api/bookings & \
  curl -H "Content-Type: application/json" -d @booking-4.json http://localhost:9090/api/bookings &
```

```json
{"id":2,"email":"john.smith.3@email.com","firstName":"John","lastName":"Smith3","startDate":"2020-06-04","endDate":"2020-06-05","active":true}
{"status":"BAD_REQUEST","timestamp":"2020-05-30T17:36:41.161385","message":"No available dates available from 2020-06-04 to 2020-06-05"}
{"status":"BAD_REQUEST","timestamp":"2020-05-30T17:36:41.16143","message":"No available dates available from 2020-06-04 to 2020-06-05"}
{"status":"BAD_REQUEST","timestamp":"2020-05-30T17:36:41.161372","message":"No available dates available from 2020-06-04 to 2020-06-05"}
```

Bad request happens when the bookings are retried after CannotAcquireException gets thrown.This way the customer is getting a useful message as opposed to Http 500, Internal Server Error.

#### Load Testing of the System

Using ApacheBenchMark 
```Bash
$ ab -n 15000 -c 100 -k http://localhost:9090/api/bookings/available-dates
```
15000 in total for 100 at one time.


Output:
```
Benchmarking localhost (be patient)
Completed 1500 requests
Completed 3000 requests
Completed 4500 requests
Completed 6000 requests
Completed 7500 requests
Completed 9000 requests
Completed 10500 requests
Completed 12000 requests
Completed 13500 requests
Completed 15000 requests
Finished 15000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            9090

Document Path:          /api/bookings/available-dates
Document Length:        417 bytes

Concurrency Level:      100
Time taken for tests:   5.933 seconds
Complete requests:      15000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      7830000 bytes
HTML transferred:       6255000 bytes
Requests per second:    2528.02 [#/sec] (mean)
Time per request:       39.557 [ms] (mean)
Time per request:       0.396 [ms] (mean, across all concurrent requests)
Transfer rate:          1288.70 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    1   3.3      0     120
Processing:     3   38  25.9     36     232
Waiting:        0   37  25.8     35     232
Total:          3   39  25.9     36     233

Percentage of the requests served within a certain time (ms)
  50%     36
  66%     38
  75%     39
  80%     42
  90%     65
  95%     73
  98%    142
  99%    154
 100%    233 (longest request)
```

