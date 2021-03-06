# Cache manager

The cache manager (cmanager) is a Java-based program which is able to manage GPX files and synchronize geocache logs from [Geocaching.com](https://geocaching.com) to [Opencaching.de](https://opencaching.de). It therefore loads a GPX file with the users cache founds (e.g. `myfounds.gpx`). After configuring an OKAPI token in the settings, the user is able to match his/her founds against caches listed on Opencaching.de.

Further information in German:

* https://wiki.opencaching.de/index.php/Cmanager
* https://forum.opencaching.de/index.php?topic=4348.0

## License & Source Code

`cmanager` is distributed under the terms of [The GNU General Public License v3](http://www.gnu.org/licenses/gpl-3.0-standalone.html). The sources are available [on GitHub](https://github.com/FriedrichFroebel/cmanager).

This version is a fork of the [original repository by Samsung1](https://github.com/RoffelKartoffel/cmanager). While the original version discontinued support for the log transfer functionality itself, this fork tries to keep it working. Additionally some problems of the original version have been fixed. Please note that I do not have any plans on implementing completely new features at the moment, but feel free to open a pull request.

## Distribution/Download

Releases are published on GitHub as ["Releases"](https://github.com/FriedrichFroebel/cmanager/releases).

Besides the regular release, there is a pre-release as well. This will be built by Travis CI on every change to the `master` branch. **Please note that these files (named `cm-<hexStringOfTheCommit>.jar`) are development versions which might have additional bugs or provide undocumented changes/features. Use them at your own risk!** The Travis integration is still work-in-progress, see [#7](https://github.com/FriedrichFroebel/cmanager/issues/7) for example.

## Building from Source

### Prerequisites

- Java development kit (JDK) in version >= 10. The minimum required Java version to run the application is Java 8 nevertheless, but the `Compatibility.java` file depends on a method introduced in Java 10.
- You need to provide API keys for compiling `cmanager`. See next section for details.

### API keys

Request your personal API keys for the supported [OpenCaching](http://www.opencaching.eu/) sites, currently:

* [opencaching.de OKAPI signup](https://www.opencaching.de/okapi/signup.html)

Copy [`templates/oc_okapi.properties`](https://github.com/FriedrichFroebel/cmanager/blob/master/templates/oc_okapi.properties) to the root directory of the Git repository. Then edit `oc_okapi.properties` and insert your keys.

### Building with Gradle

Run `gradle build` from the root directory of the Git repository (or use `./gradlew build` if you do not have Gradle installed locally).

### JAR

To create a JAR file, run `gradle jar` (or `./gradlew jar`). The JAR file will be located in `build/libs`.

### Java-independent packages

There is experimental support for bundling the application in a way that no local Java installation is needed for executing it.

To create the corresponding image, run `gradle jpackageImage` (or `./gradlew jpackageImage`). The image will be available inside the `build/jpackage/cmanager` directory. You might want to put this directory into a dedicated archive file for redistribution using the `jpackageImageZip` task. **Please note that this an incubating feature of Java 14, so at least Java 14 is required and this feature might break due to API changes.**

## Usage

### Prerequisites

- Java in version >= 8. I recommend you to use at least Java 10. If you use the `jpackage` image, no own Java installation is needed.

### Starting the application with Gradle

Run `gradle run` (or `./gradlew run`) from the root directory of the Git repository.

### Starting the application JAR file

Run `java -jar cm-0.4.0.jar` from the directory containing the JAR file.

### Starting the Java-independent package

Double-click on `cmanager.exe` (on Windows) from the directory containing this package. *Please note that this version is not being distributed in the release section at the moment.*

## Contributing

### Reporting Problems

Problems can be reported in either English or German, where the former is recommended on GitHub. Please provide the following details within the report:

* The used version of the application and where you got it from.
* The used operating system.
* The used Java version.
* The steps needed to reproduce this problem. If possible add a (minimal) GPX file (alternatively a pair of cache codes) to make it easier to investigate (you might remove personal information from the GPX file if you like).
* The error you get or the problem you have.

### Requesting Features

Feel free to request features, but please note that I do not have any plans on implementing completely new features at the moment. You may send a pull request if you have implemented an interesting feature.

### Pull Requests

Feel free to open a pull request to fix a problem yourself or to contribute a new feature. See the list of issues for possible changes. Please indicate your work on an issue by commenting on it.

The code should satisfy the following requirements:

* Everything should be written in English.
* The code style is based on the one of the Android Open Source project. The most important rules:

  * Use UTF-8 encoding.
  * Indent using 4 spaces.
  * Names should be in camelCase, `static final` variables should use UPPER_CASE, class names should be PascalCase.
  * Make as much variables `final` as possible.
  * Try to avoid abbreviated names for better readability.

  You may use the `googleJavaFormatter` (`goJF`) task to let the corresponding Gradle plugin do most of the work for you.

* Document your code.
* Provide dedicated tests if it makes sense.
* Try to avoid adding additional dependencies.
