package org.JiraApiClient;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Класс для извлечения данных из Jira.
 * <p>
 * Этот класс отвечает за подключение к Jira и извлечение информации о задачах по их ключам.
 * Он использует настройки подключения, такие как URL Jira, имя пользователя и токен API,
 * которые загружаются из экземпляра класса JiraConnect.
 * </p>
 *
 * <p><b>Обработка ошибок:</b></p>
 * <ul>
 *   <li>При возникновении ошибок подключения выводит сообщения на русском и английском языках.</li>
 *   <li>Сообщает о необходимости проверки файла конфигурации в случае неудачи.</li>
 * </ul>
 *
 * <p><b>Конфигурационный файл:</b></p>
 * <p>Файл конфигурации должен находиться по следующему пути: <code>src/main/resources/config.properties</code></p>
 *
 * @version 1.0
 */
public class JiraDataFetcher {
    private final String jiraUrl;
    private final String jiraUsername;
    private final String jiraApiToken;

    /**
     * Конструктор класса JiraDataFetcher, использующий конфигурацию из переданного экземпляра JiraConnect.
     *
     * @param jiraConnect экземпляр JiraConnect с загруженными параметрами конфигурации
     */
    public JiraDataFetcher(JiraConnect jiraConnect) {
        this.jiraUrl = jiraConnect.getJiraUrl();
        this.jiraUsername = jiraConnect.getJiraUsername();
        this.jiraApiToken = jiraConnect.getJiraApiToken();
    }

    /**
     * Извлекает данные задачи по ее ключу и сохраняет их в файл.
     *
     * @param issueKey Ключ задачи в Jira
     */
    public void fetchIssueData(String issueKey) {
        JiraRestClient client = null;
        try {
            // Создание URI для подключения
            URI uri = new URI(jiraUrl);
            client = new AsynchronousJiraRestClientFactory()
                    .createWithBasicHttpAuthentication(uri, jiraUsername, jiraApiToken);

            // Проверка подключения к Jira
            if (client.getSessionClient().getCurrentSession().claim() == null) {
                System.err.println("Ошибка: Не удалось подключиться к Jira. Проверьте параметры конфигурации.");
                System.err.println("Error: Failed to connect to Jira. Please check the configuration settings.");
                System.err.println("Пожалуйста, проверьте настройки файла конфигурации подключения.");
                System.err.println("Please check the connection configuration file settings.");
                return;
            }

            // Получение задачи
            Issue issue = client.getIssueClient().getIssue(issueKey).claim();
            String issueData = "Задача: " + issue.getSummary();

            // Сохранение текстовых данных в файл
            saveToFile(issueKey + ".txt", issueData);

            // Получение XML данных задачи и сохранение их в файл
            fetchIssueXml(issueKey);

        } catch (RestClientException e) {
            // Обработка ошибок аутентификации
            if (e.getStatusCode().isPresent() && e.getStatusCode().get() == 401) {
                System.err.println("Ошибка: Не удалось подключиться к Jira. " +
                        "Аутентификация не удалась. Проверьте имя пользователя и токен API.");
                System.err.println("Error: Failed to connect to Jira. Authentication failed. Please check the username and API token.");
                System.err.println("Пожалуйста, проверьте настройки файла конфигурации подключения.");
                System.err.println("Please check the connection configuration file settings.");
            } else {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
                System.err.println("Error retrieving data: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Ошибка при попытке подключения к Jira или при получении данных: " + e.getMessage());
            System.err.println("Error attempting to connect to Jira or retrieve data: " + e.getMessage());
            System.err.println("Пожалуйста, проверьте настройки файла конфигурации подключения.");
            System.err.println("Please check the connection configuration file settings.");
        } finally {
            // Закрытие клиента
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Ошибка при закрытии подключения к Jira: " + e.getMessage());
                    System.err.println("Error closing connection to Jira: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Извлекает XML-данные задачи по ее ключу и сохраняет их в файл.
     *
     * @param issueKey Ключ задачи в Jira
     */
    private void fetchIssueXml(String issueKey) {
        String xmlUrl = jiraUrl + "/si/jira.issueviews:issue-xml/" + issueKey + "/" + issueKey + ".xml";

        try {
            URI uri = new URI(xmlUrl);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " +
                    java.util.Base64.getEncoder().encodeToString((jiraUsername + ":" + jiraApiToken).getBytes(StandardCharsets.UTF_8)));

            if (connection.getResponseCode() == 200) {
                StringBuilder xmlData = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        xmlData.append(inputLine);
                    }
                }

                String fileName = issueKey + "_details.xml";
                saveToFile(fileName, xmlData.toString());
                System.out.println("XML успешно получен и сохранен по пути: " + fileName);
                System.out.println("XML successfully retrieved and saved at: " + fileName);
            } else {
                System.err.println("Ошибка при получении XML данных: " + connection.getResponseCode());
                System.err.println("Error retrieving XML data: " + connection.getResponseCode());
            }

        } catch (IOException | URISyntaxException e) {
            System.err.println("Ошибка при получении XML данных: " + e.getMessage());
            System.err.println("Error retrieving XML data: " + e.getMessage());
        }
    }

    /**
     * Сохраняет строку в файл.
     *
     * @param filename Имя файла
     * @param data     Данные для записи
     */
    private void saveToFile(String filename, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8))) {
            writer.write(data);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + filename);
            System.err.println("Error saving file: " + filename);
            e.printStackTrace();
        }
    }
}
