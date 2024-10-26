// Указываем плагины, которые будут использоваться в проекте
plugins {
    id("java") // Плагин для поддержки Java, необходим для компиляции и выполнения Java-кода
}

// Установка группы и версии проекта
group = "org.JiraApiClient" // Группа артефактов, используется для идентификации проекта
version = "1.0-SNAPSHOT" // Версия проекта, используемая для управления версиями

// Конфигурация Java
java {
    sourceCompatibility = JavaVersion.VERSION_21 // Устанавливаем версию исходного кода Java
    targetCompatibility = JavaVersion.VERSION_21 // Устанавливаем целевую версию платформы Java
}

// Определение репозиториев для зависимостей
repositories {
    mavenCentral() // Основной репозиторий Maven Central для скачивания библиотек
    maven { url = uri("https://packages.atlassian.com/maven-public/") } // Репозиторий Atlassian для специфических библиотек
}

// Определение зависимостей проекта
dependencies {
    // --- Блок зависимостей для работы с Jira REST API ---
    implementation("com.atlassian.jira:jira-rest-java-client-api:5.2.7") // Основной API клиент для Jira
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.2.7") // Ядро клиента для работы с Jira
    implementation("io.atlassian.fugue:fugue:4.7.2") // Поддержка функциональных коллекций Atlassian

    // --- Блок зависимостей для работы с HTTP ---
    implementation("org.glassfish.jersey.core:jersey-client:2.35") // Асинхронный HTTP-клиент Jersey для взаимодействия с Jira API
    implementation("org.glassfish.jersey.core:jersey-common:2.35") // Общие библиотеки для Jersey

    // --- Блок зависимостей для логирования ---
    implementation("org.slf4j:slf4j-api:1.7.32") // API для логирования
    implementation("org.slf4j:slf4j-simple:1.7.32") // Простая реализация SLF4J для логирования

    // --- Блок зависимостей для работы с конфигурацией ---
    implementation("org.apache.commons:commons-configuration2:2.7") // Библиотека для работы с конфигурационными файлами
    implementation("commons-beanutils:commons-beanutils:1.9.4") // Библиотека для работы с JavaBeans

    // --- Блок зависимостей для тестирования ---
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0") // JUnit 5 для тестирования
    testImplementation("org.mockito:mockito-core:5.4.0") // Основная библиотека Mockito для создания моков
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0") // Интеграция Mockito с JUnit 5
}

// Настройка тестирования
tasks.withType<Test> {
    useJUnitPlatform() // Устанавливаем платформу JUnit 5 для запуска тестов
}

// Настройка компиляции Java
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Устанавливаем кодировку UTF-8 для компиляции Java файлов
    options.compilerArgs.add("-Xlint:deprecation") // Добавляем флаг для отображения предупреждений о депрекациях
}

// Настройка выполнения Java приложения
tasks.withType<JavaExec> {
    environment("file.encoding", "UTF-8") // Устанавливаем системную кодировку UTF-8 для запускаемых приложений
    jvmArgs = listOf("-Dfile.encoding=UTF-8") // Применяем кодировку UTF-8 для JVM
}

// --- Настройка задачи создания JAR ---
tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to "org.JiraApiClient.Main" // Указываем главный класс приложения для выполнения JAR файла
        )
    }
    from(sourceSets.main.get().allSource) { // Включаем все исходные файлы в JAR
        include("**/*.properties") // Включаем конфигурационные файлы .properties в JAR, если они необходимы для работы приложения
    }
}
