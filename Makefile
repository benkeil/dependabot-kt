clean:
	./gradlew clean

build:
	./gradlew shadowJar

version:
	./gradlew showVersion

publish-local:
	./gradlew publishPluginMavenPublicationToMavenLocal publishDependabotktPluginMarkerMavenPublicationToMavenLocal

publish:
	./gradlew publishPlugins
