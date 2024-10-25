package org.JiraApiClient;

public class Main {

    /**
     * Основной метод программы.
     * Запускает получение данных о задаче с фиксированным ключом.
     */
    public static void main(String[] args) {
        String issueKey = "KAN-1"; // Задайте ключ задачи здесь

        // Создание экземпляра JiraDataFetcher
        JiraDataFetcher dataFetcher = new JiraDataFetcher();
        dataFetcher.fetchIssueData(issueKey); // Запускаем метод для получения данных
    }
}
