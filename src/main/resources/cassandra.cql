create table if not exists configserver.config (application text, profile text, label text, property text, value text, primary key (application, profile, label, property));
truncate configserver.config;
update configserver.config set value = 'oui214hmui23o4hm1pui3o2hp4m1o3h2m1o43' where application = '*' and profile = 'default' and label = 'master' and property = 'signing-key';
create keyspace if not exists user WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  and durable_writes = true;
create table if not exists user.user (username text, name text, primary key (username));
