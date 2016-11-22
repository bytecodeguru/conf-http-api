conf-http-api
=============

An HTTP API for managing the configuration values of a system.

Here is an example configuration value:

```json
{
    "id": "foo",
    "name": "Configuration for Foo",
    "value": "This is the value for configuration Foo"
}
```

User stories
------------

These are just a few of user stories to help you get going and to give you an idea of the sort of thing we're asking for. More specific requirements can be found below in the next section.

```
Given a value has been set for the configuration id "foo"
When the API receives a GET request with path "/foo"
Then the API should return the value for the configuration id "foo"


Given a value has been set for the configuration id "foo"
When the API receives a PUT request with path "/foo"
Then the API should verify the body of the request is valid
And it should update the value for the configuration id "foo"

Given a value has been set for two configuration ids "foo" and "bar"
When the API receives a GET with path "/"
Then the API should return the values for both "foo" and "bar"
```

Requirements
------------

- [x] The service should be HTTP-based but doesn't have to be publicly available, as long as we can run it locally
- [x] The service should be simple to run locally so we can manually verify its behavior
- [x] The service should let you create/read/update/delete configurations
- [x] The resources should be served in JSON format
- [x] You can use a programming language of you choice
- [x] You can use any library or framework, but be prepared to justify your decision
- [x] You can use any type of storage to persist the resources. It’s ok if resources are lost when the API is restarted (i.e. you can store them in memory if you like)
- [x] Please add any additional functionality or behavior that you feel is important to meet the requirements
- [x] Please also ensure you have some form of tests (whether they be unit or integration we'll leave it up to you to decide)
- [x] If there are things you wanted to add but didn’t have enough time, mention them (somewhere, your choice)

Build instructions
------------------

To build a runnable jar:
```
mvn clean package
```

To build a runnable docker image:
```
mvn clean package docker:build
```

Run istructions
---------------

Example:
```
java -jar target/conf-http-api-<version>.jar
```

Example using Docker:
```
docker run -p 127.0.0.1:8080:8080 conf-http-api
```

Usage
-----

### Get all configurations
```
curl -v 'http://localhost:8080/'
```

### Get configuration with id 1
```
curl -v 'http://localhost:8080/1'
```

### Add new configuration
```
curl -v -XPOST -H "Content-type: application/json" -d '{"id":"1","name":"foo","value":"bar"}' 'http://localhost:8080/'
```

### Update configuration with id 1
```
curl -v -XPUT -H "Content-type: application/json" -d '{"id":"1","name":"bar","value":"baz"}' 'http://localhost:8080/1'
```

### Delete configuration with id 1
```
curl -v -XDELETE 'http://localhost:8080/1'
```



Implementation notes
--------------------

Useful missing features:
- [ ] Filters
- [ ] Sorting
- [ ] Pagination
- [ ] Stricter validation of json
