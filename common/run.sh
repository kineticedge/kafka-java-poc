#!/bin/sh
set -e
gradle assemble > /dev/null

export CP=""
export CP="${CP}:/Users/buesing/workspaces/kineticedge/kafka-java-poc/common/build/classes/java/main"
export CP="${CP}:/Users/buesing/workspaces/kineticedge/kafka-java-poc/common/build/resources/main"
export CP="${CP}:/Users/buesing/.m2/repository/ch/qos/logback/logback-classic/1.2.10/logback-classic-1.2.10.jar"
export CP="${CP}:/Users/buesing/.m2/repository/com/beust/jcommander/1.78/jcommander-1.78.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/org.apache.kafka/kafka-streams/3.2.3/7c867ffc8239dcdb9d2b7013393e596752134f1b/kafka-streams-3.2.3.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.13.3/7198b3aac15285a49e218e08441c5f70af00fc51/jackson-annotations-2.13.3.jar"
export CP="${CP}:/Users/buesing/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.13.3/jackson-datatype-jsr310-2.13.3.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.13.3/56deb9ea2c93a7a556b3afbedd616d342963464e/jackson-databind-2.13.3.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.13.3/a27014716e4421684416e5fa83d896ddb87002da/jackson-core-2.13.3.jar"
export CP="${CP}:/Users/buesing/.m2/repository/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/org.apache.kafka/kafka-clients/3.2.3/ec1c16f07f8e5b99542a295a1034d9f71ecfef1f/kafka-clients-3.2.3.jar"
export CP="${CP}:/Users/buesing/.m2/repository/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
export CP="${CP}:/Users/buesing/.m2/repository/ch/qos/logback/logback-core/1.2.10/logback-core-1.2.10.jar"
export CP="${CP}:/Users/buesing/.m2/repository/com/github/luben/zstd-jni/1.5.2-1/zstd-jni-1.5.2-1.jar"
export CP="${CP}:/Users/buesing/.m2/repository/org/lz4/lz4-java/1.8.0/lz4-java-1.8.0.jar"
export CP="${CP}:/Users/buesing/.m2/repository/org/xerial/snappy/snappy-java/1.1.8.4/snappy-java-1.1.8.4.jar"
export CP="${CP}:/Users/buesing/.gradle/caches/modules-2/files-2.1/org.rocksdb/rocksdbjni/6.29.4.1/9f4019c5d8247b01eabc58bf0c3f34a904d65ca4/rocksdbjni-6.29.4.1.jar"

java -cp ${CP} io.kineticedge.poc.common.Main "$@"
