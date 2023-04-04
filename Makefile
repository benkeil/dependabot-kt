clean:
	./gradlew clean

build:
	./gradlew shadowJar

version:
	./gradlew showVersion

publish-local:
	./gradlew publishPluginMavenPublicationToMavenLocal publishDependabotktPluginMarkerMavenPublicationToMavenLocal

publish-github:
	./gradlew publishAllPublicationsToGithubRepository

publish-plugin:
	./gradlew publishPlugins

publish:
	./gradlew publishAllPublicationsToGithubRepository publishPlugins
