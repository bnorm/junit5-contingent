# junit5-contingent

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bnorm.junit5.contingent/junit5-contingent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bnorm.junit5.contingent/junit5-contingent)

Allows for JUnit 5 tests to be contingent on the success of other tests.

## Why?

Unit tests can fail for all sorts of reasons. Sometimes a multitude of failures
can hide the unit test failures which expose the actual bug. If unit tests,
which implicitly depended on functionality tested by other unit tests, failed
early with a clear message what implicit dependency failed, then it could be
easier pinpoint the actual problem. That's the problem this library is
attempting to solve.

Also, I wanted to see if it could be done using JUnit 5 extensions. Yeah,
mainly this reason.

## Example

In the following example, tests `required1` and `required2` will be executed
first. If none of them fail, then tests `dependent1` and `dependent2` will be
allowed to run.

```java
@ContingentExtension
class ContingentTests {
    @Test @Tag("required")
    public void required1() {}

    @Test @Tag("required")
    public void required2() {}

    @Test @Contingent("required")
    public void dependent1() {}

    @Test @Contingent("required")
    public void dependent2() {}
}
```

## What's Next?

- API: I'm not happy with the use of Tags to mark required tests. I don't like
  having to match raw strings for creating this link. The whole API layer could
  be greatly improved.

- Cross-Class Linking: Right now unit tests are only linked if they exist in
  the same class. It would be great if they could be linked for all discovered
  unit tests.
