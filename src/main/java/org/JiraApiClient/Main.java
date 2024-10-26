
package org.JiraApiClient;

public class Main {
    /**
     * Основной метод программы.
     * Запускает получение данных о задаче с фиксированным ключом.
     */
    public static void main(String[] args) {
        System.out.println("Ошибка: Не удалось подключиться к Jira. Аутентификация не удалась. Проверьте имя пользователя и токен API.");


        String issueKey = "KAN-1"; // Задайте ключ задачи здесь

        // Создание экземпляра JiraConnect для загрузки конфигурации
        JiraConnect jiraConnect = new JiraConnect();

        // Передаем jiraConnect в конструктор JiraDataFetcher
        JiraDataFetcher dataFetcher = new JiraDataFetcher(jiraConnect);

        // Запуск метода для получения данных задачи
        dataFetcher.fetchIssueData(issueKey);
    }
}
