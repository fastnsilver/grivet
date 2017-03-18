## Endpoints

### Type Registration

* GET `/api/v1/definitions`

  * returns all registered types

* GET `/api/definition/{type}`

  * returns the registered type

* POST `/api/v1/definition`

  * Sample POST request [TestType.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestType.json)

* POST `/api/v1/definitions`

  * Sample POST request [TestMultipleType.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestMultipleType.json)

* POST `/api/v1/schema`

  * links a JSON Schema with a pre-registered type; subsequent `/store/{type}` requests will be validated against schema

  * Sample POST request [TestTypeSchema.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestTypeSchema.json)

* DELETE `/api/v1/schema/{type}`

  * unlinks existing JSON Schema from a pre-registered type; subsequent `/store/{type}` requests will NOT be validated against schema

* DELETE `/api/v1/definition/{type}`

  * deletes a registered type; default configuration has cascading deletes enabled which means that any persistent data from prior POST `/store/{type}` requests will also be deleted; so use with caution!

  * However, if `spring.profiles.active` is set to `mysql` then when a DELETE request is made only the Class from the `class` table is deleted which orphans entries in other tables


### Type Storage and Retrieval

* POST `/api/v1/type`

  * Sample POST request [TestTypeData.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestTypeData.json)
  * You are required to send a header named `Type` whose value must match a pre-registered type definition. For the sample case you would send `Type: TestType`.

* POST `/api/v1/types`

  * Sample POST request [TestMultipleContactsData.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestMultipleContactsData.json)
  * You are required to send a header named `Type` whose value must match a pre-registered type definition. For the sample case you would send `Type: Contact`.

* GET `/api/v1/type/{type}`

  * returns records that were created within the last 7 days

* GET `/api/v1/type/{type}?createdTimeStart=yyyy-MM-ddTHH:mm:ss&createdTimeEnd=yyyy-MM-ddTHH:mm:ss`

  * returns records that were created between `createdTimeStart` and `createdTimeEnd`

* GET `/api/v1/type?oid={oid}`
  
  * returns record(s) that match the object identifier
  
* DELETE `/api/v1/type?oid={oid}`
  
  * deletes records (including attribute-values) that match the object identifier

* PATCH `/api/v1/type?oid={oid}`

  * update one or more existing type's attribute-value(s)
  
* GET `/api/v1/type/{type}?noAudit=true`

  * return all records for type. No audit trail, only most recent records.

#### Named Queries

You may wish to review the ER diagram below to fully leverage this feature.  Currently, you are limited to registering and executing `SELECT` queries and `CALL`ing stored procedures.  

* POST `/api/v1/query`

  * Sample POST requests:

    * [SELECT](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestSelectQuery.json)
    * [CALL](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestSprocQuery.json)

      * Consult this [Stored Procedure](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/db/hsqldb/V1_1__add_test_sproc.sql) example for a sample HSQLDB based implementation

* DELETE `/api/v1/query/{name}`

  * deletes the named query

* GET `/api/v1/queries`

  * returns all named queries; displays: name, type, query, and parameters

* GET `/api/v1/query/{name}`

  * executes a named query that does not require parameters

* GET `/api/v1/query/{name}?<parameter_key1>=<parameter_value1>&<parameter_key2>=<parameter_key2>...`

  * executes a named query consuming the parameters supplied in request

##### Example

* GET `/api/v1/query/getAttributesCreatedBefore?createdTime=2015-10-01T00:00:00`

  * will execute the named query `getAttributesCreatedBefore` consuming the `createdTime` parameter value

Note: in case of calling a stored procedure, the stored procedure must already exist in the database!

![this ER diagram](images/er-diagram.png "ER Diagram")


#### Dynamic Queries

Dynamic queries are great to get started with, but often you will want more flexibility over query structure.  For those situations look into employing a Named Query.

To retrieve a type by any of its attributes you may specify one or more request parameters for query constraints of the form:

* `<constraint_key>=<attribute_name>|<operator>|<value>|<conjunction>`

where

* `<constraint_key>` may be `c` or `constraint`
* `<attribute_name>` is a registered attribute of the type
* `<operator>` is one of [Operator](https://github.com/fastnsilver/grivet/blob/master/core/services/src/main/java/com/fns/grivet/query/Operator.java)
* `value` is (depending upon the `operator`) either a single value or a comma-separated list of values
* `conjunction` is one of [Conjunction](https://github.com/fastnsilver/grivet/blob/master/core/services/src/main/java/com/fns/grivet/query/Conjunction.java); this value is optional and if not defined then (for more than one constraint) each constraint is `OR`ed. All conjunctions must be homogenously defined!

##### Examples

* GET `/api/v1/type/TestType?c=datetime|lessThan|2015-07-01T10:00:00`

  * returns `TestType` records that have attribute `datetime` less than the day constraint value

* GET `/api/v1/type/TestType?constraint=datetime|lessThan|2015-07-01T10:00:00&constraint=varchar|equals|Rush`

  * returns `TestType` records that match on either `datetime` `OR` `varchar` attributes and constraint values
