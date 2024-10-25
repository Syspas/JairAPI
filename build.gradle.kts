plugins {
    java // Плагин для поддержки Java
}

group = "org.JiraApiClient" // Группа артефактов
version = "1.0-SNAPSHOT" // Версия проекта

java {
    sourceCompatibility = JavaVersion.VERSION_21 // Версия исходного кода
    targetCompatibility = JavaVersion.VERSION_21 // Версия целевой платформы
}

repositories {
    mavenCentral() // Основной репозиторий Maven Central
    maven { url = uri("https://packages.atlassian.com/maven-public/") } // Репозиторий Atlassian
    maven { url = uri("https://repository.atlassian.com/maven-external/") } // Альтернативный Atlassian репозиторий
}


dependencies {
    // Зависимости для работы с Jira REST API
    implementation("com.atlassian.jira:jira-rest-java-client-api:5.2.7")
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.2.7")
    implementation("io.atlassian.fugue:fugue:4.7.2") // Версия может варьироваться

    // Асинхронный HTTP-клиент Jersey для работы с Jira API
    implementation("org.glassfish.jersey.core:jersey-client:2.35")
    implementation("org.glassfish.jersey.core:jersey-common:2.35")

    // Логирование с использованием библиотеки SLF4J
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("org.slf4j:slf4j-simple:1.7.32")
}

tasks.withType<JavaCompile> {
    // Настройка компиляции с указанием кодировки UTF-8
    options.encoding = "UTF-8"
    // Добавление аргумента компилятора для отображения предупреждений о депрекациях
    options.compilerArgs.add("-Xlint:deprecation")
}
