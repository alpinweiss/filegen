# README #

This README would normally document whatever steps are necessary to get your application up and running.

### File Generator by Alpinweiss ###

* MS Excel, MS Access and plain file generator for testing purposes. The goal generate file containing some random data for third party app testing.
* Version 16.8.1

### Setup for development ###

* Install Java 7+
* Install Maven 3+
* run app from IDE or from command line

### Build excecutable jar ###

```bash
$ mvn package
```

All necessary configuration provided by default. All you need just build the app.

### How to use ###

```bash
$ java -jar filegen-XX.jar -i <input file name> -x
```

parameter list:

| Parameter | Attribute | Description | Example |
| -i | <input file name> | Input file name containing generation options | -i InputSample.xlsx |
| -x |||||


### Authors ###

* Alpinweiss SIA. info@alpinweiss.eu
