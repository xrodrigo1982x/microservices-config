package com.tweet.config;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnvironmentRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class CassandraRepository implements EnvironmentRepository {

    @Autowired
    private Session session;
    private PreparedStatement stm;

    @PostConstruct
    public void init() {
        stm = session.prepare("SELECT PROPERTY, VALUE FROM CONFIG WHERE APPLICATION IN ('*', ?) AND PROFILE = ? AND LABEL = ?");
    }

    @Override
    public String getDefaultLabel() {
        return "master";
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        Environment env = new Environment(application, profile);
        env.setLabel(label);
        Map<String, String> props = new HashMap<>();
        session.execute(new BoundStatement(stm).bind(application, profile, label)).all().forEach(r -> props.put(r.getString("PROPERTY"), r.getString("VALUE")));
        env.add(new PropertySource(application + "-" + profile, props));
        return env;
    }

}
