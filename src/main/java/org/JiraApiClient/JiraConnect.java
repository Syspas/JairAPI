/**
 * Класс для извлечения данных из конфигурационного файла JIRA.
 * <p>
 * Этот класс загружает параметры подключения к JIRA из конфигурационного файла формата properties.
 * Если файл отсутствует, он автоматически создается с параметрами по умолчанию.
 * Конфигурационный файл имеет кодировку UTF-8.
 * </p>
 *
 * <p><b>Перспективы развития:</b></p>
 * <ul>
 *   <li><b>Шифрование конфиденциальных данных:</b> добавить поддержку шифрования для полей, содержащих чувствительные данные,
 *   таких как имя пользователя и токен API, для повышения безопасности.</li>
 *   <li><b>Динамическое обновление конфигурации:</b> реализовать механизм динамической подгрузки настроек во время работы приложения,
 *   чтобы изменения в конфигурации не требовали перезагрузки.</li>
 *   <li><b>Валидация значений конфигурации:</b> добавить проверки для URL и токена API, что повысит устойчивость к некорректным данным.</li>
 *   <li><b>Логирование событий:</b> интегрировать логирование для отслеживания этапов загрузки и сохранения конфигурации, а также для вывода ошибок.</li>
 *   <li><b>Поддержка нескольких конфигурационных файлов:</b> внедрить поддержку различных профилей (например, development, production) для удобства управления конфигурацией.</li>
 *   <li><b>Кэширование параметров:</b> кэшировать параметры конфигурации, чтобы снизить нагрузку на файловую систему и улучшить производительность.</li>
 * </ul>
 *
 * @version 1.0
 */
package org.JiraApiClient;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Класс для извлечения данных из конфигурационного файла JIRA.
 * <p>
 * Этот класс загружает параметры подключения к JIRA из конфигурационного файла формата properties.
 * Если файл отсутствует, он автоматически создается с параметрами по умолчанию.
 * Конфигурационный файл имеет кодировку UTF-8 и содержит комментарии к каждому параметру.
 * </p>
 *
 * @version 1.0
 */
public class JiraConnect {
    /** URL JIRA. */
    private String jiraUrl;

    /** Имя пользователя JIRA. */
    private String jiraUsername;

    /** Токен API JIRA. */
    private String jiraApiToken;

    /** Имя конфигурационного файла. */
    private static final String CONFIG_FILE = "src/main/resources/config.properties";

    /**
     * Конструктор, который загружает настройки из конфигурационного файла.
     * Если файл отсутствует, он создается с параметрами по умолчанию.
     */
    public JiraConnect() {
        PropertiesConfiguration config = loadConfiguration();
        this.jiraUrl = config.getString("jira.url", "https://example.atlassian.net");
        this.jiraUsername = config.getString("jira.username", "defaultUsername");
        this.jiraApiToken = config.getString("jira.api.token", "defaultApiToken");
    }

    /**
     * Загружает конфигурационные параметры из файла.
     * <p>Если файл не существует, он создается с начальными значениями по умолчанию.</p>
     *
     * @return объект PropertiesConfiguration с параметрами из файла
     */
    private PropertiesConfiguration loadConfiguration() {
        File configFile = new File(CONFIG_FILE);
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                .configure(new Parameters().properties()
                        .setFile(configFile)
                        .setEncoding(StandardCharsets.UTF_8.name()));

        try {
            return builder.getConfiguration();
        } catch (ConfigurationException e) {
            if (!configFile.exists()) {
                createDefaultConfigFile(configFile);
            }
            try {
                return builder.getConfiguration();
            } catch (ConfigurationException ex) {
                System.out.println("Ошибка: Не удалось загрузить конфигурацию.");
                System.out.println("Error: Failed to load the configuration.");
                throw new RuntimeException("Не удалось загрузить конфигурацию", ex);
            }
        }
    }

    /**
     * Создает файл конфигурации с начальными значениями по умолчанию.
     *
     * @param configFile файл конфигурации
     */
    private void createDefaultConfigFile(File configFile) {
        try {
            if (configFile.getParentFile() != null) configFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(configFile, StandardCharsets.UTF_8)) {
                // Добавляем комментарии в файл конфигурации
                writer.write("# Конфигурационный файл JIRA\n");
                writer.write("# Configuration file for JIRA\n\n");

                writer.write("# URL JIRA инстанса\n");
                writer.write("# JIRA instance URL\n");
                writer.write("jira.url=https://example.atlassian.net\n\n");

                writer.write("# Имя пользователя для подключения к JIRA\n");
                writer.write("# Username for connecting to JIRA\n");
                writer.write("jira.username=defaultUsername\n\n");

                writer.write("# Токен API для доступа к JIRA\n");
                writer.write("# API token for accessing JIRA\n");
                writer.write("jira.api.token=defaultApiToken\n");
            }
            System.out.println("Создан новый конфигурационный файл по пути: " + configFile.getPath());
            System.out.println("A new configuration file was created at: " + configFile.getPath());
        } catch (IOException e) {
            System.out.println("Ошибка: Не удалось создать конфигурационный файл.");
            System.out.println("Error: Failed to create the configuration file.");
            throw new RuntimeException("Не удалось создать конфигурационный файл", e);
        }
    }

    /**
     * Возвращает URL JIRA, указанный в конфигурационном файле.
     *
     * @return URL JIRA
     */
    public String getJiraUrl() {
        return jiraUrl;
    }

    /**
     * Возвращает имя пользователя JIRA, указанное в конфигурационном файле.
     *
     * @return имя пользователя JIRA
     */
    public String getJiraUsername() {
        return jiraUsername;
    }

    /**
     * Возвращает токен API JIRA, указанный в конфигурационном файле.
     *
     * @return токен API JIRA
     */
    public String getJiraApiToken() {
        return jiraApiToken;
    }

}
