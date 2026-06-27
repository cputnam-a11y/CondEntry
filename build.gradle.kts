import jdk.internal.net.http.common.TimeSource.source

plugins {
    id("net.fabricmc.fabric-loom")
    `maven-publish`
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()
sourceSets {
    create("testmod") {
        compileClasspath += main.get().compileClasspath
        runtimeClasspath += main.get().runtimeClasspath
        compileClasspath += main.get().output
        runtimeClasspath += main.get().output
    }
}

repositories {
    exclusiveContent {
        forRepositories(maven("https://mvn.devos.one/releases/")).filter {
            includeModule("fish.cichlidmc", "tiny-json")
            includeModule("fish.cichlidmc", "fishflakes")
            includeModule("fish.cichlidmc", "tiny-codecs")
        }
    }
}

val condEntry = loom.mods.register("cond-entry") {
    sourceSet(sourceSets.main.get())
}
var testmod = loom.mods.register("testmod") {
    sourceSet(sourceSets.main.get())
}
loom {
    runs {
        create("testmod") {
            client()
            isIdeConfigGenerated = true
            name = "Testmod"
            source(sourceSets.named("testmod").get())
        }
    }
}
dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
    include(implementation("fish.cichlidmc:tiny-codecs:6.0.0")!!.also {
        condEntry.configure {
            dependency(it)
        }
        testmod.configure {
            dependency(it)
        }
    })
}

tasks.processResources {
    val version = version
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val projectName = project.name
    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}

// configure the maven publication
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
