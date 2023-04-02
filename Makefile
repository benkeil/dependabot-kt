clean:
	./gradlew clean

build:
	./gradlew shadowJar

rm:
	rm -rf /Users/ben/.m2/repository/de/benkeil

publish-local:
	./gradlew publishPluginMavenPublicationToMavenLocal publishDependabotktPluginMarkerMavenPublicationToMavenLocal

publish-github:
	./gradlew publishPluginMavenPublicationToGithubRepository publishDependabotktPluginMarkerMavenPublicationToGithubRepository

publish-plugin:
	./gradlew publishPlugins
