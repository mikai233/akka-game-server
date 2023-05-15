group = "com.mikai233"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(test.junit.bom))
    testImplementation(test.junit.jupiter)
    implementation(tool.reflections)
    implementation(tool.protobuf.kotlin)
    implementation(kt.reflect)
    implementation(akka.bundles.common)
    implementation(project(":common"))
    implementation(project(":proto"))
}

tasks.test {
    useJUnitPlatform()
}