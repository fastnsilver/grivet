# Sample Workflow

Assuming you've already deployed this service locally...


## Register a type

### Request

```
curl -i -H "Content-Type: application/json" -X POST -d '{ "type": "TestType", "attributes": { "bigint": "bigint", "varchar": "varchar", "decimal": "decimal", "datetime": "datetime", "int": "int", "text": "text", "json": "json" } }' http://localhost:8081/type/register
```

### Response

```
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
X-Application-Context: application:h2
Location: /register/TestType
Content-Length: 0
Date: Wed, 29 Jul 2015 13:37:38 GMT
```

## Verify that the type was registered

### Request

```
curl -H "Content-Type: application/json" "http://localhost:8081/type/register/TestType" | python -mjson.tool
```

### Response

```
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   171  100   171    0     0  11132      0 --:--:-- --:--:-- --:--:-- 11400
{
    "attributes": {
        "bigint": "bigint",
        "datetime": "datetime",
        "decimal": "decimal",
        "int": "int",
        "json": "json",
        "text": "text",
        "varchar": "varchar"
    },
    "description": "",
    "type": "TestType"
}
```

## Check all registered types

### Request

```
curl -H "Content-Type: application/json" "http://localhost:8081/type/register/" | python -mjson.tool
```

### Response

```
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   308  100   308    0     0  10706      0 --:--:-- --:--:-- --:--:-- 11000
[
    {
        "attributes": {
            "artist": "varchar",
            "label": "varchar",
            "price": "decimal",
            "title": "varchar",
            "year": "int"
        },
        "description": "",
        "type": "Album"
    },
    {
        "attributes": {
            "bigint": "bigint",
            "datetime": "datetime",
            "decimal": "decimal",
            "int": "int",
            "json": "json",
            "text": "text",
            "varchar": "varchar"
        },
        "description": "",
        "type": "TestType"
    }
]
```


## Store a type

### Request

```
curl -i -H "Content-Type: application/json" -X POST -d '{ "artist": "Rush", "year": 1981, "price": 9.99, "label": "Anthem", "title": "Moving Pictures"}' http://localhost:8081/type/store/Album
```

### Response

```
HTTP/1.1 204 No Content
Server: Apache-Coyote/1.1
X-Application-Context: application:h2
Date: Wed, 29 Jul 2015 14:25:18 GMT
```


## Verify that the type was stored 

In this example request results constrained by `createdTime`

### Request

```
curl -H "Content-Type: application/json" "http://localhost:8081/type/store/Album" | python -mjson.tool
```

### Response

```
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    87  100    87    0     0   1650      0 --:--:-- --:--:-- --:--:--  1673
[
    {
        "artist": "Rush",
        "label": "Anthem",
        "price": 9.99,
        "title": "Moving Pictures",
        "year": 1981
    }
]
```

## Token Management

In this example we will visit the `/oauth/token` endpoint to get authenticate and receive an authorization token that has a finite time-to-live.  Then we'll verify who the currently 
authenticated user is.  Assume the following Spring Profiles are activated `h2,secure,https`.


### Request

```
curl -k -v -X POST \
  -H "Origin: https://localhost:8443" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&username=<usename>&password=<password>" \
  https://localhost:8443/oauth/token
```

replace `<username>` and `<password>` above with valid Stormpath credentials

### Response

```
{
    "access_token" : "<your_access_token>",
    "refresh_token": "<your_refresh_token>",
    "token_type" : "Bearer",
    "expires_in" : 3600
}
```

`access_token` and `refresh_token` above represent encrypted Strings (and are much longer)


Once you have have the `access_token`, you can verify a subset of account info for the current user.

### Request

```
curl \
  -k -H "Authorization: Bearer <your_access_token>" \
  https://localhost:8443/me
```

### Response

```
{
    "href" : "https://api.stormpath.com/v1/accounts/<account_id>",
    "fullName" : "My Full Name",
    "email" : "my@email.com"
}
```

Note: the above response is contrived, but is representative of the type of information you will see.


From here on out (for as long as the `access_token` is active) you can make similar requests of all
other available Grivet endpoints, so long as the account is authorized to do so.
