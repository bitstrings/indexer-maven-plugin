## indexer-maven-plugin

```xml
<plugin>
    <groupId>org.bitstrings.maven.plugins</groupId>
    <artifactId>indexer-maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <execution>
            <id>index-res</id>
            <goals>
                <goal>index</goal>
            </goals>
            <configuration>
                <indexes>
                    <index>
                        <directory>${project.build.outputDirectory}/res</directory>
                    </index>
                </indexes>
            </configuration>
        </execution>
    </executions>
</plugin>

```
