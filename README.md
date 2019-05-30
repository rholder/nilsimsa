[![Build Status](http://img.shields.io/travis/rholder/nilsimsa.svg)](https://travis-ci.org/rholder/nilsimsa) [![Latest Version](http://img.shields.io/badge/latest-1.0.0-brightgreen.svg)](https://github.com/rholder/nilsimsa/releases/tag/v1.0.0) [![License](http://img.shields.io/badge/license-apache%202-brightgreen.svg)](https://github.com/rholder/nilsimsa/blob/master/LICENSE)

## What is this?
The `nilsimsa` module is an implementation of an existing [locality-sensitive hashing](http://en.wikipedia.org/wiki/Locality-sensitive_hashing)
algorithm designed specifically to handle spam filtering. LSH is a method
of performing probabilistic dimension reduction of high-dimensional data. The
basic idea is to hash the input items so that similar items are mapped to the
same buckets with high probability (the number of buckets being much smaller
than the universe of possible input items). This is different from conventional
hash functions, such as those used in cryptography, because in this case the
goal is to maximize the probability of a collision of similar items rather than
avoid collisions.

As per the original description [here](http://ixazon.dynip.com/~cmeclax/nilsimsa.html):
> A nilsimsa code is something like a hash, but unlike hashes, a small
> change in the message results in a small change in the nilsimsa code.
> Such a function is called a locality-sensitive hash.

## Maven
```xml
<dependency>
    <groupId>com.github.rholder</groupId>
    <artifactId>nilsimsa</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Gradle
```groovy
compile "com.github.rholder:nilsimsa:1.0.0"
```

## Quickstart
A minimal sample of some of the functionality would look like:

```java
String first  = new Nilsimsa().update("potatoes are the best".getBytes()).toHexDigest();
String second = new Nilsimsa().update("tomatoes are really the best".getBytes()).toHexDigest();
String third  = new Nilsimsa().update("bananas taste pretty good".getBytes()).toHexDigest();

System.out.println(Nilsimsa.compare(first, third));   //   3
System.out.println(Nilsimsa.compare(second, third));  //  -6
System.out.println(Nilsimsa.compare(first, second));  //  53 -- closest match
System.out.println(Nilsimsa.compare(first, first));   // 128 -- exact match
```

## Building from source
The nilsimsa module uses a [Gradle](http://gradle.org)-based build system. In the instructions
below, [`./gradlew`](http://vimeo.com/34436402) is invoked from the root of the source tree and serves as
a cross-platform, self-contained bootstrap mechanism for the build. The only
prerequisites are [Git](https://help.github.com/articles/set-up-git) and JDK 1.6+.

### check out sources
`git clone git://github.com/rholder/nilsimsa.git`

### compile and test, build all jars
`./gradlew build`

### install all jars into your local Maven cache
`./gradlew install`

## License
This project is a Java port of `py-nilsimsa` which is MIT/X11 licensed.
The `nilsimsa` module is released under version 2.0 of the
[Apache License](http://www.apache.org/licenses/LICENSE-2.0).

## References
* http://ixazon.dynip.com/~cmeclax/nilsimsa.html
* https://code.google.com/p/py-nilsimsa/
* http://en.wikipedia.org/wiki/Locality-sensitive_hashing
* http://en.wikipedia.org/wiki/Nilsimsa_Hash
