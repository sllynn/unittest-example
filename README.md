## Databricks test skeleton

#### Example unit and integration tests for Databricks Spark Scala jobs

This is a sample scala project demonstrating:

- unit testing of individual  methods containing Spark transformations; and 
- integration testing of a basic pipeline which might consist of calls to many such methods.

The objective is to demonstrate how tests and code can be written in a developer's local environment and executed remotely.

#### Support for remote Continuous Integration pipelines

The project also contains a `Dockerfile` and `buildspec.yml` file that would be required to create an AWS CodeBuild CI project.

This could be set to trigger build and test stages every time code is checked in to a given branch of your pipeline's repository or, perhaps more likely, a pull request is raised

#### Pre-requisites

- Databricks Connect (see instructions [here](https://docs.databricks.com/dev-tools/databricks-connect.html) for installation advice)
- A compatible IDE (IntelliJ, VSCode, Eclipse)
- SBT 1.3.2
- Set the environment variable `SPARK_JARS` equal to the path returned by `databricks-connect get-jar-dir`
- Docker (if you want to test)

