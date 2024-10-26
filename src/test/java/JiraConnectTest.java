package org.JiraApiClient;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.atlassian.util.concurrent.Promises.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JiraConnectTest {

    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private JiraConnect jiraConnect;
    private File configFile;

    @BeforeEach
    void setUp() {
        configFile = new File(CONFIG_FILE);
        jiraConnect = new JiraConnect();
    }

    @AfterEach
    void tearDown() {
        if (configFile.exists()) {
            configFile.delete();
        }
    }

    @Test
    void testConfigFileCreation() {
        assertTrue(configFile.exists(), "Файл конфигурации должен быть создан.");
    }

    @Test
    void testDefaultProperties() {
        assertEquals("https://example.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("defaultUsername", jiraConnect.getJiraUsername());
        assertEquals("defaultApiToken", jiraConnect.getJiraApiToken());
    }

    @Test
    void testLoadConfiguration() throws ConfigurationException {
        PropertiesConfiguration mockConfig = Mockito.mock(PropertiesConfiguration.class);
        FileBasedConfigurationBuilder<PropertiesConfiguration> mockBuilder = Mockito.mock(FileBasedConfigurationBuilder.class);

        when(mockBuilder.getConfiguration()).thenReturn(mockConfig);
        when(mockConfig.getString("jira.url", "default")).thenReturn("https://mocked-url.atlassian.net");
        when(mockConfig.getString("jira.username", "default")).thenReturn("mockedUsername");
        when(mockConfig.getString("jira.api.token", "default")).thenReturn("mockedToken");

        jiraConnect = new JiraConnect() {
            @Override
            protected PropertiesConfiguration loadConfiguration() {
                return mockConfig;
            }
        };

        assertEquals("https://mocked-url.atlassian.net", jiraConnect.getJiraUrl());
        assertEquals("mockedUsername", jiraConnect.getJiraUsername());
        assertEquals("mockedToken", jiraConnect.getJiraApiToken());
    }

    @Test
    void testConfigurationFileExists() throws IOException {
        jiraConnect = new JiraConnect();
        assertTrue(configFile.exists(), "Файл конфигурации должен существовать после создания экземпляра JiraConnect");
    }
}
