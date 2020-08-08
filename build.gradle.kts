import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"

    id("nebula.release") version "15.1.0"
    signing
    `maven-publish`
}

group = "com.bnorm.junit5.contingent"

val release = tasks.findByPath(":release")
release?.finalizedBy(tasks.publish)

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation("org.junit.platform:junit-platform-launcher:1.6.2")
    testImplementation("org.junit.platform:junit-platform-testkit:1.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform {
        // Do not run the template files
        exclude("**/*Template.class")
    }
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka"
}
tasks.assemble { dependsOn(tasks.dokka) }

signing {
    val signingKey = findProperty("signingKey") as? String
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, "")
    }

    setRequired(provider { gradle.taskGraph.hasTask("release") })
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("Allows for JUnit 5 tests to be contingent on the success of other tests")
                url.set("https://github.com/bnorm/junit5-contingent")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/bnorm/junit5-contingent/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/bnorm/junit5-contingent")
                    connection.set("scm:git:git://github.com/bnorm/junit5-contingent.git")
                }
                developers {
                    developer {
                        name.set("Brian Norman")
                        url.set("https://github.com/bnorm")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url = when {
                    "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                    else -> property("sonatypeReleaseUrl")
                } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}
