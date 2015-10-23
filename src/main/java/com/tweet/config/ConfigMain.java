package com.tweet.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.thetransactioncompany.cors.CORSFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.ConfigServerProperties;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

@Configuration
@EnableConfigServer
@SpringBootApplication
@EnableEurekaServer
@EnableConfigurationProperties(ConfigServerProperties.class)
public class ConfigMain {

    private final Log log = LogFactory.getLog(ConfigMain.class);

    @Value("${cassandra.host}")
    private String cassandraHost;
    @Value("${cassandra.keyspace}")
    private String keyspace;

    public static void main(String[] args) {
        SpringApplication.run(ConfigMain.class, args);
    }

    @Autowired
    private Session session;

    @PostConstruct
    public void init() throws IOException {
        String[] cqls = StreamUtils.copyToString(this.getClass().getClassLoader().getResourceAsStream("cassandra.cql"), Charset.defaultCharset()).split("\n");
        for (String cql : cqls) {
            try {
                session.execute(cql);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    @Bean
    public Cluster cassandraCluster() {
        return Cluster.builder().addContactPoint(cassandraHost).build();
    }

    @Bean
    public Session cassandraSession(Cluster cluster) {
        return cluster.connect(keyspace);
    }

    @Bean
    public CORSFilter corsFilter(){
        return new CORSFilter();
    }


}