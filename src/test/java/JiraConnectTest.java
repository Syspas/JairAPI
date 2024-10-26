package org.JiraApiClient;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraConnectTest {

    /** Путь к файлу конфигурации. */
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    /** Экземпляр класса JiraConnect для тестирования. */
    private JiraConnect jiraConnect;

    /** Файл конфигурации для проверки. */
    private File configFile;

    @BeforeEach
    void setUp() throws IOException {
        // Инициализация файла конфигурации
        configFile = new File(CONFIG_FILE);
        // Удаляем файл конфигурации, если он существует
        if (configFile.exists()) {
            configFile.delete();
        }
        // Создаем экземпляр JiraConnect
        jiraConnect = new JiraConnect();
    }

    @AfterEach
    void tearDown() {
        // Удаление файла конфигурации после тестов
        if (configFile.exists()) {
            configFile.delete();
        }
    }

    /**
     * Тест проверяет создание конфигурационного файла при его отсутствии.
     * Убедитесь, что файл создается с параметрами по умолчанию.
     */
    @Test
    void testConfigFileCreation() {
        // Проверяем, что файл конфигурации был создан
        assertTrue(configFile.exists(), "Файл конфигурации должен быть создан.");
    }

    /**
     * Тест проверяет загрузку параметров по умолчанию из конфигурационного файла.
     * Убедитесь, что значения соответствуют ожидаемым.
     */
    @Test
    void testDefaultProperties() {
        assertEquals("https://example.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("defaultUsername", jiraConnect.getJiraUsername());
        assertEquals("defaultApiToken", jiraConnect.getJiraApiToken());
    }

    /**
     * Тест проверяет, что метод loadConfiguration() корректно загружает параметры из файла.
     * Здесь используется мок для имитации загрузки конфигурации.
     *
     * @throws ConfigurationException если происходит ошибка конфигурации
     */
    @Test
    void testLoadConfiguration() throws ConfigurationException {
        // Создание мока для PropertiesConfiguration
        PropertiesConfiguration mockConfig = Mockito.mock(PropertiesConfiguration.class);

        // Создание мока для FileBasedConfigurationBuilder
        FileBasedConfigurationBuilder<PropertiesConfiguration> mockBuilder = Mockito.mock(FileBasedConfigurationBuilder.class);

        // Настройка поведения мок-объектов
        when(mockBuilder.getConfiguration()).thenReturn(mockConfig);
        when(mockConfig.getString("jira.url", "default")).thenReturn("https://mocked-url.atlassian.net");
        when(mockConfig.getString("jira.username", "default")).thenReturn("mockedUsername");
        when(mockConfig.getString("jira.api.token", "default")).thenReturn("mockedToken");

        // Инициализируем новый экземпляр JiraConnect, который будет использовать мок
        jiraConnect = new JiraConnect() {
        };

        // Проверка загруженных значений
        assertEquals("https://mocked-url.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("mockedUsername", jiraConnect.getJiraUsername());
        assertEquals("mockedToken", jiraConnect.getJiraApiToken());
    }

    /**
     * Тест проверяет, что создание конфигурационного файла работает корректно.
     * Убедитесь, что файл создается и содержит правильные значения.
     *
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Test
    void testCreateDefaultConfigFile() throws IOException {
        // Удаляем файл конфигурации, если существует
        if (configFile.exists()) {
            configFile.delete();
        }
        // Создание нового экземпляра JiraConnect, что вызовет создание файла конфигурации
        jiraConnect = new JiraConnect();

        // Проверяем, что файл конфигурации создан
        assertTrue(configFile.exists(), "Файл конфигурации должен быть создан.");
        // Проверяем, что файл содержит ожидаемые значения
        try (FileReader reader = new FileReader(configFile)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            boolean foundUrl = false;
            boolean foundUsername = false;
            boolean foundApiToken = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("jira.url=https://example.atlassian.net")) {
                    foundUrl = true;
                }
                if (line.contains("jira.username=defaultUsername")) {
                    foundUsername = true;
                }
                if (line.contains("jira.api.token=defaultApiToken")) {
                    foundApiToken = true;
                }
            }
            assertTrue(foundUrl, "Файл конфигурации должен содержать URL JIRA.");
            assertTrue(foundUsername, "Файл конфигурации должен содержать имя пользователя JIRA.");
            assertTrue(foundApiToken, "Файл конфигурации должен содержать токен API JIRA.");
        }
    }
}
