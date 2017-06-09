# akka-todo

An akka based todo list system. Allows you to create and query todo lists.

It also uses [Dropwizard](http://dropwizard.io) to expose the system API (through HTTP)

## Building

To compile and run the unit tests:

```bash
$ ./gradlew clean check
```

## Running

Create an executable jar by running the following command:

```bash
$ ./gradlew fatCapsule
```

This should create a single jar file in the project's `/build/libs` folder. You can then run that jar by doing:

```bash
$ java -jar /build/libs/akka-todo-capsule.jar server conf/app.yaml
```

By default, the API runs on port `8080`. You can override this by modifying the configuration file (`conf/app.yaml`).

## API

`GET /todo`

Retrieves todo entries from a list by date. It accepts the following query parameters:

* title - the todo list title
* date - the date in the yyyy-MM-dd format

It replies with a json representation of the entries.

Example:

```
$ curl "http://localhost:8080/todo?title=AliceTasks&date=2016-02-01"

# entries from Alice's task list for 2016-02-01
{
    "title": "AliceTasks",
    "date": "2016-02-01",
    "entries": []
}
```

`POST /todo`

Creates an entry in a list for a date. It accept a json body with the following format:

```json
{
  "title": "list title",
  "date": "date in yyyy-MM-dd format",
  "entry": "actual entry"
}
```

Example:

```
$ curl -X POST \
    -H 'Content-Type: application/json' \
    -d '{"title": "AliceTasks", "date": "2016-02-01", "entry": "Fix some bugs"}' \
    http://localhost:8080/todo

$ curl "http://localhost:8080/todo?title=AliceTasks&date=2016-02-01"
{"title":"AliceTasks","entries":["Fix some bugs","Fix some bugs"],"date":"2016-02-01"}
```
