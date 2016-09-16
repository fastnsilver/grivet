## Endpoints

### Type Registration

* GET `/register?showAll`

  * returns all registered types

* GET `/register/{type}`

  * returns the registered type

* POST `/register`

  * Sample POST request [TestType.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestType.json)

* POST `/register/types`

  * Sample POST request [TestMultipleType.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestMultipleType.json)

* POST `/schema`

  * links a JSON Schema with a pre-registered type; subsequent `/store/{type}` requests will be validated against schema

  * Sample POST request [TestTypeSchema.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestTypeSchema.json)

* DELETE `/schema/{type}`

  * unlinks existing JSON Schema from a pre-registered type; subsequent `/store/{type}` requests will NOT be validated against schema

* DELETE `/register/{type}`

  * deletes a registered type; default configuration has cascading deletes enabled which means that any persistent data from prior POST `/store/{type}` requests will also be deleted; so use with caution!

  * However, if `spring.profiles.active` is set to `mysql` then when a DELETE request is made only the Class from the `class` table is deleted which orphans entries in other tables


### Type Storage and Retrieval

* POST `/store/{type}`

  * Sample POST request [TestTypeData.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestTypeData.json)

* POST `/store/{type}/batch`

  * Sample POST request [TestMultipleContactsData.json](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestMultipleContactsData.json)

* GET `/store/{type}`

  * returns records that were created within the last 7 days

* GET `/store/{type}?createdTimeStart=yyyy-MM-ddTHH:mm:ss&createdTimeEnd=yyyy-MM-ddTHH:mm:ss`

  * returns records that were created between `createdTimeStart` and `createdTimeEnd`

* GET `/store?oid={oid}`
  
  * returns record(s) that match the object identifier

* PATCH `/store?oid={oid}`

  * update one or more existing type's attribute-value(s)
  
* GET `/store/{type}/noAudit`

  * return all records for type. No audit trail, only most recent records.

#### Named Queries

You may wish to review the ER diagram below to fully leverage this feature.  Currently, you are limited to registering and executing `SELECT` queries and `CALL`ing stored procedures.  

* POST `/namedQuery`

  * Sample POST requests:

    * [SELECT](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestSelectQuery.json)
    * [CALL](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/TestSprocQuery.json)

      * Consult this [Stored Procedure](https://github.com/fastnsilver/grivet/blob/master/core/test-resources/src/main/resources/db/hsqldb/V1_1__add_test_sproc.sql) example for a sample HSQLDB based implementation

* DELETE `/namedQuery/{name}`

  * deletes the named query

* GET `/namedQuery?showAll`

  * returns all named queries; displays: name, type, query, and parameters

* GET `/namedQuery/{name}`

  * executes a named query that does not require parameters

* GET `/namedQuery/{name}?<parameter_key1>=<parameter_value1>&<parameter_key2>=<parameter_key2>...`

  * executes a named query consuming the parameters supplied in request

##### Example

* GET `/namedQuery/getAttributesCreatedBefore?createdTime=2015-10-01T00:00:00`

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

* GET `/store/TestType?c=datetime|lessThan|2015-07-01T10:00:00`

  * returns `TestType` records that have attribute `datetime` less than the day constraint value

* GET `/store/TestType?constraint=datetime|lessThan|2015-07-01T10:00:00&constraint=varchar|equals|Rush`

  * returns `TestType` records that match on either `datetime` `OR` `varchar` attributes and constraint values
