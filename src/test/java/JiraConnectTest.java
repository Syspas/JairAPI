import org.JiraApiClient.JiraConnect;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Класс для тестирования функциональности класса {@link JiraConnect}.
 * <p>
 * Этот класс содержит тесты для проверки загрузки конфигурации, создания файла конфигурации
 * и корректности параметров по умолчанию.
 * </p>
 */
class JiraConnectTest {

    /** Путь к файлу конфигурации. */
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    /** Экземпляр класса JiraConnect для тестирования. */
    private JiraConnect jiraConnect;

    /** Файл конфигурации для проверки. */
    private File configFile;

    /**
     * Метод, выполняемый перед каждым тестом.
     * <p>
     * Здесь мы инициализируем экземпляр JiraConnect и создаем объект файла конфигурации.
     * </p>
     */
    @BeforeEach
    void setUp() {
        // Инициализация файла конфигурации
        configFile = new File(CONFIG_FILE);
        // Создание нового экземпляра JiraConnect
        jiraConnect = new JiraConnect();
    }

    /**
     * Метод, выполняемый после каждого теста.
     * <p>
     * Удаляет файл конфигурации после завершения тестов для обеспечения чистоты тестового окружения.
     * </p>
     */
    @AfterEach
    void tearDown() {
        // Удаление файла конфигурации, если он существует
        if (configFile.exists()) {
            configFile.delete();
        }
    }

    /**
     * Тест проверяет создание файла конфигурации.
     * <p>
     * Убедитесь, что файл конфигурации был успешно создан после инициализации JiraConnect.
     * </p>
     */
    @Test
    void testConfigFileCreation() {
        // Проверка существования файла конфигурации
        assertTrue(configFile.exists(), "Файл конфигурации должен быть создан.");
    }

    /**
     * Тест проверяет значения по умолчанию для параметров конфигурации.
     * <p>
     * Убедитесь, что значения URL, имени пользователя и токена API соответствуют ожидаемым значениям по умолчанию.
     * </p>
     */
    @Test
    void testDefaultProperties() {
        // Проверка значений по умолчанию
        assertEquals("https://example.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("defaultUsername", jiraConnect.getJiraUsername());
        assertEquals("defaultApiToken", jiraConnect.getJiraApiToken());
    }

    /**
     * Тест проверяет загрузку конфигурации из файла.
     * <p>
     * Здесь мы используем мок-объекты для имитации поведения класса PropertiesConfiguration
     * и проверки корректности загруженных параметров.
     * </p>
     *
     * @throws ConfigurationException если происходит ошибка конфигурации
     */
    @Test
    void testLoadConfiguration() throws ConfigurationException {
        // Создание мок-объектов для конфигурации
        PropertiesConfiguration mockConfig = Mockito.mock(PropertiesConfiguration.class);
        FileBasedConfigurationBuilder<PropertiesConfiguration> mockBuilder = Mockito.mock(FileBasedConfigurationBuilder.class);

        // Настройка поведения мок-объектов
        when(mockBuilder.getConfiguration()).thenReturn(mockConfig);
        when(mockConfig.getString("jira.url", "default")).thenReturn("https://mocked-url.atlassian.net");
        when(mockConfig.getString("jira.username", "default")).thenReturn("mockedUsername");
        when(mockConfig.getString("jira.api.token", "default")).thenReturn("mockedToken");

        // Переопределение метода loadConfiguration() для возврата мок-объекта
        jiraConnect = new JiraConnect() {
            @Override
            protected PropertiesConfiguration loadConfiguration() {
                return mockConfig;
            }
        };

        // Проверка значений, загруженных из конфигурации
        assertEquals("https://mocked-url.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("mockedUsername", jiraConnect.getJiraUsername());
        assertEquals("mockedToken", jiraConnect.getJiraApiToken());
    }

    /**
     * Тест проверяет, что файл конфигурации существует после создания экземпляра JiraConnect.
     * <p>
     * Этот тест гарантирует, что конфигурация загружается корректно и файл создается.
     * </p>
     *
     * @throws IOException если происходит ошибка ввода-вывода
     */
    @Test
    void testConfigurationFileExists() throws IOException {
        // Создание нового экземпляра JiraConnect
        jiraConnect = new JiraConnect();
        // Проверка существования файла конфигурации
        assertTrue(configFile.exists(), "Файл конфигурации должен существовать после создания экземпляра JiraConnect");
    }
}
