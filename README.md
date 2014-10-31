yolo-lana
=========

Fun with Java 8 streams, filters, and Java Time.

Creates a basic Item class that has the capacity to store discounts and calculate "Red Line" special
discounts based upon the duraction, direction and size of cumulative discounts.

Maven Notes
-----------

The Maven [Cobertura plugin](http://mojo.codehaus.org/cobertura-maven-plugin/) and 
[Checkstyle](http://maven.apache.org/plugins/maven-checkstyle-plugin/index.html) currently don't
support Java 8. There are open tickets for both: [checkstyle](https://github.com/checkstyle/checkstyle/issues/10), [cobertura](http://jira.codehaus.org/browse/MCOBERTURA-187).

